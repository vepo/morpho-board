package dev.vepo.morphoboard.ticket;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import dev.vepo.morphoboard.Given;
import dev.vepo.morphoboard.project.ProjectResponse;
import dev.vepo.morphoboard.workflow.StatusResource.StatusResponse;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.http.Header;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
class TicketEndpointTest {
    private ProjectResponse project;  
    private Header userAuthenticatedHeader;
    private Header pmAuthenticatedHeader;
    private TicketResponse ticket;
    private List<StatusResponse> allStatuses;

    @BeforeEach
    void setup() {
        this.project = Given.simpleProject();
        this.ticket = Given.simpleTicket(this.project.id());
        this.userAuthenticatedHeader = Given.authenticatedUser();
        this.pmAuthenticatedHeader = Given.authenticatedProjectManager();
        this.allStatuses = Given.allStatuses();
    }

    @Test
    @Order(1)
    @DisplayName("No authenticated user should be able to list tickets")
    void noAuthenticatedUserShouldListTicketsTest() {
        given().when()
               .accept(ContentType.JSON)
               .get("/api/tickets")
               .then()
               .statusCode(401);
    }

    @Test
    @Order(2)
    @DisplayName("Only authenticated users should be able to list tickets")
    void onlyAuthenticatedUsersShouldListTicketsTest() {
        given().header(userAuthenticatedHeader)
               .accept(ContentType.JSON)
               .when()
               .get("/api/tickets")
               .then()
               .statusCode(200)
               .body("$.size()", greaterThan(0));    
    }

    @Test
    @Order(3)
    @DisplayName("It should be possible to list tickets by status")
    void shouldListTicketsByStatusTest() {
        var todo = allStatuses.stream()
                              .filter(status -> status.name().equals("TODO"))
                              .findFirst()
                              .orElseThrow(() -> new IllegalStateException("TODO status not found"));
        given().header(pmAuthenticatedHeader)
               .accept(ContentType.JSON)
               .when()
               .get("/api/tickets?status=TODO")
               .then()
               .statusCode(200)
               .body("$.size()", greaterThan(0))
               .body("[0].status", equalTo((int) todo.id()));
        given().header(pmAuthenticatedHeader)
               .accept(ContentType.JSON)
               .when()
               .get("/api/tickets?status=" + todo.id())
               .then()
               .statusCode(200)
               .body("$.size()", greaterThan(0))
               .body("[0].status", equalTo((int) todo.id()));
    }

    @Test
    @Order(4)
    @DisplayName("It should be possible to search tickets by term on title or description")
    void shouldSearchTicketsByTermTest() throws UnsupportedEncodingException {
        given().header(pmAuthenticatedHeader)
               .accept(ContentType.JSON)
               .when()
               .get("/api/tickets/search?term=" + ticket.title())
               .then()
               .statusCode(200)
               .body("$.size()", greaterThan(0))
               .body("[0].title", equalTo(ticket.title()));
        given().header(pmAuthenticatedHeader)
               .accept(ContentType.JSON)
               .when()
               .get("/api/tickets/search?term=" + ticket.description())
               .then()
               .statusCode(200)
               .body("$.size()", greaterThan(0))
               .body("[0].description", equalTo(ticket.description()));
    }

    @Test
    @Order(5)
    @DisplayName("It should be possible to retrieve a ticket by its ID")
    void shouldRetrieveTicketByIdTest() {
        given().header(pmAuthenticatedHeader)
               .accept(ContentType.JSON)
               .when()
               .get("/api/tickets/{id}", ticket.id())
               .then()
               .statusCode(200)
               .body("id", equalTo((int) ticket.id()))
               .body("title", equalTo(ticket.title()))
               .body("description", equalTo(ticket.description()))
               .body("status", equalTo((int) ticket.status()))
               .body("project", equalTo((int) ticket.project()))
               .body("category", equalTo((int) ticket.category()))
               .body("author", equalTo((int) ticket.author()));
    }

    @Test
    @Order(6)
    @DisplayName("It should be possible to create a new ticket")
    void shouldCreateNewTicketTest() throws UnsupportedEncodingException {
        given().header(pmAuthenticatedHeader)
               .contentType(ContentType.JSON)
               .accept(ContentType.JSON)
               .when()
               .body(String.format("""
                                   {
                                       "title": "New Ticket",
                                       "description": "This is a new ticket.",
                                       "projectId": %d,
                                       "categoryId": %d
                                   }""",
                                   project.id(), 1))
               .post("/api/tickets")
               .then()
               .statusCode(201)
               .body("title", equalTo("New Ticket"))
               .body("description", equalTo("This is a new ticket."))
               .body("project", equalTo((int) project.id()))
               .body("category", equalTo(1))
               .body("author", equalTo((int) Given.userIdByEmail("pm@morpho-board.vepo.dev")))
               .body("status", equalTo((int) allStatuses.stream()
                                                          .filter(status -> status.name().equals("TODO"))
                                                          .findFirst()
                                                          .orElseThrow(() -> new IllegalStateException("TODO status not found")).id()));
    } 
}
