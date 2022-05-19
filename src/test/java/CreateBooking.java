import Beans.BookingDatesModel;
import Beans.BookingResponseModel;
import Beans.CreateBasicBookingRequestModel;
import Helpers.Helper;
import Helpers.TokenBuilder;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;

public class CreateBooking {
    String token;
    Helper helper;

    @BeforeClass
    void generateToken() {
        token = new TokenBuilder().getToken();
        helper = new Helper();
    }

    @Test
    void createBasicBookingWithAllFieldsFilledInAndVerifyAllFields() {
        CreateBasicBookingRequestModel booking = CreateBasicBookingRequestModel.builder()
                .firstname("Jim")
                .lastname("Brown")
                .depositpaid(true)
                .totalprice(111)
                .bookingdates(BookingDatesModel.builder()
                        .checkin("2025-01-01")
                        .checkout("2026-01-01").build())
                .additionalneeds("breakfast")
                .build();

        int bookingId = helper.createBasicBooking(booking);

        BookingResponseModel response = RestAssured
                .given()
                .get("https://restful-booker.herokuapp.com/booking/" + bookingId)
                .as(BookingResponseModel.class);

        Assert.assertEquals(response.getFirstname(), booking.getFirstname());
        Assert.assertEquals(response.getLastname(), booking.getLastname());
        Assert.assertEquals(response.getTotalprice(), booking.getTotalprice());
        Assert.assertEquals(response.getDepositpaid(), booking.getDepositpaid());
        Assert.assertEquals(response.getBookingdates().getCheckin(), booking.getBookingdates().getCheckin());
        Assert.assertEquals(response.getBookingdates().getCheckout(), booking.getBookingdates().getCheckout());
        Assert.assertEquals(response.getAdditionalneeds(), booking.getAdditionalneeds());

        helper.removeAllBookings(token, bookingId);
    }

    @Test
    void checkThatItsPossibleToCreateBookingWithSpecialSymbolsAndNumbersInName() {
        CreateBasicBookingRequestModel booking = CreateBasicBookingRequestModel.builder()
                .firstname("@)!(@)#)*!)@&#*(&$(")
                .lastname("ordinary")
                .depositpaid(false)
                .totalprice(90)
                .bookingdates(BookingDatesModel.builder()
                        .checkin("2010-10-10")
                        .checkout("2019-01-01").build())
                .additionalneeds("breakfast")
                .build();

        int bookingId = helper.createBasicBooking(booking);

        helper.removeAllBookings(token, bookingId);
    }

    @Test
    void checkThatItsPossibleToCreateBookingWithNumbersInName() {
        CreateBasicBookingRequestModel booking = CreateBasicBookingRequestModel.builder()
                .firstname("Name")
                .lastname("2398407238570752")
                .depositpaid(false)
                .totalprice(90)
                .bookingdates(BookingDatesModel.builder()
                        .checkin("2022-10-10")
                        .checkout("2022-12-01").build())
                .additionalneeds("breakfast")
                .build();

        int bookingId = helper.createBasicBooking(booking);

        helper.removeAllBookings(token, bookingId);
    }

    @Test
    void checkThatItsPossibleToCreateBookingWithNameNotInEnglish() {
        CreateBasicBookingRequestModel booking = CreateBasicBookingRequestModel.builder()
                .firstname("Кристина")
                .lastname("Высокова")
                .depositpaid(true)
                .totalprice(345)
                .bookingdates(BookingDatesModel.builder()
                        .checkin("2023-10-10")
                        .checkout("2023-12-01").build())
                .additionalneeds("breakfast")
                .build();

        int bookingId = helper.createBasicBooking(booking);

        helper.removeAllBookings(token, bookingId);
    }

    @Test
    void checkThatItsPossibleToCreateBookingWithDatesInPast() {
        CreateBasicBookingRequestModel booking = CreateBasicBookingRequestModel.builder()
                .firstname("Iryna")
                .lastname("Artysh")
                .depositpaid(true)
                .totalprice(2784)
                .bookingdates(BookingDatesModel.builder()
                        .checkin("1983-10-10")
                        .checkout("1984-01-01").build())
                .additionalneeds("breakfast")
                .build();

        int bookingId = RestAssured.given()
                .contentType("application/json")
                .body(booking)
                .post("https://restful-booker.herokuapp.com/booking")
                .then()
                .statusCode(400)
                .extract()
                .path("bookingid");

        helper.removeAllBookings(token, bookingId);
    }

    @Test
    void getErrorWhenTryingToCreateBookingWithoutAnyOfMandatoryFields() {
        CreateBasicBookingRequestModel booking = CreateBasicBookingRequestModel.builder()
                .lastname("Haspadaryk")
                .depositpaid(true)
                .totalprice(2784)
                .bookingdates(BookingDatesModel.builder()
                        .checkin("2030-10-10")
                        .checkout("2030-11-10").build())
                .additionalneeds("breakfast")
                .build();

        RestAssured.given()
                .contentType("application/json")
                .body(booking)
                .post("https://restful-booker.herokuapp.com/booking")
                .then()
                .statusCode(400);
    }

    @Test
    void getErrorWhenTryingToCreateBookingWithNonExistingDate() {
        CreateBasicBookingRequestModel booking = CreateBasicBookingRequestModel.builder()
                .firstname("Malibu")
                .lastname("Alyaska")
                .depositpaid(true)
                .totalprice(900)
                .bookingdates(BookingDatesModel.builder()
                        .checkin("2030-20-40")
                        .checkout("2030-11-10").build())
                .additionalneeds("breakfast")
                .build();

        int bookingId = RestAssured.given()
                .contentType("application/json")
                .body(booking)
                .post("https://restful-booker.herokuapp.com/booking")
                .then()
                .statusCode(400)
                .extract()
                .path("bookingid");

        helper.removeAllBookings(token, bookingId);
    }

    @Test
    void CreateSeveralBookingAndCheckThatTheNumberOfAppearedBookingsEqualsToNumberOfCreatedOnes() {
        CreateBasicBookingRequestModel booking1 = CreateBasicBookingRequestModel.builder()
                .firstname("oliver")
                .lastname("brie")
                .depositpaid(true)
                .totalprice(90)
                .bookingdates(BookingDatesModel.builder()
                        .checkin("2030-01-20")
                        .checkout("2030-11-10").build())
                .additionalneeds("breakfast")
                .build();

        CreateBasicBookingRequestModel booking2 = CreateBasicBookingRequestModel.builder()
                .firstname("migel")
                .lastname("vadhvani")
                .depositpaid(true)
                .totalprice(200)
                .bookingdates(BookingDatesModel.builder()
                        .checkin("2027-10-20")
                        .checkout("2027-11-10").build())
                .additionalneeds("parking")
                .build();

        CreateBasicBookingRequestModel booking3 = CreateBasicBookingRequestModel.builder()
                .firstname("krisi")
                .lastname("natuke")
                .depositpaid(false)
                .totalprice(180)
                .bookingdates(BookingDatesModel.builder()
                        .checkin("2023-02-14")
                        .checkout("2023-02-15").build())
                .additionalneeds("baloons")
                .build();

        CreateBasicBookingRequestModel[] initialDataForBookings = new CreateBasicBookingRequestModel[]{booking1, booking2, booking3};

        List<Integer> response = helper.createSeveralBooking(initialDataForBookings);

        Assert.assertEquals(initialDataForBookings.length, response.size());

        helper.removeAllBookings(token, response);
    }

    @Test
    void checkOutDateBeforeCheckIn() {
        CreateBasicBookingRequestModel booking = CreateBasicBookingRequestModel.builder()
                .firstname("jack")
                .lastname("london")
                .depositpaid(false)
                .totalprice(80)
                .bookingdates(BookingDatesModel.builder()
                        .checkin("2030-10-10")
                        .checkout("2020-11-10").build())
                .additionalneeds("breakfast")
                .build();

        int bookingId = RestAssured.given()
                .contentType("application/json")
                .body(booking)
                .post("https://restful-booker.herokuapp.com/booking")
                .then()
                .statusCode(400)
                .extract()
                .path("bookingid");

        helper.removeAllBookings(token, bookingId);
    }

    @Test
    void checkOutDateEqualsCheckIn() {
        CreateBasicBookingRequestModel booking = CreateBasicBookingRequestModel.builder()
                .firstname("lindi")
                .lastname("am")
                .depositpaid(true)
                .totalprice(10000)
                .bookingdates(BookingDatesModel.builder()
                        .checkin("2030-10-10")
                        .checkout("2030-10-10").build())
                .additionalneeds("no needs")
                .build();

        int bookingId = RestAssured.given()
                .contentType("application/json")
                .body(booking)
                .post("https://restful-booker.herokuapp.com/booking")
                .then()
                .statusCode(400)
                .extract()
                .path("bookingid");

        helper.removeAllBookings(token, bookingId);
    }
}
