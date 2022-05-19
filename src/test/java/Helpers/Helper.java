package Helpers;

import Beans.CreateBasicBookingRequestModel;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.List;

public class Helper {
    public int createBasicBooking(CreateBasicBookingRequestModel createdBooking) {
        int bookingId = RestAssured.given()
                .contentType("application/json")
                .body(createdBooking)
                .post("https://restful-booker.herokuapp.com/booking")
                .then()
                .statusCode(200)
                .extract()
                .path("bookingid");
        return bookingId;
    }

    public List<Integer> createSeveralBooking(CreateBasicBookingRequestModel[] createdBooking) {
        List<Integer> createdBookingIds = new ArrayList<>();
        for (CreateBasicBookingRequestModel createBasicBookingRequestTemplate : createdBooking) {
            Response response = RestAssured.given()
                    .contentType("application/json")
                    .body(createBasicBookingRequestTemplate)
                    .post("https://restful-booker.herokuapp.com/booking");
            createdBookingIds.add(response
                    .then()
                    .statusCode(200)
                    .extract()
                    .body()
                    .path("bookingid"));
        }
        return createdBookingIds;
    }

    public void removeAllBookings(String token, List<Integer> bookingIds) {
        for (Integer bookingId : bookingIds) {
            RestAssured
                    .given()
                    .when()
                    .header("Cookie", "token=" + token)
                    .delete("https://restful-booker.herokuapp.com/booking/" + bookingId)
                    .then()
                    .statusCode(201);
        }
    }

    public void removeAllBookings(String token, int bookingId) {
        RestAssured
                .given()
                .when()
                .header("Cookie", "token=" + token)
                .delete("https://restful-booker.herokuapp.com/booking/" + bookingId)
                .then()
                .statusCode(201);
    }
}
