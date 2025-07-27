package dev.vepo.morphoboard.project;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import dev.vepo.morphoboard.Given;
import dev.vepo.morphoboard.workflow.WorkflowResource.WorkflowResponse;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.http.Header;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
class ProjectEndpointTest {

    private WorkflowResponse workflow;
    private Header userAuthenticatedHeader;
    private Header pmAuthenticatedHeader;

    @BeforeEach
    void setup() {
        this.workflow = Given.simpleWorkflow();
        this.userAuthenticatedHeader = Given.authenticatedUser();
        this.pmAuthenticatedHeader = Given.authenticatedProjectManager();
    }

    @Test
    @Order(1)
    @DisplayName("No authenticated user should be able to list projects")
    void noAuthenticatedUserShouldListProjectsTest() {
        given().when()
               .accept(ContentType.JSON)
               .get("/api/projects")
               .then()
               .statusCode(401);
    }

    @Test
    @Order(2)
    @DisplayName("Only authenticated users should be able to list projects")
    void onlyAuthenticatedUsersShouldListProjectsTest() {
        given().header(userAuthenticatedHeader)
               .accept(ContentType.JSON)
               .when()
               .get("/api/projects")
               .then()
               .statusCode(200)
               .body("$.size()", greaterThan(0));
    }

    @Test
    @Order(3)
    @DisplayName("No user should be allowed to create a project")
    void noUserShouldBeAllowedToCreateProjectTest() {
        given().header(userAuthenticatedHeader)
               .accept(ContentType.JSON)
               .when()
               .contentType(ContentType.JSON)
               .body(String.format("""
                                   {
                                       "name": "Test Project",
                                       "description": "This is a test project.",
                                       "workflowId": %d
                                   }""", workflow.id()))
               .post("/api/projects")
               .then()
               .statusCode(403);
    }

    @Test
    @Order(4)
    @DisplayName("Only project managers should be allowed to create projects")
    void onlyProjectManagerShouldBeAllowedToCreateProjectsTest() {
        given().header(pmAuthenticatedHeader)
               .accept(ContentType.JSON)
               .when()
               .contentType(ContentType.JSON)
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
               .body("description", is("This is a test project."))
               .body("workflow.id", is((int) workflow.id()))
               .body("workflow.name", is(workflow.name()));
    }

    @Test
    @Order(5)
    @DisplayName("Project listing should return the created project")
    void anyoneCanListProjects() {
        Stream.of(userAuthenticatedHeader, pmAuthenticatedHeader)
              .forEach(header -> given().header(header)
                                        .accept(ContentType.JSON)
                                        .when()
                                        .get("/api/projects")
                                        .then()
                                        .statusCode(200)
                                        .body("$.size()", greaterThan(1))
                                        .body("find { it.name == 'Test Project' }.name", is("Test Project"))
                                        .body("find { it.name == 'Test Project' }.description", is("This is a test project."))
                                        .body("find { it.name == 'Test Project' }.workflow.id", is((int) workflow.id()))
                                        .body("find { it.name == 'Test Project' }.workflow.name", is(workflow.name())));
    }

    @Test
    @Order(6)
    @DisplayName("Project name is a required field")
    void projectNameIsRequiredTest() {
        given().header(pmAuthenticatedHeader)
               .accept(ContentType.JSON)
               .when()
               .contentType(ContentType.JSON)
               .body(String.format("""
                                   {
                                       "description": "This is a test project.",
                                       "workflowId": 1
                                   }""", workflow.id()))
               .post("/api/projects")
               .then()
               .statusCode(400)
               .body("violations[0].message", is("Project name cannot be empty"));
    }

    @Test
    @Order(7)
    @DisplayName("Workflow ID must be provided and must exist")
    void workflowIdMustExistTest() {
        given().header(pmAuthenticatedHeader)
               .accept(ContentType.JSON)
               .when()
               .contentType(ContentType.JSON)
               .body("""
                     {
                         "name": "Test Project",
                         "description": "This is a test project."
                     }""")
               .post("/api/projects")
               .then()
               .statusCode(400)
               .body("violations[0].message", is("Workflow ID must be provided"));

        given().header(pmAuthenticatedHeader)
               .accept(ContentType.JSON)
               .when()
               .contentType(ContentType.JSON)
               .body("""
                     {
                         "name": "Test Project",
                         "description": "This is a test project.",
                         "workflowId": 9999
                     }""")
               .post("/api/projects")
               .then()
               .statusCode(400)
               .body("message", is("Workflow with ID 9999 does not exist"));
    }
}
