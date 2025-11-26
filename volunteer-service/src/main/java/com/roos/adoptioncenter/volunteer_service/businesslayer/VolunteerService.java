package com.roos.adoptioncenter.volunteer_service.businesslayer;

import com.roos.adoptioncenter.volunteer_service.presentation.VolunteerRequestModel;
import com.roos.adoptioncenter.volunteer_service.presentation.VolunteerResponseModel;

import java.util.List;

public interface VolunteerService {
    List<VolunteerResponseModel> getVolunteers();
    VolunteerResponseModel getVolunteerById(String volunteerId);
    VolunteerResponseModel addVolunteer(VolunteerRequestModel volunteerRequestModel);
    VolunteerResponseModel updateVolunteer(VolunteerRequestModel volunteerRequestModel, String volunteerId);
    void deleteVolunteer(String volunteerId);
}
