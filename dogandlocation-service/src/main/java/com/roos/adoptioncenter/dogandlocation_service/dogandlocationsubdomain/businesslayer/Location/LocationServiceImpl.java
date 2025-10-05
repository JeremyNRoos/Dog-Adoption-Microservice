package com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.businesslayer.Location;


import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Dog.Dog;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Dog.DogRepository;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Location.Location;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Location.LocationAddress;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Location.LocationIdentifier;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Location.LocationRepository;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.mappinglayer.Location.LocationRequestMapper;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.mappinglayer.Location.LocationResponseMapper;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.presentationlayer.Location.LocationRequestModel;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.presentationlayer.Location.LocationResponseModel;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.utils.exceptions.InvalidInputException;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.utils.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final LocationRequestMapper locationRequestMapper;
    private final LocationResponseMapper locationResponseMapper;
    private final DogRepository dogRepository;


    public LocationServiceImpl(LocationRepository locationRepository, LocationRequestMapper locationRequestMapper, LocationResponseMapper locationResponseMapper, DogRepository dogRepository) {
        this.locationRepository = locationRepository;
        this.locationRequestMapper = locationRequestMapper;
        this.locationResponseMapper = locationResponseMapper;
        this.dogRepository = dogRepository;
    }

    @Override
    public List<LocationResponseModel> getLocations() {
        List<Location> location = locationRepository.findAll();
        return locationResponseMapper.entityListToResponseModelList(location);
    }

    @Override
    public LocationResponseModel getLocationById(String locationId) {
       Location location = getLocationObjectById(locationId);
       return locationResponseMapper.toResponseModel(location);
    }

    @Override
    public LocationResponseModel addLocation(LocationRequestModel locationRequestModel) {
        Location location = locationRequestMapper.requestToEntity(locationRequestModel,
                new LocationIdentifier(),
                new LocationAddress(locationRequestModel.getStreetAddress(), locationRequestModel.getCity(), locationRequestModel.getProvince(), locationRequestModel.getCountry(), locationRequestModel.getPostalCode()));

        validateLocationRequestModel(locationRequestModel);

        return locationResponseMapper.toResponseModel(locationRepository.save(location));
    }

    @Override
    public LocationResponseModel updateLocation(LocationRequestModel requestLocation, String locationId) {

        Location location = getLocationObjectById(locationId);

        validateLocationRequestModel(requestLocation);

        Location updateLocation = locationRequestMapper.requestToEntity(requestLocation,
                new LocationIdentifier(locationId),
                new LocationAddress(requestLocation.getStreetAddress(), requestLocation.getCity(), requestLocation.getProvince(), requestLocation.getCountry(), requestLocation.getPostalCode()));

        location.setLocationIdentifier(updateLocation.getLocationIdentifier());
        location.setAddress(updateLocation.getAddress());
//        location.setKennel(updateLocation.getKennel());
        location.setCapacity(updateLocation.getCapacity());
        location.setAvailableSpace(updateLocation.getAvailableSpace());
        location.setShelterType(updateLocation.getShelterType());
        Location savedLocation = locationRepository.save(location);
        return locationResponseMapper.toResponseModel(savedLocation);
    }

    @Override
    public void deleteLocation(String locationId) {
        Location location = getLocationObjectById(locationId);
        List<Dog> dogsToDelete = dogRepository.findAllByLocationIdentifier_LocationId(locationId);

        dogRepository.deleteAll(dogsToDelete);

        locationRepository.delete(location);
    }

    private void validateLocationRequestModel(LocationRequestModel model) {
        if (model.getName() == null || model.getName().isBlank()) {
            throw new InvalidInputException("Invalid Name: " + model.getName());
        }
        if (model.getCity() == null || model.getCity().isBlank()) {
            throw new InvalidInputException("Invalid City: " + model.getCity());
        }
    }

    private Location getLocationObjectById(String locationid) {
        try {
            UUID.fromString(locationid);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid locationid: " + locationid);
        }

        Location location = this.locationRepository.findLocationByLocationIdentifier_LocationId(locationid);

        if (location == null) {
            throw new NotFoundException("Unknown locationid: " + locationid);
        }

        return location;
    }
}
