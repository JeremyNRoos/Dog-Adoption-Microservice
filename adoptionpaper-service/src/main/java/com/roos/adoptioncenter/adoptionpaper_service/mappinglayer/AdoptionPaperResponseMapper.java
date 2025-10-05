package com.roos.adoptioncenter.adoptionpaper_service.mappinglayer;

import com.roos.adoptioncenter.adoptionpaper_service.dataaccesslayer.AdoptionPaper;
import com.roos.adoptioncenter.adoptionpaper_service.presentationlayer.AdoptionPaperController;
import com.roos.adoptioncenter.adoptionpaper_service.presentationlayer.AdoptionPaperResponseModel;
import org.mapstruct.*;
import org.springframework.hateoas.Link;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Mapper(componentModel = "spring")
public interface AdoptionPaperResponseMapper {

    @Mappings({
            @Mapping(source = "adoptionPaperIdentifier.adoptionPaperId", target = "adoptionPaperId"),

            @Mapping(source = "adopterModel.adopterId", target = "adopterId"),
            @Mapping(source = "adopterModel.FName", target = "adopterFName"),
            @Mapping(source = "adopterModel.LName", target = "adopterLName"),

            @Mapping(source = "volunteerModel.volunteerId", target = "volunteerId"),
            @Mapping(source = "volunteerModel.FName", target = "volunteerFName"),
            @Mapping(source = "volunteerModel.LName", target = "volunteerLName"),


            @Mapping(source = "locationModel.locationId", target = "locationId"),
            @Mapping(source = "locationModel.name", target = "locationName"),
            @Mapping(source = "locationModel.shelterType", target = "locationShelterType"),


            @Mapping(source = "dogModel.dogId", target = "dogId"),
            @Mapping(source = "dogModel.name", target = "dogName"),
            @Mapping(source = "dogModel.age", target = "dogAge"),

    })
    AdoptionPaperResponseModel toResponseModel(AdoptionPaper adoptionPaper);

    List<AdoptionPaperResponseModel> entityListToResponseModelList(List<AdoptionPaper> adoptionPapers);

    @AfterMapping
    default void addLinks(@MappingTarget AdoptionPaperResponseModel adoptionPaperResponseModel, AdoptionPaper adoptionPaper) {
        Link selflink = linkTo(methodOn(AdoptionPaperController.class)
                .getAdoptionPaperById(adoptionPaperResponseModel.getAdopterId(),adoptionPaperResponseModel.getAdoptionPaperId()))
                .withSelfRel();
        adoptionPaperResponseModel.add(selflink);

        Link allAdoptionLink = linkTo(methodOn(AdoptionPaperController.class)
                .getAdoptionPapers(adoptionPaperResponseModel.getAdoptionPaperId()))
                .withRel("allAdoptions");
        adoptionPaperResponseModel.add(allAdoptionLink);
    }

}

