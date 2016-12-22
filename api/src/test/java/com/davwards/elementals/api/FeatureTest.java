package com.davwards.elementals.api;

import io.restassured.http.ContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
        classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class FeatureTest {

    @Value("${local.server.port}")
    private String port;
    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    public void mainWorkflow() throws Exception {
        String playerUrl = given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"name\": \"TestPlayer\"\n" +
                        "}")
                .when()
                .post(baseUrl() + "/api/players")
                .then()
                .statusCode(201)
                .body("player.name", equalTo("TestPlayer"))
                .body("player.id", notNullValue())
                .extract()
                .header("Location");

        given()
                .contentType(ContentType.JSON)
                .when()
                .get(playerUrl)
                .then()
                .statusCode(200)
                .body("player.name", equalTo("TestPlayer"))
                .body("player.id", notNullValue());
    }
}
