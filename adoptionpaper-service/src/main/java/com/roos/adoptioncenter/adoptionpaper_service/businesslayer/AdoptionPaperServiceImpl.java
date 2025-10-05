package com.roos.adoptioncenter.adoptionpaper_service.businesslayer;

import com.roos.adoptioncenter.adoptionpaper_service.utils.ExceptionsHandling.DogServiceException;
import com.roos.adoptioncenter.adoptionpaper_service.utils.ExceptionsHandling.NotFoundException;
import com.roos.adoptioncenter.adoptionpaper_service.dataaccesslayer.AdoptionPaper;
import com.roos.adoptioncenter.adoptionpaper_service.dataaccesslayer.AdoptionPaperIdentifier;
import com.roos.adoptioncenter.adoptionpaper_service.dataaccesslayer.AdoptionPaperRepository;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.adopter.AdopterModel;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.adopter.AdopterServiceClient;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.dog.DogModel;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.dog.DogServiceClient;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.dog.LocationModel;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.dog.LocationServiceClient;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.volunteer.VolunteerModel;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.volunteer.VolunteerServiceClient;
import com.roos.adoptioncenter.adoptionpaper_service.mappinglayer.AdoptionPaperRequestMapper;
import com.roos.adoptioncenter.adoptionpaper_service.mappinglayer.AdoptionPaperResponseMapper;
import com.roos.adoptioncenter.adoptionpaper_service.presentationlayer.AdoptionPaperRequestModel;
import com.roos.adoptioncenter.adoptionpaper_service.presentationlayer.AdoptionPaperResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AdoptionPaperServiceImpl implements AdoptionPaperService{

    private final VolunteerServiceClient volunteerService;
    private final AdopterServiceClient adopterService;
    private final DogServiceClient dogService;
    private final LocationServiceClient locationService;
    private final AdoptionPaperRepository adoptionPaperRepository;
    private final AdoptionPaperResponseMapper adoptionPaperResponseMapper;
    private final AdoptionPaperRequestMapper adoptionPaperRequestMapper;

    public AdoptionPaperServiceImpl(VolunteerServiceClient volunteerService, AdopterServiceClient adopterService, DogServiceClient dogService, LocationServiceClient locationService, AdoptionPaperRepository adoptionPaperRepository, AdoptionPaperResponseMapper adoptionPaperResponseMapper, AdoptionPaperRequestMapper adoptionPaperRequestMapper) {
        this.volunteerService = volunteerService;
        this.adopterService = adopterService;
        this.dogService = dogService;
        this.locationService = locationService;
        this.adoptionPaperRepository = adoptionPaperRepository;
        this.adoptionPaperResponseMapper = adoptionPaperResponseMapper;
        this.adoptionPaperRequestMapper = adoptionPaperRequestMapper;
    }


    @Override
    public List<AdoptionPaperResponseModel> getAdoptionPapers(String adopterId) {
        AdopterModel adopterModel = adopterService.getAdopterById(adopterId);
        if(adopterModel == null){
            throw new NotFoundException("No adopter with ID: " + adopterId);
        }

        List<AdoptionPaper> adoptionPapers = adoptionPaperRepository.findAllByAdopterModel_AdopterId(adopterId);
        return adoptionPaperResponseMapper.entityListToResponseModelList(adoptionPapers);
    }

    @Override
    public AdoptionPaperResponseModel getAdoptionPaperById(String adopterId, String adoptionPaperId) {
        AdoptionPaper adoptionPaper = adoptionPaperRepository.findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(adopterId, adoptionPaperId);
        if(adoptionPaper == null){
            throw new NotFoundException("No adoption paper with ID: " + adoptionPaperId);
        }
        return adoptionPaperResponseMapper.toResponseModel(adoptionPaper);
    }

    @Override
    public AdoptionPaperResponseModel addAdoptionPaper(AdoptionPaperRequestModel adoptionPaperRequestModel, String adopterId) {
            if(adoptionPaperRequestModel == null){
            throw new IllegalArgumentException("Invalid identifier provided for volunteer, dog, location, or adopter");
            }

            // Fetch models from domain services with null checks
            VolunteerModel volunteerResponseModel = volunteerService.getVolunteerById(adoptionPaperRequestModel.getVolunteerId());
            if (volunteerResponseModel == null) {
                throw new NotFoundException("Volunteer not found with ID: " + adoptionPaperRequestModel.getVolunteerId());
            }

            AdopterModel adopterResponseModel = adopterService.getAdopterById(adopterId);
            if (adopterResponseModel == null) {
                throw new NotFoundException("Adopter not found with ID: " + adopterId);
            }

            DogModel dogResponseModel;
            try {
                dogResponseModel = dogService.getDogById(adoptionPaperRequestModel.getLocationId(), adoptionPaperRequestModel.getDogId());
            } catch (Exception ex) {
                throw new DogServiceException("Dog retrieval failed: " + ex.getMessage());
            }

            if (dogResponseModel == null) {
                throw new NotFoundException("Dog not found with ID: " + adoptionPaperRequestModel.getDogId() + " at location: " + adoptionPaperRequestModel.getLocationId());
            }


        LocationModel locationResponseModel = locationService.getLocationById(adoptionPaperRequestModel.getLocationId());
            if (locationResponseModel == null) {
                throw new NotFoundException("Location not found with ID: " + adoptionPaperRequestModel.getLocationId());
            }


            if(adopterResponseModel == null || adopterResponseModel.getFName() == null || adopterResponseModel.getLName() == null) {
                throw new NotFoundException("No Adopter found with Id " + adopterId);
            }



//            VolunteerModel volunteerResponseModel = volunteerService.getVolunteerById(adoptionPaperRequestModel.getVolunteerId());
//            AdopterModel adopterResponseModel = adopterService.getAdopterById(adopterId);
//            DogModel dogResponseModel = dogService.getDogById(adoptionPaperRequestModel.getLocationId(), adoptionPaperRequestModel.getDogId());
//            LocationModel locationResponseModel = locationService.getLocationById(adoptionPaperRequestModel.getLocationId());

//            dogService.updateDogAvailability(adoptionPaperRequestModel.getDogIdentifier(), false); Invariant

            AdoptionPaperIdentifier adoptionPaperIdentifier = new AdoptionPaperIdentifier();

            AdoptionPaper adoptionPaper = adoptionPaperRequestMapper.requestToEntity(adoptionPaperRequestModel,
                    adoptionPaperIdentifier,
                    volunteerResponseModel,
                    locationResponseModel,
                    dogResponseModel,
                    adopterResponseModel);

            AdoptionPaper adoptionPaperSaved = adoptionPaperRepository.save(adoptionPaper);
            return adoptionPaperResponseMapper.toResponseModel(adoptionPaperSaved);
    }

//    @Override
//    public AdoptionPaperResponseModel updateAdoptionPaper(AdoptionPaperRequestModel adoptionPaperRequestModel, String adoptionPaperId) {
//        AdoptionPaper adoptionPaper = adoptionPaperRepository.findAdoptionPaperByAdoptionPaperIdentifier_AdoptionPaperId(adoptionPaperId);
//        if(adoptionPaper == null){
//            throw new NotFoundException("No adoption paper with ID: " + adoptionPaperId);
//        }
//
//        DogModel dogModel = dogService.getDogById(adoptionPaperRequestModel.getLocationId(), adoptionPaperRequestModel.getDogId());
//        AdopterModel adopterModel = adopterService.getAdopterById(adoptionPaperRequestModel.getAdopterId());
//        VolunteerModel volunteerModel = volunteerService.getVolunteerById(adoptionPaperRequestModel.getVolunteerId());
//        LocationModel locationModel = locationService.getLocationById(adoptionPaperRequestModel.getLocationId());
//
//        adoptionPaper.setDogModel(dogModel);
//        adoptionPaper.setAdopterModel(adopterModel);
//        adoptionPaper.setVolunteerModel(volunteerModel);
//        adoptionPaper.setLocationModel(locationModel);
//
//        AdoptionPaper existingPaper = adoptionPaperRepository.save(adoptionPaper);
//        return adoptionPaperResponseMapper.toResponseModel(existingPaper);
//    }

    @Override
    public AdoptionPaperResponseModel updateAdoptionPaper(String adopterId, AdoptionPaperRequestModel requestModel, String adoptionPaperId) {
        AdoptionPaper adoptionPaper = adoptionPaperRepository.findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(adopterId, adoptionPaperId);
        if (adoptionPaper == null) {
            throw new NotFoundException("No adoption paper with ID: " + adoptionPaperId);
        }

        // Retrieve external resources with proper exception handling
        DogModel dogModel;
        try {
            dogModel = dogService.getDogById(requestModel.getLocationId(), requestModel.getDogId());
        } catch (Exception ex) {
            // Explicitly rethrow DogServiceException
            throw new DogServiceException("Dog retrieval failed: " + ex.getMessage());
        }

        AdopterModel adopterModel = adopterService.getAdopterById(adopterId);
        if (adopterModel == null) {
            throw new NotFoundException("Adopter not found with ID: " + adopterId);
        }

        VolunteerModel volunteerModel = volunteerService.getVolunteerById(requestModel.getVolunteerId());
        if (volunteerModel == null) {
            throw new NotFoundException("Volunteer not found with ID: " + requestModel.getVolunteerId());
        }

        LocationModel locationModel = locationService.getLocationById(requestModel.getLocationId());
        if (locationModel == null) {
            throw new NotFoundException("Location not found with ID: " + requestModel.getLocationId());
        }
        if(adopterModel.getFName() == null || adopterModel.getLName() == null){
            throw new NotFoundException("Incomplete Adopter data for ID: " + adopterId);
        }


        AdoptionPaper updatedEntity = adoptionPaperRequestMapper.requestToEntity(
                requestModel,
                adoptionPaper.getAdoptionPaperIdentifier(),
                volunteerModel,
                locationModel,
                dogModel,
                adopterModel
        );

        adoptionPaper.setDogModel(updatedEntity.getDogModel());
        adoptionPaper.setAdopterModel(updatedEntity.getAdopterModel());
        adoptionPaper.setVolunteerModel(updatedEntity.getVolunteerModel());
        adoptionPaper.setLocationModel(updatedEntity.getLocationModel());

        AdoptionPaper savedEntity = adoptionPaperRepository.save(adoptionPaper);
        return adoptionPaperResponseMapper.toResponseModel(savedEntity);
    }


    @Override
    public void deleteAdoptionPaper(String adopterId, String adoptionPaperId) {
        AdoptionPaper adoptionPaper = adoptionPaperRepository.findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(adopterId, adoptionPaperId);
        if(adoptionPaper == null){
            throw new NotFoundException("No adoption paper with ID" + adoptionPaperId);
        }
        adoptionPaperRepository.delete(adoptionPaper);
    }
}
