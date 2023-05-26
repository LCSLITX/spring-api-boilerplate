package com.api.parkingcontrol.controllers;

import com.api.parkingcontrol.dtos.ParkingSpotDto;
import com.api.parkingcontrol.models.ParkingSpotModel;
import com.api.parkingcontrol.services.ParkingSpotService;
import jakarta.validation.Valid; // javax.persistence became jakarta.persistence on Spring 6/Spring boot 3.
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/parking-spot")
public class ParkingSpotController {

  final ParkingSpotService parkingSpotService;

  public ParkingSpotController(ParkingSpotService parkingSpotService) {
    this.parkingSpotService = parkingSpotService;
  }

  @PostMapping
  public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDto parkingSpotDto) {
    // @RequestBody annotation is to inform that the data received will be taken from the Request body.
    // @Valid annotation to validate data received by this method.
    // It returns automatic bad request if one parkingSpotDto's field is empty,
    // as all of them are annotated as @NotBlank.

    // Ideally these validations should be made through custom validations.
    if (parkingSpotService.existsByLicensePlateCar(parkingSpotDto.getLicensePlateCar())) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: License Plate Car is already in use!");
    }
    if (parkingSpotService.existsByParkingSpotNumber(parkingSpotDto.getParkingSpotNumber())) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot is already in use!");
    }
    if (parkingSpotService.existsByApartmentAndBlock(parkingSpotDto.getApartment(), parkingSpotDto.getBlock())) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking spot already registered for this apartment/block!");
    }

    // When in local scope it's possible to use var syntax,
    // which infer the type of the variable, instead of usual syntax.
    // Introduced in Java 10.
    var parkingSpotModel = new ParkingSpotModel();
    // ParkingSpotModel parkingSpotModel = new ParkingSpotModel();

    // Converting from parkingSpotDto to parkingSpotModel
    BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel);

    parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));

    return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));
  }

  // getAllParkingSpots without pagination.
  // @GetMapping
  // public ResponseEntity<List<ParkingSpotModel>> getAllParkingSpots() {
  //   return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll());
  // }

  @GetMapping
  public ResponseEntity<Page<ParkingSpotModel>> getAllParkingSpots(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll(pageable));
  }

  @GetMapping("/{id}") // @PathVariable must be equal to specified here.
  public ResponseEntity<Object> getOneParkingSpot(@PathVariable(value = "id") UUID id) {
    // Optional is a class wrapper, or a container, which may or may not contain a specified value.
    Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);

    // The following logic could be 'simplified' using functional style with lambda expressions.
    // return parkingSpotModelOptional.<ResponseEntity<Object>>map(
    //         parkingSpotModel -> ResponseEntity.status(HttpStatus.OK).body(parkingSpotModel)
    //     ).orElseGet(
    //         () -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found")
    //     );

    if (!parkingSpotModelOptional.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found");
    }
    return ResponseEntity.status(HttpStatus.OK).body(parkingSpotModelOptional.get());
  }

  @DeleteMapping("/{id}") // @PathVariable must be equal to specified here.
  public ResponseEntity<Object> deleteParkingSpot(@PathVariable(value = "id") UUID id) {
    Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
    if (!parkingSpotModelOptional.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found");
    }
    parkingSpotService.delete(parkingSpotModelOptional.get());
    return ResponseEntity.status(HttpStatus.OK).body("Parking Spot deleted succesfully");
  }

  @PutMapping("/{id}") // @PathVariable must be equal to specified here.
  public ResponseEntity<Object> updateParkingSpot(@PathVariable(value = "id") UUID id, @RequestBody @Valid ParkingSpotDto parkingSpotDto) {
    Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
    if (!parkingSpotModelOptional.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found");
    }

    // One way of accomplishing.
    // var parkingSpotModel = parkingSpotModelOptional.get();
    // parkingSpotModel.setParkingSpotNumber(parkingSpotDto.getParkingSpotNumber());
    // parkingSpotModel.setLicensePlateCar(parkingSpotDto.getLicensePlateCar());
    // parkingSpotModel.setModelCar(parkingSpotDto.getModelCar());
    // parkingSpotModel.setBrandCar(parkingSpotDto.getBrandCar());
    // parkingSpotModel.setColorCar(parkingSpotDto.getColorCar());
    // parkingSpotModel.setResponsibleName(parkingSpotDto.getResponsibleName());
    // parkingSpotModel.setApartment(parkingSpotDto.getApartment());
    // parkingSpotModel.setBlock(parkingSpotDto.getBlock());

    // Another way of accomplishing.
    var parkingSpotModel = new ParkingSpotModel();
    BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel);
    parkingSpotModel.setId(parkingSpotModelOptional.get().getId());
    parkingSpotModel.setRegistrationDate(parkingSpotModelOptional.get().getRegistrationDate());

    return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel));
  }
}
