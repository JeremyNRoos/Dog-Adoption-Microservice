package com.roos.adoptioncenter.apigateway.businesslayer.adoptionPaper;

import com.roos.adoptioncenter.apigateway.DomainClientLayer.adoptionpaper.AdoptionPaperServiceClient;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
import com.roos.adoptioncenter.apigateway.presentationlayer.adoptionPaper.AdoptionPaperController;
import com.roos.adoptioncenter.apigateway.presentationlayer.adoptionPaper.AdoptionPaperRequestModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.adoptionPaper.AdoptionPaperResponseModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import java.util.List;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class AdoptionPaperServiceImpl implements AdoptionPaperService {

    private final AdoptionPaperServiceClient adoptionPaperServiceClient;

    public AdoptionPaperServiceImpl(AdoptionPaperServiceClient adoptionPaperServiceClient) {
        this.adoptionPaperServiceClient = adoptionPaperServiceClient;
    }


    @Override
    public List<AdoptionPaperResponseModel> getAdoptionPapers(String adopterId) {
        return this.adoptionPaperServiceClient.getAllAdoptioPapers(adopterId).stream().map(this::addLinks).toList();
    }



    @Override
    public AdoptionPaperResponseModel getAdoptionPaperById(String adopterId, String adoptionPaperId) {
        AdoptionPaperResponseModel adoptionPaper = adoptionPaperServiceClient.getAdoptionById(adopterId, adoptionPaperId);

        if (adoptionPaper == null) {
            throw new NotFoundException("Adoption paper with ID " + adoptionPaperId + " for adopter " + adopterId + " not found.");
        }

        return addLinks(adoptionPaper);
    }


    @Override
    public AdoptionPaperResponseModel addAdoptionPaper(AdoptionPaperRequestModel adoptionPaperRequestModel, String adopterId) {
        return this.addLinks(adoptionPaperServiceClient.addAdoptionPaper(adopterId ,adoptionPaperRequestModel));
    }

    @Override
    public AdoptionPaperResponseModel updateAdoptionPaper(String adopterId,AdoptionPaperRequestModel adoptionPaperRequestModel, String adoptionPaperId) {
        return this.addLinks(adoptionPaperServiceClient.updateAdoptionPaper(adopterId ,adoptionPaperRequestModel, adoptionPaperId));
    }

    @Override
    public void deleteAdoptionPaper(String adopterId,String adoptionPaperId) {
        adoptionPaperServiceClient.deleteAdoptionPaper(adopterId ,adoptionPaperId);
    }

    private AdoptionPaperResponseModel addLinks(AdoptionPaperResponseModel adoptionPaper) {
        Link selfLink = linkTo(methodOn(AdoptionPaperController.class)
                .getAdoptionPaperById(adoptionPaper.getAdopterId(),adoptionPaper.getAdoptionPaperId()))
                .withSelfRel();
        adoptionPaper.add(selfLink);

        Link allAdoptionPaperLink = linkTo(methodOn(AdoptionPaperController.class)
                .getAdoptionPapers(adoptionPaper.getAdoptionPaperId()))
                .withRel("adoptionPapers");
        adoptionPaper.add(allAdoptionPaperLink);

        return adoptionPaper;
    }
}
