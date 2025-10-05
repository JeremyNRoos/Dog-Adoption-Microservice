package com.roos.adoptioncenter.apigateway.businesslayer.volunteer;

import com.roos.adoptioncenter.apigateway.presentationlayer.volunteer.VolunteerRequestModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.volunteer.VolunteerResponseModel;

import java.util.List;
public interface VolunteerService {
    public List<VolunteerResponseModel> getVolunteers();

    VolunteerResponseModel getVolunteerById(String volunteerId);

    VolunteerResponseModel addVolunteer(VolunteerRequestModel volunteerRequestModel);

    VolunteerResponseModel updateVolunteer(VolunteerRequestModel volunteerRequestModel, String volunteerId);

    void deleteVolunteer(String volunteerId);
}
