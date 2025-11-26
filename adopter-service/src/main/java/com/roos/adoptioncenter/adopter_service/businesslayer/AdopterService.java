package com.roos.adoptioncenter.adopter_service.businesslayer;


import com.roos.adoptioncenter.adopter_service.presentation.AdopterRequestModel;
import com.roos.adoptioncenter.adopter_service.presentation.AdopterResponseModel;

import java.util.List;

public interface AdopterService {
    List<AdopterResponseModel> getAdopters();
    AdopterResponseModel getAdopterById(String adopterId);
    AdopterResponseModel addAdopter(AdopterRequestModel adopterRequestModel);
    AdopterResponseModel updateAdopter(AdopterRequestModel adopterRequestModel, String adopterId);
    void deleteAdopter(String adopterId);
}
