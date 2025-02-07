package com.officemanagement.resource;

import com.officemanagement.model.Floor;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class FloorResourceTest extends BaseResourceTest {

    @Test
    public void testCreateFloor() {
        Floor floor = new Floor();
        floor.setName("First Floor");
        floor.setFloorNumber(1);
        floor.setCreatedAt(LocalDateTime.now());

        given()
            .contentType(ContentType.JSON)
            .body(floor)
        .when()
            .post(getApiPath("/floors"))
        .then()
            .statusCode(Response.Status.CREATED.getStatusCode())
            .body("id", notNullValue())
            .body("name", equalTo("First Floor"))
            .body("floorNumber", equalTo(1));
    }

    @Test
    public void testGetAllFloors() {
        // Create test floors
        createTestFloor("Ground Floor", 0);
        createTestFloor("First Floor", 1);
        createTestFloor("Second Floor", 2);

        given()
        .when()
            .get(getApiPath("/floors"))
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .body("$", hasSize(greaterThanOrEqualTo(3)))
            .body("find { it.name == 'Ground Floor' }.floorNumber", equalTo(0))
            .body("find { it.name == 'First Floor' }.floorNumber", equalTo(1))
            .body("find { it.name == 'Second Floor' }.floorNumber", equalTo(2));
    }

    @Test
    public void testGetFloor() {
        // First create a floor
        Floor floor = new Floor();
        floor.setName("Test Floor");
        floor.setFloorNumber(3);
        floor.setCreatedAt(LocalDateTime.now());

        Integer floorIdInt = given()
            .contentType(ContentType.JSON)
            .body(floor)
        .when()
            .post(getApiPath("/floors"))
        .then()
            .statusCode(Response.Status.CREATED.getStatusCode())
            .extract()
            .path("id");
            
        Long floorId = floorIdInt.longValue();

        // Then get the floor
        given()
        .when()
            .get(getApiPath("/floors/" + floorId))
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .body("name", equalTo("Test Floor"))
            .body("floorNumber", equalTo(3))
            .body("rooms", notNullValue());
    }

    @Test
    public void testUpdateFloor() {
        // First create a floor
        Floor floor = new Floor();
        floor.setName("Original Name");
        floor.setFloorNumber(4);
        floor.setCreatedAt(LocalDateTime.now());

        Integer floorIdInt = given()
            .contentType(ContentType.JSON)
            .body(floor)
        .when()
            .post(getApiPath("/floors"))
        .then()
            .statusCode(Response.Status.CREATED.getStatusCode())
            .extract()
            .path("id");
            
        Long floorId = floorIdInt.longValue();

        // Update the floor
        floor.setName("Updated Name");
        
        given()
            .contentType(ContentType.JSON)
            .body(floor)
        .when()
            .put(getApiPath("/floors/" + floorId))
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .body("name", equalTo("Updated Name"))
            .body("floorNumber", equalTo(4));
    }

    @Test
    public void testDeleteFloor() {
        // First create a floor
        Floor floor = new Floor();
        floor.setName("Floor to Delete");
        floor.setFloorNumber(5);
        floor.setCreatedAt(LocalDateTime.now());

        Integer floorIdInt = given()
            .contentType(ContentType.JSON)
            .body(floor)
        .when()
            .post(getApiPath("/floors"))
        .then()
            .statusCode(Response.Status.CREATED.getStatusCode())
            .extract()
            .path("id");
            
        Long floorId = floorIdInt.longValue();

        // Delete the floor
        given()
        .when()
            .delete(getApiPath("/floors/" + floorId))
        .then()
            .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        // Verify floor is deleted
        given()
        .when()
            .get(getApiPath("/floors/" + floorId))
        .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    private void createTestFloor(String name, int floorNumber) {
        Floor floor = new Floor();
        floor.setName(name);
        floor.setFloorNumber(floorNumber);
        floor.setCreatedAt(LocalDateTime.now());

        given()
            .contentType(ContentType.JSON)
            .body(floor)
        .when()
            .post(getApiPath("/floors"))
        .then()
            .statusCode(Response.Status.CREATED.getStatusCode());
    }
} 