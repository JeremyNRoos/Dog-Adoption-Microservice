package com.roos.adoptioncenter.apigateway.businesslayer.dog;

import com.roos.adoptioncenter.apigateway.presentationlayer.dog.DogRequestModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.dog.DogResponseModel;

import java.util.List;

public interface DogService {

    public List<DogResponseModel> getAllDogs(String locationId);

    public DogResponseModel getDogById(String locationId,String dogId);

    public DogResponseModel addDog(String locationId,DogRequestModel dogRequestModel);

    public DogResponseModel updateDog(String locationId,DogRequestModel dogRequestModel, String dogId);

    public void deleteDog(String locationId,String dogId);
}
