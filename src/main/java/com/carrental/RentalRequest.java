package com.carrental;

import com.carrental.domain.CarType;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class RentalRequest {
    private String dateOfBirth;
    private String lastFourDigits;
    private CarType carType;
    private LocalDate pickUpDate;
    private LocalTime pickUpTime;
    private int mileageAtPickup;
}
