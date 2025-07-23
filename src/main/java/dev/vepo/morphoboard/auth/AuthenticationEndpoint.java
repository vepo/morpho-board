package dev.vepo.morphoboard.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.vepo.morphoboard.user.Role;
import dev.vepo.morphoboard.user.User;
import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/auth") 
@Produces(MediaType.APPLICATION_JSON) 
@Consumes(MediaType.APPLICATION_JSON)
public class AuthenticationEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationEndpoint.class);


    @Inject
    PasswordEncoder passwordEncoder;

    @POST
    @Path("/login")
    public LoginResponse login(@Valid @Parameter(name = "request") LoginRequest req) {
        var user = User.<User>find("email", req.email()).firstResult();
        if (user == null || !passwordEncoder.matches(req.password(), user.encodedPassword) ) {
            throw new NotAuthorizedException("Invalid credentials!", req);
        }
        Instant now = Instant.now();
        return new LoginResponse(Jwt.issuer("https://morpho-board.vepo.dev")
                                    .upn(user.email)
                                    .claim("id", user.id)
                                    .claim("email", user.email)
                                    .groups(user.roles.stream().map(Role::role).collect(Collectors.toSet()))
                                    .issuedAt(now)
                                    .expiresAt(now.plus(1, ChronoUnit.DAYS))
                                    .sign());
    }

    @GET
    @Path("/me")
    @RolesAllowed({ Role.USER_ROLE, Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public AuthResponse me(@Context SecurityContext ctx) {
        logger.info("Resquting user information! {}", ctx);
        String email = ctx.getUserPrincipal().getName();
        var user = User.<User>find("email", email).firstResult();
        if (Objects.isNull(user)) {
            throw new NotFoundException("User not found!");
        }
        return AuthResponse.load(user);
    }
}