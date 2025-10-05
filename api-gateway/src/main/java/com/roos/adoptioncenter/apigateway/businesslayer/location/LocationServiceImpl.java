package com.roos.adoptioncenter.apigateway.businesslayer.location;

import com.roos.adoptioncenter.apigateway.DomainClientLayer.location.LocationServiceClient;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
import com.roos.adoptioncenter.apigateway.presentationlayer.adopter.AdopterController;
import com.roos.adoptioncenter.apigateway.presentationlayer.adopter.AdopterResponseModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.location.LocationController;
import com.roos.adoptioncenter.apigateway.presentationlayer.location.LocationRequestModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.location.LocationResponseModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class LocationServiceImpl implements LocationService{
    private final LocationServiceClient locationServiceClient;

    public LocationServiceImpl(LocationServiceClient locationServiceClient) {
        this.locationServiceClient = locationServiceClient;
    }

    @Override
    public LocationResponseModel addLocation(LocationRequestModel location) {
        return this.addLinks(locationServiceClient.addLocation(location));
    }

    @Override
    public LocationResponseModel updateLocation(String locationId, LocationRequestModel location) {
        return this.addLinks(locationServiceClient.updateLocation(locationId, location));
    }

    @Override
    public LocationResponseModel getLocationById(String locationId) {
        var location = locationServiceClient.getLocationById(locationId);
        if (location == null) {
            throw new NotFoundException("Location with ID " + locationId + " not found.");
        }
        return addLinks(location);
    }


    @Override
    public List<LocationResponseModel> getAllLocations() {
        return this.locationServiceClient.getAllLocations().stream().map(this::addLinks).toList();
    }

    @Override
    public void deleteLocation(String locationId) {
        locationServiceClient.deleteLocation(locationId);
    }

    private LocationResponseModel addLinks(LocationResponseModel location) {
        Link selfLink = linkTo(methodOn(LocationController.class)
                .getLocationById(location.getLocationId()))
                .withSelfRel();
        location.add(selfLink);

        Link allLocationsLink = linkTo(methodOn(LocationController.class)
                .getAllLocations())
                .withRel("locations");
        location.add(allLocationsLink);

        return location;
    }
}
