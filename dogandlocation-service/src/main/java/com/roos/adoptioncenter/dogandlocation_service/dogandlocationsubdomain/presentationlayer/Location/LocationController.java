package com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.presentationlayer.Location;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.businesslayer.Location.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {
    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping()
    public ResponseEntity<List<LocationResponseModel>> getLocations(){
        List<LocationResponseModel> locations = locationService.getLocations();
        return ResponseEntity.ok().body(locations);
    }

    @GetMapping("/{locationId}")
    public ResponseEntity<LocationResponseModel> getLocationById(@PathVariable String locationId){
        LocationResponseModel locationResponseModel = locationService.getLocationById(locationId);
        return ResponseEntity.ok().body(locationResponseModel);
    }

    @PostMapping()
    public ResponseEntity<LocationResponseModel> addLocation(@RequestBody LocationRequestModel locationRequestModel){
        return new ResponseEntity<>(locationService.addLocation(locationRequestModel), HttpStatus.CREATED);
    }

    @PutMapping("/{locationId}")
    public ResponseEntity<LocationResponseModel> updateLocation(@PathVariable String locationId, @RequestBody LocationRequestModel locationRequestModel){
        return new ResponseEntity<>(locationService.updateLocation(locationRequestModel, locationId), HttpStatus.OK);
    }

    @DeleteMapping("/{locationId}")
    public ResponseEntity<Void> deleteLocation(@PathVariable String locationId){
        locationService.deleteLocation(locationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}


