package Beans;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateBasicBookingRequestModel {
    private String firstname;
    private String lastname;
    private Integer totalprice;
    private Boolean depositpaid;
    private String additionalneeds;
    private BookingDatesModel bookingdates;
}
