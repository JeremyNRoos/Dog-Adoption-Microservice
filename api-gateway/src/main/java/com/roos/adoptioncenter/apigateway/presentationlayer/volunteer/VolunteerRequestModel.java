package com.roos.adoptioncenter.apigateway.presentationlayer.volunteer;



import com.roos.adoptioncenter.apigateway.DomainClientLayer.volunteer.TitleEnum2;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.volunteer.VolunteerAddress;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.volunteer.VolunteerPhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class VolunteerRequestModel {
    public String fName;
    public String lName;
    public String email;
    public String salary;
    public TitleEnum2 title;
    public VolunteerAddress volunteerAddress;
    public VolunteerPhoneNumber volunteerPhoneNumber;
}
