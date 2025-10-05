package com.roos.adoptioncenter.adoptionpaper_service.businesslayer;


import com.roos.adoptioncenter.adoptionpaper_service.presentationlayer.AdoptionPaperRequestModel;
import com.roos.adoptioncenter.adoptionpaper_service.presentationlayer.AdoptionPaperResponseModel;

import java.util.List;

public interface AdoptionPaperService {
    List<AdoptionPaperResponseModel> getAdoptionPapers(String adopterId);
    AdoptionPaperResponseModel getAdoptionPaperById(String adopterId,String adoptionPaperId);
    AdoptionPaperResponseModel addAdoptionPaper(AdoptionPaperRequestModel adoptionPaperRequestModel, String adopterId);
    AdoptionPaperResponseModel updateAdoptionPaper(String adopterId,AdoptionPaperRequestModel adoptionPaperRequestModel, String adoptionPaperId);
    void deleteAdoptionPaper(String adopterId,String adoptionPaperId);
}
