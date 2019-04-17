package com.carrental.repository;

import com.carrental.RentalRequest;
import com.carrental.domain.Car;
import com.carrental.domain.CarType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Component
public class CarRentalRepositoryImpl implements CarRentalRepository {

    @Autowired
    private DataSource dataSource;

    public CarRentalRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public CarType[] getCarTypes() {
        CarType[] carTypes = CarType.values();
        return carTypes;
    }

    @Override
    public List<Car> getAvailableCars(CarType car_type) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM cars WHERE car_type = ? AND available= 'TRUE'")) {
            ps.setString(1, car_type.getDisplayName());
            ResultSet rs = ps.executeQuery();
            List<Car> cars = new ArrayList<>();

            while (rs.next()) cars.add(rsCar(rs));
            return cars;

        } catch (SQLException e) {
            throw new CarRentalRepositoryException(e);
        }
    }

    public List<Car> getAllCars() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM cars")) {
            List<Car> cars = new ArrayList<>();
            while (rs.next()) {
                cars.add(rsCar(rs));
            }
            return cars;
        } catch (SQLException e) {
            throw new CarRentalRepositoryException(e);
        }
    }

    @Override
    public void selectCar(int carId, String ssn) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE rent_cars SET car_id = ? WHERE customer_ssn = ?")) {
            ps.setInt(1, carId);
            ps.setString(2, ssn);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new CarRentalRepositoryException(e);
        }
    }

    @Override
    public void addBooking(RentalRequest rentalRequest) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO rent_cars(customer_ssn,pick_up_date, pick_up_time, booking_number) VALUES (?,?,?,?)")) {
            String ssn = getCustomerSsn(rentalRequest);
            String bookingNumber = generateBookingNumber(rentalRequest);
            ps.setString(1, ssn);
            ps.setDate(2, Date.valueOf(rentalRequest.getPickUpDate()));
            ps.setTime(3, Time.valueOf(rentalRequest.getPickUpTime()));
            ps.setString(4, bookingNumber);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new CarRentalRepositoryException(e);
        }
    }

    @Override
    public String getCustomerSsn(RentalRequest rentalRequest) {
        String customerSsn = (rentalRequest.getDateOfBirth().toString()).replace("-", "") + "-" + rentalRequest.getLastFourDigits();
        return customerSsn;
    }

    private String generateBookingNumber(RentalRequest request){
        String bookingNumber = request.getPickUpDate().toString().replace("-", "") + request.getLastFourDigits();
        return bookingNumber;
    }


    private Car rsCar(ResultSet rs) throws SQLException {
        return new Car(
                rs.getInt("id"),
                rs.getString("registration_plate"),
                rs.getString("car_type"),
                rs.getInt("mileage")
        );
    }
}