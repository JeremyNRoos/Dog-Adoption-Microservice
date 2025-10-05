package com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.businesslayer.Dog;

import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Dog.*;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Location.Location;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Location.LocationIdentifier;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Location.LocationRepository;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.mappinglayer.Dog.DogRequestMapper;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.mappinglayer.Dog.DogResponseMapper;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.presentationlayer.Dog.DogRequestModel;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.presentationlayer.Dog.DogResponseModel;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.utils.exceptions.InvalidInputException;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Slf4j
@Service
public class DogServiceImpl implements DogService {

    private final DogRepository dogRepository;
    private final LocationRepository locationRepository;
    private final DogRequestMapper dogRequestMapper;
    private final DogResponseMapper dogResponseMapper;

    public DogServiceImpl(DogRepository dogRepository, LocationRepository locationRepository, DogRequestMapper dogRequestMapper, DogResponseMapper dogResponseMapper) {
        this.dogRepository = dogRepository;
        this.locationRepository = locationRepository;
        this.dogRequestMapper = dogRequestMapper;
        this.dogResponseMapper = dogResponseMapper;
    }


    @Override
    public List<DogResponseModel> getDogs(String locationId) {
        Location locationDog = locationRepository.findLocationByLocationIdentifier_LocationId(locationId);
        if(locationDog == null){
            throw new NotFoundException("Dog in location id " + locationId + " was not found.");
        }
        List<Dog> dog = dogRepository.findAllByLocationIdentifier_LocationId(locationId);
        return dogResponseMapper.entityListToResponseModelList(dog);
    }

    @Override
    public DogResponseModel getDogById(String locationId, String dogId) {
        Dog dogFound = getDogObjectById(locationId,dogId);

        return dogResponseMapper.toResponseModel(dogFound);

    }

    @Override
    public DogResponseModel addDog(DogRequestModel dogRequestModel, String locationId) {
        Dog dog = dogRequestMapper.requestToEntity(dogRequestModel,
                new DogIdentifier(),
                new LocationIdentifier(locationId),
                new Kennel(dogRequestModel.getDogKennel().getKennelSize()));

        validateDogRequestModel(dogRequestModel);

        return dogResponseMapper.toResponseModel(dogRepository.save(dog));

    }

    @Override
    public DogResponseModel updateDog(String locationId ,DogRequestModel dogRequestModel, String dogId) {

        validateDogRequestModel(dogRequestModel);

        Dog updateDog = dogRequestMapper.requestToEntity(dogRequestModel, new DogIdentifier(dogId),
                new LocationIdentifier(dogRequestModel.locationId),
                new Kennel(dogRequestModel.getDogKennel().getKennelSize()));
            Dog dog1 = getDogObjectById(locationId,dogId);

            dog1.setDogIdentifier(updateDog.getDogIdentifier());
            dog1.setAge(updateDog.getAge());
            dog1.setName(updateDog.getName());
            dog1.setBreed(updateDog.getBreed());
            dog1.setAvailabilityStatus(updateDog.getAvailabilityStatus());
            dog1.setVaccinationStatus(updateDog.getVaccinationStatus());
            Dog dogCreated = dogRepository.save(dog1);
            return dogResponseMapper.toResponseModel(dogCreated);
    }

    @Override
    public void removeDog(String locationId,String DogId) {
        Dog dog = getDogObjectById(locationId,DogId);
        dogRepository.delete(dog);
    }
    @Override
    public void updateDogAvailability(String dogId, boolean isAvailable){
        Dog dog = dogRepository.findDogByDogIdentifier_DogId(dogId);
        if(dog != null){
            dog.setAvailabilityStatus(isAvailable ? AvailabilityStatusEnum.AVAILABLE : AvailabilityStatusEnum.ADOPTED);
            dogRepository.save(dog);
        }
    }

    private void validateDogRequestModel(DogRequestModel model) {
        if (model.getName() == null || model.getName().isBlank()) {
            throw new InvalidInputException("Invalid Name: " + model.getName());
        }
    }

    private Dog getDogObjectById(String locationid,String dogid) {
        try {
            UUID.fromString(dogid);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid dogid: " + dogid);
        }

        Dog dog = this.dogRepository.findDogByLocationIdentifier_LocationIdAndDogIdentifier_DogId(locationid,dogid);

        if (dog == null) {
            throw new NotFoundException("Unknown dogid: " + dogid);
        }

        return dog;
    }
}
