package com.roos.adoptioncenter.apigateway.presentationlayer.adoptionPaper;


import com.roos.adoptioncenter.apigateway.businesslayer.adoptionPaper.AdoptionPaperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/v1/adopters/{adopterId}/adoptionpapers")
public class AdoptionPaperController {
    private final AdoptionPaperService adoptionPaperService;

    public AdoptionPaperController(AdoptionPaperService adoptionPaperService) {
        this.adoptionPaperService = adoptionPaperService;
    }


    @GetMapping(
            produces = {"application/json"}
    )
    public ResponseEntity<List<AdoptionPaperResponseModel>> getAdoptionPapers(@PathVariable String adopterId){
        log.debug("adoptionPaper id is:" + adopterId);
        List<AdoptionPaperResponseModel> adoptionPaperResponseModels = adoptionPaperService.getAdoptionPapers(adopterId);
        return ResponseEntity.ok().body(adoptionPaperResponseModels);
    }

    @GetMapping(
            value = {"/{adoptionPaperId}"},
            produces = {"application/json"}
        )
    public ResponseEntity<AdoptionPaperResponseModel> getAdoptionPaperById(@PathVariable String adopterId,@PathVariable String adoptionPaperId){
        log.debug("adoptionPaper id is:" + adoptionPaperId + " and adopter id is: " + adopterId);
        return ResponseEntity.ok().body(adoptionPaperService.getAdoptionPaperById(adopterId,adoptionPaperId));
    }

    @PostMapping(
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    public ResponseEntity<AdoptionPaperResponseModel> addAdoptionPaper(@RequestBody AdoptionPaperRequestModel adoptionPaperRequestModel, @PathVariable String adopterId){
        log.debug("dog id is:" + adoptionPaperRequestModel.getDogId() + " and location id is: " + adoptionPaperRequestModel.getLocationId());
        return new ResponseEntity<>(adoptionPaperService.addAdoptionPaper(adoptionPaperRequestModel, adopterId), HttpStatus.CREATED);
    }

    @PutMapping(
            value ={"/{adoptionPaperId}"},
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    public ResponseEntity<AdoptionPaperResponseModel> UpdateAdoptionPaper(@PathVariable String adopterId,@RequestBody AdoptionPaperRequestModel adoptionPaperRequestModel, @PathVariable String adoptionPaperId){
        log.debug("adoptionPaper id is:" + adoptionPaperId + " and adopter id is: " + adopterId);
        return ResponseEntity.ok().body(adoptionPaperService.updateAdoptionPaper(adopterId,adoptionPaperRequestModel,adoptionPaperId));
    }

    @DeleteMapping(
            value = {"/{adoptionPaperId}"},
            produces = {"application/json"}
    )
    public ResponseEntity<Void> deleteAdoptionPaper(@PathVariable String adopterId,@PathVariable String adoptionPaperId){
        log.debug("adoptionPaper id is:" + adoptionPaperId + " and adopter id is: " + adopterId);
        adoptionPaperService.deleteAdoptionPaper(adopterId,adoptionPaperId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
