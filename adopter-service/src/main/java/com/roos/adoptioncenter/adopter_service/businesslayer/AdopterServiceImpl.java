package com.roos.adoptioncenter.adopter_service.businesslayer;

import com.roos.adoptioncenter.adopter_service.dataaccesslayer.Adopter;
import com.roos.adoptioncenter.adopter_service.dataaccesslayer.AdopterIdentifier;
import com.roos.adoptioncenter.adopter_service.dataaccesslayer.AdopterRepository;
import com.roos.adoptioncenter.adopter_service.mappinglayer.AdopterRequestMapper;
import com.roos.adoptioncenter.adopter_service.mappinglayer.AdopterResponseMapper;
import com.roos.adoptioncenter.adopter_service.presentation.AdopterRequestModel;
import com.roos.adoptioncenter.adopter_service.presentation.AdopterResponseModel;
import com.roos.adoptioncenter.adopter_service.utils.exceptions.InvalidInputException;
import com.roos.adoptioncenter.adopter_service.utils.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AdopterServiceImpl implements AdopterService {
    private final AdopterRepository adopterRepository;
    private final AdopterRequestMapper adopterRequestMapper;
    private final AdopterResponseMapper adopterResponseMapper;

    public AdopterServiceImpl(AdopterRepository adopterRepository, AdopterRequestMapper adopterRequestMapper, AdopterResponseMapper adopterResponseMapper) {
        this.adopterRepository = adopterRepository;
        this.adopterRequestMapper = adopterRequestMapper;
        this.adopterResponseMapper = adopterResponseMapper;
    }

    @Override
    public List<AdopterResponseModel> getAdopters() {
        List<Adopter> adopter = adopterRepository.findAll();
        return adopterResponseMapper.entityListToResponseModelList(adopter);
    }

    @Override
    public AdopterResponseModel getAdopterById(String adopterId) {
        Adopter adopter = getAdopterObjectById(adopterId);
        return adopterResponseMapper.toResponseModel(adopter);
    }

    @Override
    public AdopterResponseModel addAdopter(AdopterRequestModel adopterRequestModel) {

        validateAdopterRequestModel(adopterRequestModel);

        Adopter adopter = adopterRequestMapper.requestToEntity(adopterRequestModel,
                new AdopterIdentifier(),
                adopterRequestModel.getAddress(),
                adopterRequestModel.getPhoneNumber());
        if(adopter == null){
            throw new NotFoundException("could not create entity from request model");
        }
        adopterRepository.save(adopter);
        return adopterResponseMapper.toResponseModel(adopter);
    }

    @Override
    public AdopterResponseModel updateAdopter(AdopterRequestModel adopterRequestModel, String adopterId) {

        validateAdopterRequestModel(adopterRequestModel);

        Adopter updateAdopter = adopterRequestMapper.requestToEntity(adopterRequestModel,
                new AdopterIdentifier(adopterId),
                adopterRequestModel.getAddress(),
                adopterRequestModel.getPhoneNumber());

        Adopter adopter = getAdopterObjectById(adopterId);
        adopter.setAdopterIdentifier(updateAdopter.getAdopterIdentifier());
        adopter.setAdopterAddress(updateAdopter.getAdopterAddress());
        adopter.setAdopterPhoneNumber(updateAdopter.getAdopterPhoneNumber());
        adopter.setFName(updateAdopter.getFName());
        adopter.setLName(updateAdopter.getLName());
        adopter.setContactMethodPreference(updateAdopter.getContactMethodPreference());
        Adopter savedAdopter = adopterRepository.save(adopter);
        return adopterResponseMapper.toResponseModel(savedAdopter);
    }

    @Override
    public void deleteAdopter(String adopterId) {
        Adopter adopter = getAdopterObjectById(adopterId);
        adopterRepository.delete(adopter);
    }

    private void validateAdopterRequestModel(AdopterRequestModel model) {
        if (model.getFName() == null || model.getFName().isBlank()) {
            throw new InvalidInputException("Invalid firstName: " + model.getFName());
        }
        if (model.getLName() == null || model.getLName().isBlank()) {
            throw new InvalidInputException("Invalid lastName: " + model.getLName());
        }
    }

    private Adopter getAdopterObjectById(String adopterid) {
        try {
            UUID.fromString(adopterid);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid adopterid: " + adopterid);
        }

        Adopter adopter = this.adopterRepository.findAdopterByAdopterIdentifier_AdopterId(adopterid);

        if (adopter == null) {
            throw new NotFoundException("Unknown adopterid: " + adopterid);
        }

        return adopter;
    }
}
