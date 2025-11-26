package com.roos.adoptioncenter.apigateway.businesslayer.adopter;

import com.roos.adoptioncenter.apigateway.presentationlayer.adopter.AdopterRequestModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.adopter.AdopterResponseModel;
import org.springframework.stereotype.Service;

import java.util.List;

public interface AdopterService {

    public AdopterResponseModel createAdopter(AdopterRequestModel adopterRequestModel);

    public AdopterResponseModel getAdopterById(String adopterId);

    public List<AdopterResponseModel> getAllAdopters();

    public AdopterResponseModel updateAdopter(AdopterRequestModel adopterRequestModel, String adopterId);

    public void deleteAdopter(String adopterId);
}
