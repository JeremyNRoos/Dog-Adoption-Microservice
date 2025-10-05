package com.roos.adoptioncenter.adoptionpaper_service.presentationlayer;

import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.adopter.AdopterModel;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.dog.DogModel;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.dog.LocationModel;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.volunteer.VolunteerModel;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AdoptionPaperRequestModel {
    private String adopterId;
    @NotNull(message = "Dog ID is required")
    private String dogId;

    @NotNull(message = "Volunteer ID is required")
    private String volunteerId;

    @NotNull(message = "Location ID is required")
    private String locationId;


}
