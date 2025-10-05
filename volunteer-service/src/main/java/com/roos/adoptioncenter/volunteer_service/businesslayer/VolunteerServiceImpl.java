package com.roos.adoptioncenter.volunteer_service.businesslayer;

import com.roos.adoptioncenter.volunteer_service.utils.exceptions.InvalidInputException;
import com.roos.adoptioncenter.volunteer_service.utils.exceptions.NotFoundException;
import com.roos.adoptioncenter.volunteer_service.dataaccesslayer.Volunteer;
import com.roos.adoptioncenter.volunteer_service.dataaccesslayer.VolunteerIdentifier;
import com.roos.adoptioncenter.volunteer_service.dataaccesslayer.VolunteerRepository;
import com.roos.adoptioncenter.volunteer_service.mappinglayer.VolunteerRequestMapper;
import com.roos.adoptioncenter.volunteer_service.mappinglayer.VolunteerResponseMapper;
import com.roos.adoptioncenter.volunteer_service.presentation.VolunteerRequestModel;
import com.roos.adoptioncenter.volunteer_service.presentation.VolunteerResponseModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class VolunteerServiceImpl implements VolunteerService {

    private final VolunteerRepository volunteerRepository;
    private final VolunteerResponseMapper volunteerResponseMapper;
    private final VolunteerRequestMapper volunteerRequestMapper;

    public VolunteerServiceImpl(VolunteerRepository volunteerRepository, VolunteerResponseMapper volunteerResponseMapper, VolunteerRequestMapper volunteerRequestMapper) {
        this.volunteerRepository = volunteerRepository;
        this.volunteerResponseMapper = volunteerResponseMapper;
        this.volunteerRequestMapper = volunteerRequestMapper;
    }

    @Override
    public List<VolunteerResponseModel> getVolunteers() {
        List<Volunteer> volunteer = volunteerRepository.findAll();
        return volunteerResponseMapper.entityListToResponseModelList(volunteer);
    }

    @Override
    public VolunteerResponseModel getVolunteerById(String volunteerId) {
       Volunteer volunteer = getVolunteerObjectById(volunteerId);
        return volunteerResponseMapper.toResponseModel(volunteer);
    }

    @Override
    public VolunteerResponseModel addVolunteer(VolunteerRequestModel volunteerRequestModel) {
        Volunteer volunteer = volunteerRequestMapper.requestToEntity(volunteerRequestModel,
                new VolunteerIdentifier(),
                volunteerRequestModel.getVolunteerAddress(),
                volunteerRequestModel.getVolunteerPhoneNumber());

        validateVolunteerRequestModel(volunteerRequestModel);

        return volunteerResponseMapper.toResponseModel(volunteerRepository.save(volunteer));
    }

    @Override
    public VolunteerResponseModel updateVolunteer(VolunteerRequestModel volunteerRequestModel, String volunteerId) {
        Volunteer volunteer = getVolunteerObjectById(volunteerId);

        validateVolunteerRequestModel(volunteerRequestModel);


        Volunteer updatedVolunteer = volunteerRequestMapper.requestToEntity(volunteerRequestModel, new VolunteerIdentifier(volunteerId),
                volunteerRequestModel.getVolunteerAddress(),
                volunteerRequestModel.getVolunteerPhoneNumber());
//        volunteer.setVolunteerIdentifier(updatedVolunteer.getVolunteerIdentifier());
        volunteer.setVolunteerAddress(updatedVolunteer.getVolunteerAddress());
        volunteer.setEmail(updatedVolunteer.getEmail());
        volunteer.setVolunteerPhoneNumber(updatedVolunteer.getVolunteerPhoneNumber());
        volunteer.setFName(updatedVolunteer.getFName());
        volunteer.setLName(updatedVolunteer.getLName());
        volunteer.setTitle(updatedVolunteer.getTitle());
        volunteer.setSalary(updatedVolunteer.getSalary());
        Volunteer savedVolunteer = volunteerRepository.save(volunteer);
        return volunteerResponseMapper.toResponseModel(savedVolunteer);
    }

    @Override
    public void deleteVolunteer(String volunteerId) {

        Volunteer volunteer = getVolunteerObjectById(volunteerId);


        volunteerRepository.delete(volunteer);
    }

    private void validateVolunteerRequestModel(VolunteerRequestModel model) {
        if (model.getFName() == null || model.getFName().isBlank()) {
            throw new InvalidInputException("Invalid firstName: " + model.getFName());
        }
        if (model.getLName() == null || model.getLName().isBlank()) {
            throw new InvalidInputException("Invalid lastName: " + model.getLName());
        }

    }

    private Volunteer getVolunteerObjectById(String volunteerid) {
        try {
            UUID.fromString(volunteerid);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid volunteerid: " + volunteerid);
        }

        Volunteer volunteer = this.volunteerRepository.findVolunteerByVolunteerIdentifier_VolunteerId(volunteerid);

        if (volunteer == null) {
            throw new NotFoundException("Unknown volunteerid: " + volunteerid);
        }

        return volunteer;
    }
}
