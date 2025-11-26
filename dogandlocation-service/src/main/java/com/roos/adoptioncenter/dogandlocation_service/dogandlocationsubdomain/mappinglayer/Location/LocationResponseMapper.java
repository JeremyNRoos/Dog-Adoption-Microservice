package com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.mappinglayer.Location;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Location.Location;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.presentationlayer.Location.LocationController;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.presentationlayer.Location.LocationResponseModel;
import org.mapstruct.*;
import org.springframework.hateoas.Link;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Mapper(componentModel = "spring")
public interface LocationResponseMapper {
    @Mappings(
            {
                    @Mapping(source = "locationIdentifier.locationId", target = "locationId"),
                    @Mapping(source = "name", target = "name"),
                    @Mapping(source = "shelterType", target = "shelterType"),
                    @Mapping(source = "address", target = "address"),
                    @Mapping(source = "capacity", target = "capacity"),
                    @Mapping(source = "availableSpace", target = "availableSpace"),
//                    @Mapping(source = "kennel", target = "kennel")
            }
    )
    LocationResponseModel toResponseModel(Location location);

    List<LocationResponseModel> entityListToResponseModelList(List<Location> locations);


    @AfterMapping
    default void addLinks(@MappingTarget LocationResponseModel locationResponseModel, Location location) {
        Link selflink = linkTo(methodOn(LocationController.class)
                .getLocationById(locationResponseModel.getLocationId()))
                .withSelfRel();
        locationResponseModel.add(selflink);

        Link allLocationLink = linkTo(methodOn(LocationController.class)
                .getLocations())
                .withRel("allLocations");
        locationResponseModel.add(allLocationLink);
    }
}
