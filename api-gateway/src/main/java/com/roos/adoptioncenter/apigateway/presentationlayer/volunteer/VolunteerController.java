package com.roos.adoptioncenter.apigateway.presentationlayer.volunteer;

import com.roos.adoptioncenter.apigateway.businesslayer.volunteer.VolunteerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("api/v1/volunteers")
public class VolunteerController {

    private final VolunteerService volunteerService;

    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }


    @GetMapping(
            value = "/{volunteerId}",
            produces = "application/json"
    )
    public ResponseEntity<VolunteerResponseModel> getVolunteerById(@PathVariable String volunteerId) {
        log.debug("1. Request Received in API-Gateway Volunteer Controller: getVolunteerById");
        return ResponseEntity.status(HttpStatus.OK).body(volunteerService.getVolunteerById(volunteerId));
    }

    @GetMapping(
            produces = "application/json"
    )
    public ResponseEntity<List<VolunteerResponseModel>> getAllVolunteers() {
        log.debug("1. Request Received in API-Gateway Volunteer Controller: getAllVolunteers");
        return ResponseEntity.status(HttpStatus.OK).body(volunteerService.getVolunteers());
    }

    @PostMapping(
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<VolunteerResponseModel> addVolunteer(@RequestBody VolunteerRequestModel volunteerRequestModel) {
        log.debug("1. Request Received in API-Gateway Volunteer Controller: addVolunteer");
        return ResponseEntity.status(HttpStatus.CREATED).body(volunteerService.addVolunteer(volunteerRequestModel));
    }

    @PutMapping(
            value = "/{volunteerId}",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<VolunteerResponseModel> updateVolunteer(@RequestBody VolunteerRequestModel volunteerRequestModel, @PathVariable String volunteerId) {
        log.info("Received request to update volunteers with ID: {}", volunteerId);
        return ResponseEntity.status(HttpStatus.OK).body(volunteerService.updateVolunteer(volunteerRequestModel, volunteerId));
    }

    @DeleteMapping(
            value = "/{volunteerId}"
    )
    public ResponseEntity<Void> deleteVolunteer(@PathVariable String volunteerId) {
        log.info("Received request to delete volunteers with ID: {}", volunteerId);
        volunteerService.deleteVolunteer(volunteerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
