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
                         "stages": ["Stage 1", "Stage 2"],
                         "start": "Stage 1",
                         "transitions": [{"from": "Stage 1", "to": "Stage 2"}]
                     }""")
               .post("/api/workflows")
               .then()
               .statusCode(201)
               .body("name", is("Test Workflow"))
               .body("stages.size()", is(2))
               .body("stages[0]", is("Stage 1"))
               .body("stages[1]", is("Stage 2"))
               .body("start", is("Stage 1"))
               .body("transitions.size()", is(1))
               .body("transitions[0].from", is("Stage 1"))
               .body("transitions[0].to", is("Stage 2"));
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
               .body("find { it.name == 'Test Workflow' }.stages.size()", is(2))
               .body("find { it.name == 'Test Workflow' }.stages[0]", is("Stage 1"))
               .body("find { it.name == 'Test Workflow' }.stages[1]", is("Stage 2"))
               .body("find { it.name == 'Test Workflow' }.start", is("Stage 1"))
               .body("find { it.name == 'Test Workflow' }.transitions.size()", is(1))
               .body("find { it.name == 'Test Workflow' }.transitions[0].from", is("Stage 1"))
               .body("find { it.name == 'Test Workflow' }.transitions[0].to", is("Stage 2"));
    }
}