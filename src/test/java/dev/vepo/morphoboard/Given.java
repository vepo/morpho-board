package dev.vepo.morphoboard;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Set;
import java.util.stream.Stream;

import dev.vepo.morphoboard.auth.LoginResponse;
import dev.vepo.morphoboard.auth.PasswordEncoder;
import dev.vepo.morphoboard.user.Role;
import dev.vepo.morphoboard.user.User;
import dev.vepo.morphoboard.workflow.WorkflowResource.WorkflowResponse;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.restassured.http.Header;
import jakarta.enterprise.inject.spi.CDI;

public class Given {

    public static Header authenticatedUser() {
        ensureUser("User", "user@morpho-board.vepo.dev", Set.of(Role.USER));
        var response = given().when()
                              .contentType("application/json")
                              .body("""
                                    {
                                        "email": "user@morpho-board.vepo.dev",
                                        "password": "password"
                                    }
                                    """)
                              .post("/api/auth/login")
                              .then()
                              .statusCode(200)
                              .extract()
                              .as(LoginResponse.class);
        return new Header("Authorization", "Bearer " + response.token());
    }

    public static User randomUser() {
        var id = System.currentTimeMillis();
        String email = "user" + id + "@morpho-board.vepo.dev";
        ensureUser("Random User " + id, email, Set.of(Role.USER));
        return User.find("email", email).firstResult();
    }

    public static Header authenticatedProjectManager() {
        ensureUser("PM", "pm@morpho-board.vepo.dev", Set.of(Role.PROJECT_MANAGER));
        var response = given().when()
                              .contentType("application/json")
                              .body("""
                                    {
                                        "email": "pm@morpho-board.vepo.dev",
                                        "password": "password"
                                    }
                                    """)
                              .post("/api/auth/login")
                              .then()
                              .statusCode(200)
                              .extract()
                              .as(LoginResponse.class);
        return new Header("Authorization", "Bearer " + response.token());
    }

    public static WorkflowResponse simpleWorkflow() {
        // try get the value before creating it
        WorkflowResponse[] existingWorkflow = given().when()
                                                     .get("/api/workflows")
                                                     .then()
                                                     .statusCode(200)
                                                     .extract()
                                                     .as(WorkflowResponse[].class);
        if (existingWorkflow.length > 0 && Stream.of(existingWorkflow)
                                                 .anyMatch(w -> "Project".equals(w.name()))) {
            return Stream.of(existingWorkflow)
                         .filter(w -> "Project".equals(w.name()))
                         .findFirst()
                         .orElseThrow();

        }
        return given().when()
                      .contentType("application/json")
                      .body("""
                            {
                                "name": "Project",
                                "statuses": ["TODO", "In Progress", "Blocked", "Done"],
                                "start": "TODO",
                                "transitions": [
                                    {"from": "TODO", "to": "In Progress"},
                                    {"from": "In Progress", "to": "Blocked"},
                                    {"from": "Blocked", "to": "In Progress"},
                                    {"from": "In Progress", "to": "Done"}
                                ]
                            }""")
                      .post("/api/workflows")
                      .then()
                      .statusCode(201)
                      .extract()
                      .as(WorkflowResponse.class);

    }

    private static void ensureUser(String name, String email, Set<Role> roles) {
        if (!User.find("email", email)
                 .firstResultOptional()
                 .isPresent()) {
            try {
                var encoder = CDI.current().select(PasswordEncoder.class).get();
                QuarkusTransaction.begin();
                new User(name, email, encoder.hashPassword("password"), roles).persist();
                QuarkusTransaction.commit();
            } catch (Exception e) {
                QuarkusTransaction.rollback();
                fail("Fail to create user!");
            }
        }
    }
}
