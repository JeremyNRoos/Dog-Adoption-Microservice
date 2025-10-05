package com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.mappinglayer.Location;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Location.Location;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Location.LocationAddress;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Location.LocationIdentifier;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.presentationlayer.Location.LocationRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface LocationRequestMapper {

//    Location requestToEntity(LocationRequestModel locationRequestModel, LocationIdentifier locationIdentifier, LocationAddress locationAddress, Kennel kennel);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(expression = "java(locationIdentifier)", target = "locationIdentifier"),
            @Mapping(source = "locationRequestModel.name", target = "name"),
            @Mapping(source = "locationRequestModel.shelterType", target = "shelterType"),
            @Mapping(expression = "java(locationAddress)", target = "address"),
            @Mapping(source = "locationRequestModel.capacity", target = "capacity"),
            @Mapping(source = "locationRequestModel.availableSpace", target = "availableSpace"),
//            @Mapping(expression = "java(kennel)", target = "kennel")
    })
    Location requestToEntity(LocationRequestModel locationRequestModel, LocationIdentifier locationIdentifier, LocationAddress locationAddress);
}
