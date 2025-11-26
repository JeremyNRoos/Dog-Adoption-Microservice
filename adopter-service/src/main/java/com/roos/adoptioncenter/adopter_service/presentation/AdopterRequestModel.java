package com.roos.adoptioncenter.adopter_service.presentation;

import com.roos.adoptioncenter.adopter_service.dataaccesslayer.AdopterAddress;
import com.roos.adoptioncenter.adopter_service.dataaccesslayer.AdopterContactMethodPreference;
import com.roos.adoptioncenter.adopter_service.dataaccesslayer.AdopterPhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AdopterRequestModel {

    public String fName;
    public String lName;
    public AdopterAddress address;
    public AdopterPhoneNumber phoneNumber;
    public AdopterContactMethodPreference contactMethodPreference;
}
