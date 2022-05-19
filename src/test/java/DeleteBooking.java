import Beans.BookingDatesModel;
import Beans.CreateBasicBookingRequestModel;
import Helpers.Helper;
import Helpers.TokenShmoken;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

public class DeleteBooking {
    String token;
    Helper helper;

    @BeforeClass
    void generateToken() {
        helper = new Helper();
        token = new TokenShmoken().getToken();
    }

    @Test
    void deleteOneBookingFromTheListOfBookingAndVerifyThatThisBookingDoesntExistAnymore() {
        CreateBasicBookingRequestModel booking1 = CreateBasicBookingRequestModel.builder()
                .firstname("Mike")
                .lastname("Vazovski")
                .depositpaid(true)
                .totalprice(10)
                .bookingdates(BookingDatesModel.builder()
                        .checkin("2022-08-23")
                        .checkout("2022-08-27").build())
                .additionalneeds("parking")
                .build();

        int idOfCreatedBooking = helper.createBasicBooking(booking1);

        helper.removeBookings(token, idOfCreatedBooking);

        RestAssured
                .given()
                .get("https://restful-booker.herokuapp.com/booking/" + idOfCreatedBooking)
                .then()
                .assertThat()
                .statusCode(404);
    }
}
