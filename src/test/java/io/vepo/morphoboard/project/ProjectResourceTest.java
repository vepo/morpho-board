package dev.vepo.morphoboard.project;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;
import dev.vepo.morphoboard.Given;
import dev.vepo.morphoboard.workflow.WorkflowResource.WorkflowResponse;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
class ProjectResourceTest {

    private WorkflowResponse workflow;

    @BeforeEach
    void setup() {
        this.workflow = Given.simpleWorkflow();
    }

    @Test
    @Order(1)
    void listNoProjectTest() {
        given().when()
               .get("/api/projects")
               .then()
               .statusCode(200)
               .body("$.size()", is(0));
    }

    @Test
    @Order(2)
    void createProjectTest() {
        given().when()
               .contentType("application/json")
               .body(String.format("""
                                   {
                                       "name": "Test Project",
                                       "description": "This is a test project.",
                                       "workflowId": %d
                                   }""", workflow.id()))
               .post("/api/projects")
               .then()
               .statusCode(201)
               .body("name", is("Test Project"))
               .body("description", is("This is a test project."));
    }

    @Test
    @Order(3)
    void listProjectTest() {
        given().when()
               .get("/api/projects")
               .then()
               .statusCode(200)
               .body("$.size()", is(1))
               .body("[0].name", is("Test Project"))
               .body("[0].description", is("This is a test project."));
    }
}
