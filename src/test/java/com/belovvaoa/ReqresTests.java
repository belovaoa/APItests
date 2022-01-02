package com.belovvaoa;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ReqresTests {

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "https://reqres.in/";
    }

    @Test
    @DisplayName("POST, Auth email + password, statusCode 200 and token")
    void successfulLoginTest() {

        String data = "{ \"email\": \"eve.holt@reqres.in\", \"password\": \"cityslicka\" }";
        given()
                .contentType(JSON)
                .body(data)
                .when()
                .post("/api/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue()); //  .body("token", is("QpwL5tke4Pnpja7X4"))
    }

    @Test
    @DisplayName("GET, list users")
    void getListUsersTest() {
        given()
                .when()
                .get("/api/users?page=2")
                .then().log().all()
                .statusCode(200)
                .body("page", is(2),
                        "per_page", is(6),
                        "total", is(12),
                        "total_pages", is(2),
                        "data[0].id", is(7),
                        "data[0].email", is("michael.lawson@reqres.in"),
                        "support.text", is("To keep ReqRes free, "
                                + "contributions towards server costs are appreciated!"));
    }

    @Test
    @DisplayName("Get, list users 2")
    void getListUsersTest2() {
        Response response = get("/api/users?page=2")
                            .then()
                            .statusCode(200)
                            .extract().response();
        assertThat((Integer) response.path("page")).isEqualTo(2);
        assertThat((Integer) response.path("per_page")).isEqualTo(6);
        assertThat((Integer) response.path("total")).isEqualTo(12);
        assertThat(response.path("data[0].id").toString()).isEqualTo("7");
        assertThat(response.path("data[0].email").toString()).isEqualTo("michael.lawson@reqres.in");
        assertThat(response.path("support.text").toString()).isEqualTo("To keep ReqRes free, "
                + "contributions towards server costs are appreciated!");
    }

    @Test
    @DisplayName("DELETE, delete user")
    void deleteUserTest() {
        given()
                .when()
                .delete("/api/users/2")
                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("PUT, update user info")
    void updateUserInformationTest() {
        String data = "{ \"name\": \"morpheus\", \"job\": \"zion resident\" }";
        given()
                .contentType(JSON)
                .body(data)
                .when()
                .put("/api/users/2")
                .then()
                .statusCode(200)
                .body("name", is("morpheus"),
                        "job", is("zion resident"),
                        "updatedAt", notNullValue());
    }

    @Test
    @DisplayName("Get, single user")
    void getSingleUserTest() {
        Response response = get("/api/users/2")
                .then()
                .statusCode(200)
                .extract().response();
        assertThat(response.path("data.id").toString()).isEqualTo("2");
        assertThat(response.path("data.email").toString()).contains("janet.weaver@reqres.in");
        assertThat(response.path("data.first_name").toString()).isEqualTo("Janet");
        assertThat(response.path("data.last_name").toString()).isEqualTo("Weaver");
        assertThat(response.path("data.avatar").toString()).isNotNull();
    }

    @Test
    @DisplayName("POST, register unsuccessful")
    void postRegisterUnsuccessfulTest() {
        String data = "{ \"email\": \"sydney@fife\" }";
        given()
                .contentType(JSON)
                .body(data)
                .when()
                .post("/api/register")
                .then()
                .statusCode(400)
                .body("error", is("Missing password"));
    }
}
