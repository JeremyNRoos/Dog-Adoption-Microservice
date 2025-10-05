package com.roos.adoptioncenter.apigateway.presentationlayer.adopter;

import com.roos.adoptioncenter.apigateway.DomainClientLayer.adopter.AdopterAddress;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.adopter.AdopterContactMethodPreference;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.adopter.AdopterPhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdopterRequestModel {

    public String fName;
    public String lName;
    public AdopterAddress address;
    public AdopterPhoneNumber phoneNumber;
    public AdopterContactMethodPreference contactMethodPreference;
}
