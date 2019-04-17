package com.carrental.repository;

import com.carrental.RentalRequest;
import com.carrental.domain.Car;
import com.carrental.domain.CarType;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;


@Repository
public interface CarRentalRepository {

    CarType[] getCarTypes();
    List<Car> getAllCars();
    List<Car> getAvailableCars(CarType car_type);
    void selectCar(int carId, String ssn);
    void addBooking(RentalRequest rentalRequest);
    String getCustomerSsn(RentalRequest rentalRequest);


}
