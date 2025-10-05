package com.roos.adoptioncenter.apigateway.businesslayer.dog;

import com.roos.adoptioncenter.apigateway.DomainClientLayer.dog.DogServiceClient;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
import com.roos.adoptioncenter.apigateway.presentationlayer.adopter.AdopterController;
import com.roos.adoptioncenter.apigateway.presentationlayer.adopter.AdopterResponseModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.dog.DogController;
import com.roos.adoptioncenter.apigateway.presentationlayer.dog.DogRequestModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.dog.DogResponseModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class DogServiceImpl implements DogService {
    private final DogServiceClient dogServiceClient;

    public DogServiceImpl(DogServiceClient dogServiceClient) {
        this.dogServiceClient = dogServiceClient;
    }

    @Override
    public List<DogResponseModel> getAllDogs(String locationId) {
        return this.dogServiceClient.getAllDogs(locationId).stream().map(this::addLinks).toList();

    }

    @Override
    public DogResponseModel getDogById(String adopterId, String dogId) {
        DogResponseModel dogResponseModel = dogServiceClient.getDogById(adopterId, dogId);
        if (dogResponseModel == null) {
            throw new NotFoundException("Dog with ID " + dogId + " not found.");
        }
        return addLinks(dogResponseModel);
    }


    @Override
    public DogResponseModel addDog(String locationId,DogRequestModel dogRequestModel) {
        return this.addLinks(dogServiceClient.addDog(locationId,dogRequestModel));

    }

    @Override
    public DogResponseModel updateDog(String locationId,DogRequestModel dogRequestModel, String dogId) {
        return this.addLinks(dogServiceClient.updateDog(locationId,dogRequestModel, dogId));


    }

    @Override
    public void deleteDog(String locationId,String dogId) {
        dogServiceClient.deleteDog(locationId,dogId);
    }

    private DogResponseModel addLinks(DogResponseModel dogResponseModel) {
        Link selflink = linkTo(methodOn(DogController.class)
                .getDogById(dogResponseModel.getLocationId(), dogResponseModel.getDogId()))
                .withSelfRel();
        dogResponseModel.add(selflink);

        Link allDogsLink = linkTo(methodOn(DogController.class)
                .getAllDogs(dogResponseModel.getDogId()))
                .withRel("allDogs");
        dogResponseModel.add(allDogsLink);

        return dogResponseModel;
    }



}





