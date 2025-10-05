package com.roos.adoptioncenter.adopter_service.presentation;

import com.roos.adoptioncenter.adopter_service.dataaccesslayer.AdopterAddress;
import com.roos.adoptioncenter.adopter_service.dataaccesslayer.AdopterContactMethodPreference;
import com.roos.adoptioncenter.adopter_service.dataaccesslayer.AdopterPhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdopterResponseModel extends RepresentationModel<AdopterResponseModel> {
    public String adopterId;
    public String fName;
    public String lName;
    public AdopterAddress Address;
    public AdopterPhoneNumber adopterPhoneNumber;
    public AdopterContactMethodPreference contactMethodPreference;
}
