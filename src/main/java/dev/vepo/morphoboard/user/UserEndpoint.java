package dev.vepo.morphoboard.user;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("users")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(UserEndpoint.class);

    private final UserRepository userRepository;

    @Inject
    public UserEndpoint(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GET
    @Path("search")
    public List<UserResponse> search(@QueryParam("name") String name,
                                     @QueryParam("email") String email,
                                     @QueryParam("roles") List<String> roles) {
        logger.info("Searching for used! name={} email={} roles={}", name, email, roles);
        return userRepository.search(name, email, roles.stream().map(role -> Role.from(role)
                                                                                 .orElseThrow(() -> new BadRequestException("Role does not exist! role=%s".formatted(role))))
                                                       .toList())
                             .map(UserResponse::load)
                             .toList();
    }

}
