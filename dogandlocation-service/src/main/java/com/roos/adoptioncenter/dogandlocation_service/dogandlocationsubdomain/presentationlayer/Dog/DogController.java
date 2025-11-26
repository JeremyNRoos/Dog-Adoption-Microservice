package com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.presentationlayer.Dog;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.businesslayer.Dog.DogService;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.mappinglayer.Dog.DogRequestMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/locations/{locationId}/dogs")
public class DogController {

    private final DogService dogService;

    public DogController(DogService dogService) {
        this.dogService = dogService;
    }

    @GetMapping()
    public ResponseEntity<List<DogResponseModel>> getDogs(@PathVariable String locationId){
        List<DogResponseModel> dogList = dogService.getDogs(locationId);
        return ResponseEntity.ok().body(dogList);
    }

    @GetMapping("/{dogId}")
    public ResponseEntity<DogResponseModel> getDogById(@PathVariable String locationId, @PathVariable String dogId){
        return ResponseEntity.ok().body(dogService.getDogById(locationId,dogId));
    }

    @PostMapping()
    public ResponseEntity<DogResponseModel> addDog(@RequestBody DogRequestModel dogRequestModel, @PathVariable String locationId){
        return new ResponseEntity<>(dogService.addDog(dogRequestModel, locationId), HttpStatus.CREATED);
    }
    @PutMapping("/{dogId}")
    public ResponseEntity<DogResponseModel> updateDog(@PathVariable String locationId,@PathVariable String dogId, @RequestBody DogRequestModel dogRequestModel){
        return new ResponseEntity<>(dogService.updateDog(locationId,dogRequestModel, dogId), HttpStatus.OK);
    }
    @DeleteMapping("/{dogId}")
    public ResponseEntity<Void> deleteDog(@PathVariable String locationId,@PathVariable String dogId){
        dogService.removeDog(locationId,dogId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
