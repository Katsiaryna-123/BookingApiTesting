import Beans.BookingDatesModel;
import Beans.CreateBasicBookingRequestModel;
import Beans.BookingResponseModel;
import Helpers.Helper;
import Helpers.TokenShmoken;
import io.restassured.RestAssured;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class UpdateBooking {
    String token;
    Helper helper;

    @BeforeClass
    void generateToken() {
        helper = new Helper();
        token = new TokenShmoken().getToken();
    }

    @Test
    void updateLastNameInOneBooking() {
        CreateBasicBookingRequestModel booking = CreateBasicBookingRequestModel.builder()
                .firstname("firstname")
                .lastname("lastname")
                .depositpaid(false)
                .totalprice(90000)
                .bookingdates(BookingDatesModel.builder()
                        .checkin("2022-07-23")
                        .checkout("2022-10-23").build())
                .additionalneeds("pets friendly")
                .build();

        int initialBookingId = helper.createBasicBooking(booking);

        String lastnameOfUpdatedBooking = RestAssured.given()
                .when()
                .header("Cookie", "token=" + token)
                .contentType("application/json")
                .body("{\"lastname\": \"new last name\"}")
                .patch("https://restful-booker.herokuapp.com/booking/" + initialBookingId)
                .then()
                .statusCode(200)
                .extract()
                .path("lastname");

        Assert.assertEquals(lastnameOfUpdatedBooking, "new last name");
        helper.removeBookings(token, initialBookingId);
    }

    @Test
    void updateTotalPriceAndMakeSureThatAllTheRestParametersRemainNotChanged() {
        CreateBasicBookingRequestModel booking = CreateBasicBookingRequestModel.builder()
                .firstname("Mike")
                .lastname("Vazovski")
                .depositpaid(true)
                .totalprice(10)
                .bookingdates(BookingDatesModel.builder()
                        .checkin("2022-08-23")
                        .checkout("2022-08-27").build())
                .additionalneeds("parking")
                .build();

        int initialBookingId = helper.createBasicBooking(booking);

        RestAssured.given()
                .when()
                .header("Cookie", "token=" + token)
                .contentType("application/json")
                .body("{\"totalprice\": 900}")
                .patch("https://restful-booker.herokuapp.com/booking/" + initialBookingId)
                .then()
                .statusCode(200);
        BookingResponseModel result = RestAssured.given()
                .get("https://restful-booker.herokuapp.com/booking/" + initialBookingId)
                .as(BookingResponseModel.class);

        Assert.assertEquals(result.getBookingdates().getCheckin(), booking.getBookingdates().getCheckin());
        Assert.assertEquals(result.getBookingdates().getCheckout(), booking.getBookingdates().getCheckout());
        Assert.assertEquals(result.getAdditionalneeds(), booking.getAdditionalneeds());
        Assert.assertEquals(result.getDepositpaid(), booking.getDepositpaid());
        Assert.assertEquals(result.getFirstname(), booking.getFirstname());
        Assert.assertEquals(result.getLastname(), booking.getLastname());

        helper.removeBookings(token, initialBookingId);
    }
}
