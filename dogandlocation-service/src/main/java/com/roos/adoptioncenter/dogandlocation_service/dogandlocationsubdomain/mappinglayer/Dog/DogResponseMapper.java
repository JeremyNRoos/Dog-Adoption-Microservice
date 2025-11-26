package com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.mappinglayer.Dog;

import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Dog.Dog;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.presentationlayer.Dog.DogController;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.presentationlayer.Dog.DogResponseModel;
import org.mapstruct.*;
import org.springframework.hateoas.Link;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Mapper(componentModel = "spring")
public interface DogResponseMapper {

    @Mappings(
            {
                    @Mapping(source = "dogIdentifier.dogId", target = "dogId"),
                    @Mapping(source = "locationIdentifier.locationId", target = "locationId"),
                    @Mapping(source = "name", target = "name"),
                    @Mapping(source = "breed", target = "breed"),
                    @Mapping(source = "age", target = "age"),
                    @Mapping(source = "dogKennel", target = "dogKennel"),
                    @Mapping(source = "vaccinationStatus", target = "vaccinationStatus"),
                    @Mapping(source = "availabilityStatus", target = "availabilityStatus"),
            }
    )
    DogResponseModel toResponseModel(Dog dog);

    List<DogResponseModel> entityListToResponseModelList(List<Dog> dogs);


    @AfterMapping
    default void addLinks(@MappingTarget DogResponseModel dogResponseModel, Dog dog) {
        Link selflink = linkTo(methodOn(DogController.class)
                .getDogById(dogResponseModel.getLocationId(), dogResponseModel.getDogId()))
                .withSelfRel();
        dogResponseModel.add(selflink);

        Link allDogsLink = linkTo(methodOn(DogController.class)
                .getDogs(dogResponseModel.getLocationId()))
                .withRel("allDogs");
        dogResponseModel.add(allDogsLink);
    }
}
