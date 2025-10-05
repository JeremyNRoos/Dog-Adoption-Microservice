package com.roos.adoptioncenter.apigateway.presentationlayer.adoptionPaper;

import com.roos.adoptioncenter.apigateway.DomainClientLayer.adoptionpaper.AdopterModel;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.adoptionpaper.DogModel;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.adoptionpaper.LocationModel;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.adoptionpaper.VolunteerModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdoptionPaperRequestModel {
    private String adopterId;
    private String dogId;
    private String locationId;
    private String volunteerId;


}
