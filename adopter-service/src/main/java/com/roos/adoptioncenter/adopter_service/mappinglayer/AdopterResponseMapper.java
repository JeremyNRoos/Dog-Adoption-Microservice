package com.roos.adoptioncenter.adopter_service.mappinglayer;

import com.roos.adoptioncenter.adopter_service.dataaccesslayer.Adopter;
import com.roos.adoptioncenter.adopter_service.presentation.AdopterController;
import com.roos.adoptioncenter.adopter_service.presentation.AdopterResponseModel;
import org.mapstruct.*;
import org.springframework.hateoas.Link;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Mapper(componentModel = "spring")
public interface AdopterResponseMapper {
    @Mappings({
            @Mapping(source = "adopterIdentifier.adopterId", target = "adopterId"),
//            @Mapping(source = "fName", target = "fName"),
//            @Mapping(source = "lName", target = "lName"),
            @Mapping(source = "adopterAddress", target = "Address"),
            @Mapping(source = "adopterPhoneNumber", target = "adopterPhoneNumber"),
            @Mapping(source = "contactMethodPreference", target = "contactMethodPreference")
    })
    AdopterResponseModel toResponseModel(Adopter adopter);

    List<AdopterResponseModel> entityListToResponseModelList(List<Adopter> Adopter);


    @AfterMapping
    default void addLinks(@MappingTarget AdopterResponseModel adopterResponseModel, Adopter adopter) {
        Link selflink = linkTo(methodOn(AdopterController.class)
                .getAdopterById(adopterResponseModel.getAdopterId()))
                .withSelfRel();
        adopterResponseModel.add(selflink);

        Link allAdopterLink = linkTo(methodOn(AdopterController.class)
                .getAdopters())
                .withRel("allAdopters");
        adopterResponseModel.add(allAdopterLink);
    }
}
