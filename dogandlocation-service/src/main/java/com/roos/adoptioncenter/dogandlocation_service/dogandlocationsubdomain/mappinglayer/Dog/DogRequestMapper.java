package com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.mappinglayer.Dog;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Dog.Dog;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Dog.DogIdentifier;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Dog.Kennel;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Location.LocationIdentifier;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.presentationlayer.Dog.DogRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface DogRequestMapper {

    Dog requestToEntity(DogRequestModel dogRequestModel, DogIdentifier dogIdentifier, LocationIdentifier locationIdentifier);

    @Mappings({
            @Mapping(expression = "java(dogIdentifier)", target = "dogIdentifier"),
            @Mapping(expression = "java(locationIdentifier)", target = "locationIdentifier"),
            @Mapping(source = "dogRequestModel.name", target = "name"),
            @Mapping(source = "dogRequestModel.breed", target = "breed"),
            @Mapping(source = "dogRequestModel.age", target = "age"),
            @Mapping(expression = "java(dogKennel)", target = "dogKennel"),
            @Mapping(source = "dogRequestModel.vaccinationStatus", target = "vaccinationStatus"),
            @Mapping(source = "dogRequestModel.availabilityStatus", target = "availabilityStatus"),
    })
    Dog requestToEntity(DogRequestModel dogRequestModel, DogIdentifier dogIdentifier, LocationIdentifier locationIdentifier, Kennel dogKennel);


}
