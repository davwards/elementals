package com.davwards.elementals.api;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
        classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles({"controlled-time", "fake-notifications"})
public class FeatureTest {

    @Test
    public void mainWorkflow() throws Exception {
        Response createPlayerResponse = given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"name\": \"TestPlayer\"\n" +
                        "}")
                .when()
                .post(baseUrl() + "/api/players");

        createPlayerResponse
                .then()
                .statusCode(201)
                .body("name", equalTo("TestPlayer"))
                .body("id", notNullValue())
                .body("health", notNullValue())
                .body("experience", equalTo(0))
                .body("coin", equalTo(0));

        String playerUrl = createPlayerResponse
                .then().extract().header("Location");

        String playerId = createPlayerResponse
                .then().extract().body().jsonPath().getString("id");

        given()
                .contentType(ContentType.JSON)
                .when()
                .get(playerUrl)
                .then()
                .statusCode(200)
                .body("name", equalTo("TestPlayer"))
                .body("id", notNullValue())
                .body("health", notNullValue())
                .body("experience", equalTo(0));

        LocalDateTime now = currentTime();
        LocalDateTime nextWeek = now.plusDays(7);

        String taskUrl = given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"title\": \"Task that gets completed\",\n" +
                        "  \"deadline\": \"" + nextWeek.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\"\n" +
                        "}")
                .when()
                .post(baseUrl() + "/api/players/" + playerId + "/tasks")
                .then()
                .statusCode(201)
                .body("title", equalTo("Task that gets completed"))
                .body("playerId", equalTo(playerId))
                .body("deadline", equalTo(nextWeek.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .body("status", equalTo("incomplete"))
                .extract()
                .header("Location");

        given()
                .contentType(ContentType.JSON)
                .when()
                .get(taskUrl)
                .then()
                .statusCode(200)
                .body("title", equalTo("Task that gets completed"))
                .body("playerId", equalTo(playerId))
                .body("deadline", equalTo(nextWeek.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .body("status", equalTo("incomplete"));

        given()
                .contentType(ContentType.JSON)
                .when()
                .put(taskUrl + "/complete")
                .then()
                .statusCode(200)
                .body("title", equalTo("Task that gets completed"))
                .body("playerId", equalTo(playerId))
                .body("deadline", equalTo(nextWeek.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .body("status", equalTo("complete"));

        String taskThatDoesNotGetCompletedUrl = given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"title\": \"Task that does not get completed\",\n" +
                        "  \"deadline\": \"" + nextWeek.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\"\n" +
                        "}")
                .when()
                .post(baseUrl() + "/api/players/" + playerId + "/tasks")
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        Integer originalHealth = createPlayerResponse.then().extract().body().jsonPath().getInt("health");

        setCurrentTimeTo(nextWeek.plusDays(1));

        assertThatEventually(
                () -> {
                    String taskStatus = given()
                            .contentType(ContentType.JSON)
                            .when()
                            .get(taskThatDoesNotGetCompletedUrl)
                            .then()
                            .statusCode(200)
                            .extract().body().jsonPath()
                            .getString("status");

                    return "pastDue".equals(taskStatus);
                },
                10, "Gave up waiting for status of incomplete task to update"
        );

        assertThatEventually(
                () -> {
                    Integer currentHealth = given()
                            .contentType(ContentType.JSON)
                            .when()
                            .get(playerUrl)
                            .then()
                            .statusCode(200)
                            .extract().body().jsonPath()
                            .getInt("health");

                    return currentHealth < originalHealth;
                },
                10, "Gave up waiting for player to be damaged by incomplete task"
        );
    }

    private static void assertThatEventually(Supplier<Boolean> assertion, int secondsToWait, String failureMessage) {
        LocalDateTime cutoff = LocalDateTime.now().plusSeconds(secondsToWait);
        while(LocalDateTime.now().isBefore(cutoff)) {
            if(assertion.get()) {
                return;
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        fail(failureMessage);
    }

    private LocalDateTime currentTime() {
        return LocalDateTime.parse(
                when()
                        .get(baseUrl() + "/test/time")
                        .then().extract().body().asString()
        );
    }

    private void setCurrentTimeTo(LocalDateTime time) {
        given()
                .body(time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .when()
                .put(baseUrl() + "/test/time")
                .then()
                .statusCode(200);
    }

    @Value("${local.server.port}")
    private String port;

    private String baseUrl() {
        return "http://localhost:" + port;
    }
}
