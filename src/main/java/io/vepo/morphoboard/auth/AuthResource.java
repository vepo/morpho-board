package io.vepo.morphoboard.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.jwt.build.Jwt;
import io.vepo.morphoboard.user.User;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/auth")
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
        Instant now = Instant.now();
        String token = Jwt.issuer("https://morpho-board.vepo.dev")
                          .upn(user.email)
                          .claim("id", user.id)
                          .claim("email", user.email)
                          .groups(user.roles.stream().map(Enum::name).collect(Collectors.toSet()))
                          .issuedAt(now)
                          .expiresAt(now.plus(1, ChronoUnit.DAYS))
                          .sign();
        return Response.ok(Map.of("token", token)).build();
    }

    public static record AuthResponse(long id, String email, String username) {}

    @GET
    @Path("/me")
    @RolesAllowed("user")
    public AuthResponse me(@Context SecurityContext ctx) {
        logger.info("Resquting user information! {}", ctx);
        String email = ctx.getUserPrincipal().getName();
        var user = User.<User>find("email", email).firstResult();
        if (Objects.isNull(user)) {
            throw new NotFoundException("User not found!");
        }
        return toResponse(user);
    }

    private AuthResponse toResponse(User user) {
        return new AuthResponse(user.id, user.email, user.name);
    }

    public static class LoginRequest {
        public String email;
        public String password;
    }
} 