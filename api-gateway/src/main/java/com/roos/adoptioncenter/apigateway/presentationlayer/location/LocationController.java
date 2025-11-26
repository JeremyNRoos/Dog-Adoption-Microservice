package com.roos.adoptioncenter.apigateway.presentationlayer.location;

import com.roos.adoptioncenter.apigateway.businesslayer.location.LocationService;

import com.roos.adoptioncenter.apigateway.presentationlayer.adopter.AdopterResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("api/v1/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }


    @GetMapping(
            value = "/{locationId}",
            produces = "application/json"
    )
    public ResponseEntity<LocationResponseModel> getLocationById(@PathVariable String locationId) {
        log.debug("1. Request Received in API-Gateway Location Controller: getLocationById");
        return ResponseEntity.ok().body(locationService.getLocationById(locationId));
    }

    @GetMapping(
            produces = "application/json"
    )
    public ResponseEntity<List<LocationResponseModel>> getAllLocations() {
        log.debug("1. Request Received in API-Gateway Location Controller: getAllLocations");
        return ResponseEntity.ok().body(locationService.getAllLocations());
    }

    @PostMapping(
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    public ResponseEntity<LocationResponseModel> addLocation(@RequestBody LocationRequestModel locationRequestModel) {
        log.debug("1. Request Received in API-Gateway Location Controller: addLocation");
        LocationResponseModel locationResponseModel = locationService.addLocation(locationRequestModel);
        return new ResponseEntity<>(locationResponseModel, HttpStatus.CREATED);
    }

    @PutMapping(
            value = "/{locationId}",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    public ResponseEntity<LocationResponseModel> updateLocation(@RequestBody LocationRequestModel locationRequestModel, @PathVariable String locationId) {
        log.info("Received request to update location with ID: {}", locationId);
        return ResponseEntity.ok().body(locationService.updateLocation(locationId, locationRequestModel));
    }
    @DeleteMapping(
            value = "/{locationId}",
            produces = {"application/json"}
    )
    public ResponseEntity<Void> deleteLocation(@PathVariable String locationId) {
        log.info("Received request to delete location with ID: {}", locationId);
        locationService.deleteLocation(locationId);
        return ResponseEntity.noContent().build();
    }
}