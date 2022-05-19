import Beans.BookingDatesModel;
import Beans.CreateBasicBookingRequestModel;
import Helpers.Helper;
import Helpers.TokenBuilder;
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
        token = new TokenBuilder().getToken();
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

        CreateBasicBookingRequestModel booking2 = CreateBasicBookingRequestModel.builder()
                .firstname("Bu")
                .lastname("sqlan")
                .depositpaid(true)
                .totalprice(956)
                .bookingdates(BookingDatesModel.builder()
                        .checkin("2022-08-23")
                        .checkout("2022-08-27").build())
                .additionalneeds("parking")
                .build();

        CreateBasicBookingRequestModel booking3 = CreateBasicBookingRequestModel.builder()
                .firstname("kraib")
                .lastname("goana")
                .depositpaid(true)
                .totalprice(901)
                .bookingdates(BookingDatesModel.builder()
                        .checkin("2022-08-23")
                        .checkout("2022-08-27").build())
                .additionalneeds("nothing needed")
                .build();

        CreateBasicBookingRequestModel[] createdBooking = new CreateBasicBookingRequestModel[]{booking1, booking2, booking3};
        List<Integer> idsOfCreatedBookings = helper.createSeveralBooking(createdBooking);

        helper.removeAllBookings(token, idsOfCreatedBookings.get(0));

        RestAssured
                .given()
                .get("https://restful-booker.herokuapp.com/booking/" + idsOfCreatedBookings.get(0))
                .then()
                .assertThat()
                .statusCode(404);
    }
}
