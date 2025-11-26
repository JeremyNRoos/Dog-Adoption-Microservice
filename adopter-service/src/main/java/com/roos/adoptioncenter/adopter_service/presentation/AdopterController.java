package com.roos.adoptioncenter.adopter_service.presentation;

import com.roos.adoptioncenter.adopter_service.businesslayer.AdopterService;
import com.roos.adoptioncenter.adopter_service.businesslayer.AdopterServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/adopters")
public class AdopterController {

    private final AdopterService adopterService;

    public AdopterController(AdopterServiceImpl adopterService) {
        this.adopterService = adopterService;
    }

    @GetMapping()
    public ResponseEntity<List<AdopterResponseModel>> getAdopters(){
        List<AdopterResponseModel> responseModels = adopterService.getAdopters();
        return ResponseEntity.ok().body(responseModels);
    }

    @GetMapping("/{adopterId}")
    public ResponseEntity<AdopterResponseModel> getAdopterById(@PathVariable String adopterId){
        AdopterResponseModel responseModel = adopterService.getAdopterById(adopterId);
        return ResponseEntity.ok().body(responseModel);
    }

    @PostMapping()
    public ResponseEntity<AdopterResponseModel> addAdopter(@RequestBody AdopterRequestModel adopterRequestModel){
        return new ResponseEntity<>(adopterService.addAdopter(adopterRequestModel), HttpStatus.CREATED);
    }

    @PutMapping("/{adopterId}")
    public ResponseEntity<AdopterResponseModel> updateAdopter(@RequestBody AdopterRequestModel adopterRequestModel, @PathVariable String adopterId){
        return new ResponseEntity<>(adopterService.updateAdopter(adopterRequestModel, adopterId), HttpStatus.OK);
    }

    @DeleteMapping("/{adopterId}")
    public ResponseEntity<Void> removeAdopter(@PathVariable String adopterId){
        adopterService.deleteAdopter(adopterId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
