package com.roos.adoptioncenter.adoptionpaper_service.presentationlayer;



import com.roos.adoptioncenter.adoptionpaper_service.businesslayer.AdoptionPaperService;
import com.roos.adoptioncenter.adoptionpaper_service.mappinglayer.AdoptionPaperResponseMapper;
import com.roos.adoptioncenter.adoptionpaper_service.utils.ExceptionsHandling.BadRequestException;
import com.roos.adoptioncenter.adoptionpaper_service.utils.ExceptionsHandling.InvalidInputException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/api/v1/adopters/{adopterId}/adoptionpapers")
public class AdoptionPaperController {
    private final AdoptionPaperService adoptionPaperService;


    public AdoptionPaperController(AdoptionPaperService adoptionPaperService) {
        this.adoptionPaperService = adoptionPaperService;
    }


    @GetMapping()
    public ResponseEntity<List<AdoptionPaperResponseModel>> getAdoptionPapers(@PathVariable String adopterId){
        List<AdoptionPaperResponseModel> adoptionPaperResponseModels = adoptionPaperService.getAdoptionPapers(adopterId);
        return ResponseEntity.ok().body(adoptionPaperResponseModels);
    }

    @GetMapping("/{adoptionpaperId}")
    public ResponseEntity<AdoptionPaperResponseModel> getAdoptionPaperById(@PathVariable String adopterId,@PathVariable String adoptionpaperId){
        if (!isValidUUID(adoptionpaperId)) {
            throw new InvalidInputException("Invalid UUID format for adoption paper ID: " + adoptionpaperId);
        }
        return ResponseEntity.ok().body(adoptionPaperService.getAdoptionPaperById(adopterId,adoptionpaperId));
    }

    @PostMapping()
    public ResponseEntity<AdoptionPaperResponseModel> addAdoptionPaper(@RequestBody AdoptionPaperRequestModel adoptionPaperRequestModel, @PathVariable String adopterId){
        log.debug("dog id is:" + adoptionPaperRequestModel.getDogId() + " and location id is: " + adoptionPaperRequestModel.getLocationId());
        if (adoptionPaperRequestModel.getDogId() == null ||
                adoptionPaperRequestModel.getLocationId() == null ||
                adoptionPaperRequestModel.getVolunteerId() == null) {
            throw new BadRequestException("Missing required fields in the request");
        }
        return new ResponseEntity<>(adoptionPaperService.addAdoptionPaper(adoptionPaperRequestModel, adopterId), HttpStatus.CREATED);
    }

    @PutMapping("/{adoptionpaperId}")
    public ResponseEntity<AdoptionPaperResponseModel> UpdateAdoptionPaper(@PathVariable String adopterId,@RequestBody AdoptionPaperRequestModel adoptionPaperRequestModel,@PathVariable String adoptionpaperId){
        if (!isValidUUID(adoptionpaperId)) {
            throw new InvalidInputException("Invalid UUID format for adoption paper ID: " + adoptionpaperId);
        }
        return new ResponseEntity<>(adoptionPaperService.updateAdoptionPaper(adopterId,adoptionPaperRequestModel,adoptionpaperId), HttpStatus.OK);
    }

    @DeleteMapping("/{adoptionpaperId}")
    public ResponseEntity<Void> deleteAdoptionPaper(@PathVariable String adopterId,@PathVariable String adoptionpaperId){
        if (!isValidUUID(adoptionpaperId)) {
            throw new InvalidInputException("Invalid UUID format for adoption paper ID: " + adoptionpaperId);
        }
        adoptionPaperService.deleteAdoptionPaper(adopterId,adoptionpaperId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private boolean isValidUUID(String str) {
        try {
            UUID.fromString(str);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }


}
