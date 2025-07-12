package io.vepo.morphoboard.workflow;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
class WorkflowResourceTest {

    @Test
    @Order(1)
    void listEmptyWorkflowTest() {
        given().when()
               .get("/api/workflows")
               .then()
               .statusCode(200)
               .body("$.size()", greaterThanOrEqualTo(0));
    }

    @Test
    @Order(2)
    void createWorkflowTest() {
        given().when()
               .contentType("application/json")
               .body("""
                     {
                         "name": "Test Workflow",
                         "statuses": ["Status 1", "Status 2"],
                         "start": "Status 1",
                         "transitions": [{"from": "Status 1", "to": "Status 2"}]
                     }""")
               .post("/api/workflows")
               .then()
               .statusCode(201)
               .body("name", is("Test Workflow"))
               .body("statuses.size()", is(2))
               .body("statuses[0]", is("Status 1"))
               .body("statuses[1]", is("Status 2"))
               .body("start", is("Status 1"))
               .body("transitions.size()", is(1))
               .body("transitions[0].from", is("Status 1"))
               .body("transitions[0].to", is("Status 2"));
    }

    @Test
    @Order(3)
    void listWorkflowTest() {
        given().when()
               .get("/api/workflows")
               .then()
               .statusCode(200)
               .body("$.size()", greaterThanOrEqualTo(1)) // Check that there is one workflow
               .body("find { it.name == 'Test Workflow' }.name", is("Test Workflow"))
               .body("find { it.name == 'Test Workflow' }.statuses.size()", is(2))
               .body("find { it.name == 'Test Workflow' }.statuses[0]", is("Status 1"))
               .body("find { it.name == 'Test Workflow' }.statuses[1]", is("Status 2"))
               .body("find { it.name == 'Test Workflow' }.start", is("Status 1"))
               .body("find { it.name == 'Test Workflow' }.transitions.size()", is(1))
               .body("find { it.name == 'Test Workflow' }.transitions[0].from", is("Status 1"))
               .body("find { it.name == 'Test Workflow' }.transitions[0].to", is("Status 2"));
    }
}