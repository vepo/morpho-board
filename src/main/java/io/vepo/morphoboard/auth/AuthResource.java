package io.vepo.morphoboard.auth;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.jwt.build.Jwt;
import io.vepo.morphoboard.user.User;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    private static final Logger logger = LoggerFactory.getLogger(AuthResource.class);

    @POST
    @Path("/login")
    public Response login(LoginRequest req) {
        // Exemplo: buscar usuário no banco (ajuste para seu modelo real)
        var user = User.<User>find("email", req.email).firstResult();
        if (user == null || !user.password.equals(req.password)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(Map.of("error", "Credenciais inválidas")).build();
        }
        String token = Jwt.issuer("morphoboard")
                          .upn(user.email)
                          .claim("id", user.id)
                          .claim("email", user.email)
                          .groups(Set.of("user"))
                          .sign();
        return Response.ok(Map.of("token", token)).build();
    }

    @GET
    @Path("/me")
    @RolesAllowed("user")
    public Response me(@Context SecurityContext ctx) {
        logger.info("Resquting user information! {}", ctx);
        String email = ctx.getUserPrincipal().getName();
        var user = User.find("email", email).firstResult();
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(user).build();
    }

    public static class LoginRequest {
        public String email;
        public String password;
    }
} 