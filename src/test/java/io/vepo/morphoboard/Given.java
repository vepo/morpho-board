package dev.vepo.morphoboard;

import static io.restassured.RestAssured.given;

import java.util.stream.Stream;

import dev.vepo.morphoboard.workflow.WorkflowResource.WorkflowResponse;

public class Given {
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
}
