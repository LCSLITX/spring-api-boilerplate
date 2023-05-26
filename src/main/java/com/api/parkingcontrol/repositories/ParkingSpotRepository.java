package com.api.parkingcontrol.repositories;

import com.api.parkingcontrol.models.ParkingSpotModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository // Stereotype for transactions with databases
public interface ParkingSpotRepository extends JpaRepository<ParkingSpotModel, UUID> {
  // JpaRepository implementation already implements common methods to read,
  // write etc. in a database, such as save or findAll used on Service package.

  // Custom methods can be implemented by being declared here.
  boolean existsByLicensePlateCar(String licensePlateCar);
  boolean existsByParkingSpotNumber(String parkingSpotNumber);
  boolean existsByApartmentAndBlock(String Apartment, String Block);
}
