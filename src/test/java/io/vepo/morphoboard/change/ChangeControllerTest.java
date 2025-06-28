package io.vepo.morphoboard.change;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Para ordenar os testes
public class ChangeControllerTest {

    private static Number changeId; // Armazena o ID criado no teste POST

    @Test
    @Order(1)
    public void testCreateChange() {
        Change change = new Change();
        change.title = "Teste de Mudança";
        change.description = "Descrição do teste";
        change.status = "TO_DO";

        changeId = given().contentType(ContentType.JSON)
                .body(change)
                .when()
                .post("/api/changes")
                .then()
                .statusCode(201)
                .body("title", is("Teste de Mudança"))
                .body("id", notNullValue())
                .body("status", is("TO_DO"))
                .body("description", is("Descrição do teste"))
                .extract()
                .path("id"); // Extrai o ID da mudança criada
    }

    @Test
    @Order(2)
    public void testGetAllChanges() {
        given().when()
                .get("/api/changes")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1));
    }

    // @Test
    // @Order(3)
    // public void testGetChangeById() {
    //     given().pathParam("id", changeId)
    //             .when()
    //             .get("/api/changes/{id}")
    //             .then()
    //             .statusCode(200)
    //             .body("title", is("Teste de Mudança"));
    // }

    // @Test
    // @Order(4)
    // public void testUpdateChangeStatus() {
    //     given().pathParam("id", changeId)
    //             .contentType(ContentType.JSON)
    //             .body("{\"status\": \"IN_PROGRESS\"}")
    //             .when()
    //             .patch("/api/changes/{id}")
    //             .then()
    //             .statusCode(200)
    //             .body("status", is("IN_PROGRESS"));
    // }

    // @Test
    // @Order(5)
    // public void testDeleteChange() {
    //     given().pathParam("id", changeId)
    //             .when()
    //             .delete("/api/changes/{id}")
    //             .then()
    //             .statusCode(204);
    // }
}