package com.api.parkingcontrol.services;

import com.api.parkingcontrol.models.ParkingSpotModel;
import com.api.parkingcontrol.repositories.ParkingSpotRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
public class ParkingSpotService {

  // Field injection with autowired, not recommended.
  // Read More: https://stackoverflow.com/questions/39890849/what-exactly-is-field-injection-and-how-to-avoid-it
  // @Autowired
  // ParkingSpotRepository parkingSpotRepository;

  // constructor injection.
  final ParkingSpotRepository parkingSpotRepository;
  public ParkingSpotService(ParkingSpotRepository parkingSpotRepository) {
    this.parkingSpotRepository = parkingSpotRepository;
  }

  // About @Transactional. It's interesting, on potentially destructive or constructive methods,
  // the presence of this annotation to ensure the use of transaction concept in database;
  // In case of anything going wrong, the database automatically rollback to previous state.
  @Transactional
  public ParkingSpotModel save(ParkingSpotModel parkingSpotModel) {
    return parkingSpotRepository.save(parkingSpotModel);
  }

  public boolean existsByLicensePlateCar(String licensePlateCar) {
    return parkingSpotRepository.existsByLicensePlateCar(licensePlateCar);
  }

  public boolean existsByParkingSpotNumber(String parkingSpotNumber) {
    return parkingSpotRepository.existsByParkingSpotNumber(parkingSpotNumber);
  }

  public boolean existsByApartmentAndBlock(String apartment, String block) {
    return parkingSpotRepository.existsByApartmentAndBlock(apartment, block);
  }

  public List<ParkingSpotModel> findAll() {
    return parkingSpotRepository.findAll();
  }

  public Page<ParkingSpotModel> findAll(Pageable pageable) {
    return parkingSpotRepository.findAll(pageable);
  }

  public Optional<ParkingSpotModel> findById(UUID id) {
    return parkingSpotRepository.findById(id);
  }

  @Transactional
  public void delete(ParkingSpotModel parkingSpotModel) {
    parkingSpotRepository.delete(parkingSpotModel);
  }
}
