package com.roos.adoptioncenter.apigateway.businesslayer.volunteer;

import com.roos.adoptioncenter.apigateway.DomainClientLayer.volunteer.VolunteerServiceClient;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
import com.roos.adoptioncenter.apigateway.presentationlayer.adopter.AdopterController;
import com.roos.adoptioncenter.apigateway.presentationlayer.adopter.AdopterResponseModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.volunteer.VolunteerController;
import com.roos.adoptioncenter.apigateway.presentationlayer.volunteer.VolunteerRequestModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.volunteer.VolunteerResponseModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class VolunteerServiceImpl implements VolunteerService {

    private final VolunteerServiceClient volunteerServiceClient;

    public VolunteerServiceImpl(VolunteerServiceClient volunteerServiceClient) {
        this.volunteerServiceClient = volunteerServiceClient;
    }

    @Override
    public List<VolunteerResponseModel> getVolunteers() {
        return this.volunteerServiceClient.getAllVolunteers().stream().map(this::addLinks).toList();
    }

    public VolunteerResponseModel getVolunteerById(String volunteerId) {
        var volunteer = volunteerServiceClient.getVolunteerById(volunteerId);
        if (volunteer == null) {
            throw new NotFoundException("Volunteer with ID " + volunteerId + " not found.");
        }
        return addLinks(volunteer);
    }

    @Override
    public VolunteerResponseModel addVolunteer(VolunteerRequestModel volunteerRequestModel) {
        return this.addLinks(volunteerServiceClient.addVolunteer(volunteerRequestModel));
    }

    @Override
    public VolunteerResponseModel updateVolunteer(VolunteerRequestModel volunteerRequestModel, String volunteerId) {
        return this.addLinks(volunteerServiceClient.updateVolunteer(volunteerRequestModel, volunteerId));
    }

    @Override
    public void deleteVolunteer(String volunteerId) {
        volunteerServiceClient.deleteVolunteer(volunteerId);
    }

    private VolunteerResponseModel addLinks(VolunteerResponseModel volunteer) {
        Link selfLink = linkTo(methodOn(VolunteerController.class)
                .getVolunteerById(volunteer.getVolunteerId()))
                .withSelfRel();
        volunteer.add(selfLink);

        Link allVolunteersLink = linkTo(methodOn(VolunteerController.class)
                .getAllVolunteers())
                .withRel("volunteers");
        volunteer.add(allVolunteersLink);

        return volunteer;
    }
}
