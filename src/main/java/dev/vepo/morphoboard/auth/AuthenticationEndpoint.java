package dev.vepo.morphoboard.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import dev.vepo.morphoboard.user.Role;
import dev.vepo.morphoboard.user.UserRepository;
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
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    @Inject
    public AuthenticationEndpoint(PasswordEncoder passwordEncoder,
                                  UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @POST
    @Path("/login")
    public LoginResponse login(@Valid LoginRequest request) {
        return this.userRepository.findByEmail(request.email())
                                  .filter(u -> passwordEncoder.matches(request.password(), u.getEncodedPassword()))
                                  .map(user -> {
                                      Instant now = Instant.now();
                                      return new LoginResponse(Jwt.issuer("https://morpho-board.vepo.dev")
                                                                  .upn(user.getEmail())
                                                                  .claim("id", user.getId())
                                                                  .claim("email", user.getEmail())
                                                                  .groups(user.getRoles().stream()
                                                                              .map(Role::role)
                                                                              .collect(Collectors.toSet()))
                                                                  .issuedAt(now)
                                                                  .expiresAt(now.plus(1, ChronoUnit.DAYS))
                                                                  .sign());
                                  })
                                  .orElseThrow(() -> new NotAuthorizedException("Invalid credentials!", request));
    }

    @GET
    @Path("/me")
    @RolesAllowed({ Role.USER_ROLE, Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public AuthResponse me(@Context SecurityContext ctx) {
        return userRepository.findByEmail(ctx.getUserPrincipal().getName())
                             .map(AuthResponse::load)
                             .orElseThrow(() -> new NotFoundException("User not found!"));
    }
}