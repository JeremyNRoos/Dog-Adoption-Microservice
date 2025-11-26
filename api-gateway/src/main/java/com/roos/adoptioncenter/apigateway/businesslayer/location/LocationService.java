package com.roos.adoptioncenter.apigateway.businesslayer.location;

import com.roos.adoptioncenter.apigateway.presentationlayer.location.LocationRequestModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.location.LocationResponseModel;

import java.util.List;

public interface LocationService {
    public LocationResponseModel addLocation(LocationRequestModel location);
    public LocationResponseModel updateLocation(String locationId, LocationRequestModel location);
    public LocationResponseModel getLocationById(String locationId);
    public List<LocationResponseModel> getAllLocations();
    public void deleteLocation(String locationId);
}
