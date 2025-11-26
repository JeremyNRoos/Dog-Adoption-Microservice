package com.roos.adoptioncenter.apigateway.presentationlayer.adopter;

import com.roos.adoptioncenter.apigateway.DomainClientLayer.adopter.AdopterAddress;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.adopter.AdopterContactMethodPreference;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.adopter.AdopterPhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdopterResponseModel extends RepresentationModel<AdopterResponseModel> {
    public String adopterId;
    public String fName;
    public String lName;
    public AdopterAddress Address;
    public AdopterPhoneNumber adopterPhoneNumber;
    public AdopterContactMethodPreference contactMethodPreference;
}
