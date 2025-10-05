package com.roos.adoptioncenter.volunteer_service.presentation;

import com.roos.adoptioncenter.volunteer_service.dataaccesslayer.TitleEnum2;
import com.roos.adoptioncenter.volunteer_service.dataaccesslayer.VolunteerAddress;
import com.roos.adoptioncenter.volunteer_service.dataaccesslayer.VolunteerPhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class VolunteerResponseModel extends RepresentationModel<VolunteerResponseModel> {
    public String volunteerId;
    public String fName;
    public String lName;
    public String email;
    public String salary;
    public TitleEnum2 title;
    public VolunteerAddress volunteerAddress;
    public VolunteerPhoneNumber volunteerPhoneNumber;
}
