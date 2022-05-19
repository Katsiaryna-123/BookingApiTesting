package Helpers;

import Beans.TokenCredentialsModel;
import io.restassured.RestAssured;

public class TokenShmoken {
    public String getToken() {
        return RestAssured
                .given()
                .contentType("application/json")
                .body(TokenCredentialsModel.builder()
                        .username("admin")
                        .password("password123")
                        .build())
                .post("https://restful-booker.herokuapp.com/auth")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .path("token");
    }
}
