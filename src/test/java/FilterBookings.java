import Beans.BookingDatesModel;
import Beans.CreateBasicBookingRequestModel;
import Helpers.Helper;
import Helpers.TokenBuilder;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.equalTo;

import java.util.List;

public class FilterBookings {
    String token;
    Helper helper;

    @BeforeClass
    void generateToken() {
        token = new TokenBuilder().getToken();
        helper = new Helper();
    }

    @Test
    void getBookingFromParticularPerson() {
        CreateBasicBookingRequestModel booking1 = CreateBasicBookingRequestModel.builder()
                .firstname("mikk")
                .lastname("kim")
                .depositpaid(true)
                .totalprice(90)
                .bookingdates(BookingDatesModel.builder()
                        .checkin("2030-02-10")
                        .checkout("2030-11-10").build())
                .additionalneeds("breakfast")
                .build();

        CreateBasicBookingRequestModel booking2 = CreateBasicBookingRequestModel.builder()
                .firstname("trulala")
                .lastname("lalala")
                .depositpaid(true)
                .totalprice(90)
                .bookingdates(BookingDatesModel.builder()
                        .checkin("2030-02-10")
                        .checkout("2030-11-10").build())
                .additionalneeds("breakfast")
                .build();

        CreateBasicBookingRequestModel[] initialDataForBookings = new CreateBasicBookingRequestModel[]{booking1, booking2};

        helper.createSeveralBooking(initialDataForBookings);

        List<Integer> bookingIds = RestAssured
                .given()
                .queryParam("firstname", "mikk")
                .get("https://restful-booker.herokuapp.com/booking/")
                .then()
                .extract()
                .path("bookingid");

        for (Integer bookingId : bookingIds) {
            RestAssured
                    .given()
                    .get("https://restful-booker.herokuapp.com/booking/" + bookingId)
                    .then()
                    .body("firstname", equalTo("mikk"));
        }
        helper.removeAllBookings(token, bookingIds);
    }

    @Test
    void filterBookingsByFirstAndLastName() {
        CreateBasicBookingRequestModel booking1 = CreateBasicBookingRequestModel.builder()
                .firstname("katsia")
                .lastname("lalala")
                .depositpaid(true)
                .totalprice(101)
                .bookingdates(BookingDatesModel.builder()
                        .checkin("2030-02-10")
                        .checkout("2030-11-10").build())
                .additionalneeds("breakfast")
                .build();

        CreateBasicBookingRequestModel booking2 = CreateBasicBookingRequestModel.builder()
                .firstname("katsia")
                .lastname("trulala")
                .depositpaid(true)
                .totalprice(100)
                .bookingdates(BookingDatesModel.builder()
                        .checkin("2030-02-10")
                        .checkout("2030-11-10").build())
                .additionalneeds("breakfast")
                .build();

        CreateBasicBookingRequestModel[] initialDataForBookings = new CreateBasicBookingRequestModel[]{booking1, booking2};

        helper.createSeveralBooking(initialDataForBookings);

        List<Integer> bookingIds = RestAssured
                .given()
                .queryParam("lastname", "trulala")
                .queryParam("firstname", "katsia")
                .get("https://restful-booker.herokuapp.com/booking")
                .then()
                .extract()
                .path("bookingid");

        for (Integer bookingId : bookingIds) {
            RestAssured
                    .given()
                    .get("https://restful-booker.herokuapp.com/booking/" + bookingId)
                    .then()
                    .body("firstname", equalTo("katsia"))
                    .body("lastname", equalTo("trulala"));
        }
        helper.removeAllBookings(token, bookingIds);
    }

    @Test
    void filterBookingsByCheckInAndCheckOutDates() {
        CreateBasicBookingRequestModel booking1 = CreateBasicBookingRequestModel.builder()
                .firstname("katsia")
                .lastname("trulala")
                .depositpaid(true)
                .totalprice(100)
                .bookingdates(BookingDatesModel.builder()
                        .checkin("2031-02-10")
                        .checkout("2030-11-10").build())
                .additionalneeds("breakfast")
                .build();

        CreateBasicBookingRequestModel booking2 = CreateBasicBookingRequestModel.builder()
                .firstname("katsia")
                .lastname("trulala")
                .depositpaid(true)
                .totalprice(100)
                .bookingdates(BookingDatesModel.builder()
                        .checkin("2032-02-10")
                        .checkout("2033-11-10").build())
                .additionalneeds("breakfast")
                .build();

        CreateBasicBookingRequestModel[] initialDataForBookings = new CreateBasicBookingRequestModel[]{booking1, booking2};

        helper.createSeveralBooking(initialDataForBookings);

        List<Integer> bookingIds = RestAssured
                .given()
                .queryParam("checkin", "2034-03-10")
                .queryParam("checkout", "2034-12-11")
                .get("https://restful-booker.herokuapp.com/booking")
                .then()
                .extract()
                .path("bookingid");

        for (Integer bookingId : bookingIds) {
            RestAssured
                    .given()
                    .get("https://restful-booker.herokuapp.com/booking/" + bookingId)
                    .then()
                    .body("checkin", equalTo("2034-03-10"))
                    .body("checkout", equalTo("2034-12-11"));
        }
        helper.removeAllBookings(token, bookingIds);
    }
}
