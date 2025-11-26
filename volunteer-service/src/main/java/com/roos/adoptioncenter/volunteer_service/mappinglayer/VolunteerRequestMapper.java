package com.roos.adoptioncenter.volunteer_service.mappinglayer;

import com.roos.adoptioncenter.volunteer_service.dataaccesslayer.Volunteer;
import com.roos.adoptioncenter.volunteer_service.dataaccesslayer.VolunteerAddress;
import com.roos.adoptioncenter.volunteer_service.dataaccesslayer.VolunteerIdentifier;
import com.roos.adoptioncenter.volunteer_service.dataaccesslayer.VolunteerPhoneNumber;
import com.roos.adoptioncenter.volunteer_service.presentation.VolunteerRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface VolunteerRequestMapper {
    @Mappings(
            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(expression = "java(volunteerIdentifier)", target = "volunteerIdentifier"),
                    @Mapping(source = "volunteerRequestModel.fName", target = "fName"),
                    @Mapping(source = "volunteerRequestModel.lName", target = "lName"),
                    @Mapping(source = "volunteerRequestModel.email", target = "email"),
                    @Mapping(source = "volunteerRequestModel.salary", target = "salary"),
                    @Mapping(source = "volunteerRequestModel.title", target = "title"),
                    @Mapping(expression = "java(volunteerAddress)", target = "volunteerAddress"),
                    @Mapping(expression = "java(volunteerPhoneNumber)", target = "volunteerPhoneNumber")

            }
    )
    Volunteer requestToEntity(VolunteerRequestModel volunteerRequestModel, VolunteerIdentifier volunteerIdentifier,
                              VolunteerAddress volunteerAddress,
                              VolunteerPhoneNumber volunteerPhoneNumber);

}
