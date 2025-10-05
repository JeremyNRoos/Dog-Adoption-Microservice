package com.roos.adoptioncenter.apigateway.presentationlayer.dog;


import com.roos.adoptioncenter.apigateway.businesslayer.dog.DogService;
import com.roos.adoptioncenter.apigateway.businesslayer.location.LocationService;
import com.roos.adoptioncenter.apigateway.presentationlayer.location.LocationRequestModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.location.LocationResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/locations/{locationId}/dogs")
public class DogController {

    private final DogService dogService;

    public DogController(DogService dogService) {
        this.dogService = dogService;
    }


    @GetMapping(
            value = "/{dogId}",
            produces = "application/json"
    )
    public ResponseEntity<DogResponseModel> getDogById(@PathVariable String locationId, @PathVariable String dogId) {
        log.debug("1. Request Received in API-Gateway Dog Controller: getDogById");
        return ResponseEntity.ok().body(dogService.getDogById(locationId,dogId));
    }

    @GetMapping(
            produces = "application/json"
    )
    public ResponseEntity<List<DogResponseModel>> getAllDogs(@PathVariable String locationId) {
        log.debug("1. Request Received in API-Gateway Dog Controller: getAllDogs");
        return ResponseEntity.ok().body(dogService.getAllDogs(locationId));
    }

    @PostMapping(
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    public ResponseEntity<DogResponseModel> addDog(@PathVariable String locationId,@RequestBody DogRequestModel dogRequestModel) {
        log.debug("1. Request Received in API-Gateway Dog Controller: addDog");
        DogResponseModel dogResponseModel = dogService.addDog(locationId,dogRequestModel);
        return new ResponseEntity<>(dogResponseModel, HttpStatus.CREATED);
    }

    @PutMapping(
            value = "/{dogId}",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    public ResponseEntity<DogResponseModel> updateDog(@PathVariable String locationId,@RequestBody DogRequestModel dogRequestModel, @PathVariable String dogId) {
        log.info("Received request to update dog with ID: {}", dogId);
        return ResponseEntity.ok().body(dogService.updateDog(locationId, dogRequestModel, dogId));
    }
    @DeleteMapping(
            value = "/{dogId}",
            produces = {"application/json"}
    )
    public ResponseEntity<Void> deleteDog(@PathVariable String locationId, @PathVariable String dogId) {
        log.info("Received request to delete dog with ID: {}", dogId);
        dogService.deleteDog(locationId,dogId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

