package Beans;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class BookingDatesModel {
    private String checkout;
    private String checkin;
}
