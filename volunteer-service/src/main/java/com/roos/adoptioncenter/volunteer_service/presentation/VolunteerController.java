package com.roos.adoptioncenter.volunteer_service.presentation;

import com.roos.adoptioncenter.volunteer_service.businesslayer.VolunteerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/volunteers")
public class VolunteerController {

    private final VolunteerService volunteerService;

    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }

    @GetMapping()
    public ResponseEntity<List<VolunteerResponseModel>> getVolunteers(){
        List<VolunteerResponseModel> responseModel = volunteerService.getVolunteers();
        return ResponseEntity.ok().body(responseModel);
    }

    @GetMapping("/{volunteerId}")
    public ResponseEntity<VolunteerResponseModel> getVolunteerById(@PathVariable String volunteerId){
        VolunteerResponseModel responseModel = volunteerService.getVolunteerById(volunteerId);
        return ResponseEntity.ok().body(responseModel);
    }

    @PostMapping()
    public ResponseEntity<VolunteerResponseModel> addVolunteer(@RequestBody VolunteerRequestModel volunteerRequestModel){
        VolunteerResponseModel volunteerResponseModel = volunteerService.addVolunteer(volunteerRequestModel);
        return new ResponseEntity<>(volunteerResponseModel, HttpStatus.CREATED);
    }

    @PutMapping("/{volunteerId}")
    public ResponseEntity<VolunteerResponseModel> updateVolunteer(@RequestBody VolunteerRequestModel volunteerRequestModel, @PathVariable String volunteerId){
        VolunteerResponseModel volunteerResponseModel = volunteerService.updateVolunteer(volunteerRequestModel, volunteerId);
        return new ResponseEntity<>(volunteerResponseModel, HttpStatus.OK);
    }

    @DeleteMapping("/{volunteerId}")
    public ResponseEntity<Void> removeVolunteer(@PathVariable String volunteerId){
        volunteerService.deleteVolunteer(volunteerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
