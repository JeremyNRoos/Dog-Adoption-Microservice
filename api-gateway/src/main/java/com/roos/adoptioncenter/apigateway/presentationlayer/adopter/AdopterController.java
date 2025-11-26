package com.roos.adoptioncenter.apigateway.presentationlayer.adopter;

import com.roos.adoptioncenter.apigateway.businesslayer.adopter.AdopterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("api/v1/adopters")
public class AdopterController {

    private final AdopterService adopterService;

    public AdopterController(AdopterService adopterService) {
        this.adopterService = adopterService;
    }

    @GetMapping(
            value = "/{adopterId}",
            produces = "application/json"
    )
    public ResponseEntity<AdopterResponseModel> getAdopterById(@PathVariable String adopterId) {
        log.debug("1. Request Received in API-Gateway Adopter Controller: getAdopterById");
        return ResponseEntity.status(HttpStatus.OK).body(adopterService.getAdopterById(adopterId));
    }

    @GetMapping(
            produces = {"application/json"}
    )
    public ResponseEntity<List<AdopterResponseModel>> getAllAdopters() {
        log.debug("1. Request Received in API-Gateway Adopter Controller: getAllAdopters");
        return ResponseEntity.status(HttpStatus.OK).body(adopterService.getAllAdopters());
    }

    @PostMapping(
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<AdopterResponseModel> createAdopter(@RequestBody AdopterRequestModel adopterRequestModel) {
        log.debug("1. Request Received in API-Gateway Adopter Controller: updateAdopter");
        return ResponseEntity.status(HttpStatus.CREATED).body(adopterService.createAdopter(adopterRequestModel));
    }

    @PutMapping(
            value = "/{adopterId}",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<AdopterResponseModel> updateAdopter(@RequestBody AdopterRequestModel adopterRequestModel,@PathVariable String adopterId) {
        log.info("Received request to update adopter with ID: {}", adopterId);
        return ResponseEntity.status(HttpStatus.OK).body(adopterService.updateAdopter(adopterRequestModel, adopterId));
    }

    @DeleteMapping(
            value = "/{adopterId}",
            produces = {"application/json"}
    )
    public ResponseEntity<Void> deleteAdopter(@PathVariable String adopterId) {
        log.info("Received request to delete adopter with ID: {}", adopterId);
        adopterService.deleteAdopter(adopterId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
