package com.roos.adoptioncenter.apigateway.businesslayer.adopter;

import com.roos.adoptioncenter.apigateway.DomainClientLayer.adopter.AdopterServiceClient;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
import com.roos.adoptioncenter.apigateway.presentationlayer.adopter.AdopterController;
import com.roos.adoptioncenter.apigateway.presentationlayer.adopter.AdopterRequestModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.adopter.AdopterResponseModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class AdopterServiceImpl implements AdopterService {
    private final AdopterServiceClient adopterServiceCLient;

    public AdopterServiceImpl(AdopterServiceClient adopterServiceCLient) {
        this.adopterServiceCLient = adopterServiceCLient;
    }

    @Override
    public AdopterResponseModel createAdopter(AdopterRequestModel adopterRequestModel) {
        return this.addLinks(adopterServiceCLient.addAdopter(adopterRequestModel));
    }

    @Override
    public AdopterResponseModel getAdopterById(String adopterId) {
        AdopterResponseModel adopter = adopterServiceCLient.getAdopterById(adopterId);
        if (adopter == null) {
            throw new NotFoundException("Adopter with ID " + adopterId + " not found.");
        }
        return addLinks(adopter);
    }


    @Override
    public List<AdopterResponseModel> getAllAdopters() {
        return this.adopterServiceCLient.getAllAdopters().stream().map(this::addLinks).toList();
    }

    @Override
    public AdopterResponseModel updateAdopter(AdopterRequestModel adopterRequestModel, String adopterId) {
        return this.addLinks(adopterServiceCLient.updateAdopter(adopterRequestModel, adopterId));
    }

    @Override
    public void deleteAdopter(String adopterId) {
        adopterServiceCLient.deleteAdopter(adopterId);
    }

    private AdopterResponseModel addLinks(AdopterResponseModel adopter) {
        Link selfLink = linkTo(methodOn(AdopterController.class)
                .getAdopterById(adopter.getAdopterId()))
                .withSelfRel();
        adopter.add(selfLink);

        Link allAdoptersLink = linkTo(methodOn(AdopterController.class)
                .getAllAdopters())
                .withRel("adopters");
        adopter.add(allAdoptersLink);

        return adopter;
    }
}
