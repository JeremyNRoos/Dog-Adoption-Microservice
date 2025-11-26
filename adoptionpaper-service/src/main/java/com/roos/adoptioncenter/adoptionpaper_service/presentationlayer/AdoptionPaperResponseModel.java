package com.roos.adoptioncenter.adoptionpaper_service.presentationlayer;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdoptionPaperResponseModel extends RepresentationModel<AdoptionPaperResponseModel>{
    public String adoptionPaperId;

    public String adopterId;
    public String adopterFName;
    public String adopterLName;

    public String dogId;
    public String dogName;
    public Integer dogAge;

    public String locationId;
    public String locationName;
    public String locationShelterType;

    public String volunteerId;
    public String volunteerFName;
    public String volunteerLName;
}
