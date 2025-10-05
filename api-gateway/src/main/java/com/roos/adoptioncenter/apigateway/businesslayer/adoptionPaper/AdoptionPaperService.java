package com.roos.adoptioncenter.apigateway.businesslayer.adoptionPaper;



import com.roos.adoptioncenter.apigateway.presentationlayer.adoptionPaper.AdoptionPaperRequestModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.adoptionPaper.AdoptionPaperResponseModel;

import java.util.List;

public interface AdoptionPaperService {
    List<AdoptionPaperResponseModel> getAdoptionPapers(String adopterId);
    AdoptionPaperResponseModel getAdoptionPaperById(String adopterId,String adoptionPaperId);
    AdoptionPaperResponseModel addAdoptionPaper(AdoptionPaperRequestModel adoptionPaperRequestModel, String adopterId);
    AdoptionPaperResponseModel updateAdoptionPaper(String adopterId, AdoptionPaperRequestModel adoptionPaperRequestModel, String adoptionPaperId);
    void deleteAdoptionPaper(String adopterId,String adoptionPaperId);
}
