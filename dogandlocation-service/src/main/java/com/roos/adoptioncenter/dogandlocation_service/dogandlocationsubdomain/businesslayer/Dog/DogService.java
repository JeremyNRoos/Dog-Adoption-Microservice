package com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.businesslayer.Dog;

import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.presentationlayer.Dog.DogRequestModel;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.presentationlayer.Dog.DogResponseModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface DogService {
    public List<DogResponseModel> getDogs(String locationId);
    DogResponseModel getDogById(String locationId, String dogId);
    DogResponseModel addDog(DogRequestModel dogRequestModel, String locationId);
    DogResponseModel updateDog(String locationId,DogRequestModel dog, String dogId);
    void removeDog(String locationId,String DogId);

    void updateDogAvailability(String dogId, boolean isAvailable);
}
