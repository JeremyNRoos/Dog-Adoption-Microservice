package com.roos.adoptioncenter.adoptionpaper_service.utils;




import com.roos.adoptioncenter.adoptionpaper_service.dataaccesslayer.AdoptionPaper;
import com.roos.adoptioncenter.adoptionpaper_service.dataaccesslayer.AdoptionPaperIdentifier;
import com.roos.adoptioncenter.adoptionpaper_service.dataaccesslayer.AdoptionPaperRepository;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.adopter.AdopterModel;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.dog.DogModel;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.dog.LocationModel;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.dog.ShelterTypeEnum;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.volunteer.VolunteerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Component
public class DatabaseLoaderService implements CommandLineRunner {

    @Autowired
    AdoptionPaperRepository adoptionPaperRepository;

    @Override
    public void run(String... args) throws Exception {



        var adopterModel = AdopterModel.builder()
                .adopterId("fcbf86b1-8a76-4d2b-a352-75b10a8fd4a1")
                .FName("John")
                .LName("Doe")
                .build();
        var volunteerModel = VolunteerModel.builder()
                .volunteerId("6a8aeaec-cff9-4ace-a8f0-146f8ed180e5")
                .FName("John")
                .LName("Doe")
                .build();

        var locationModel = LocationModel.builder()
                .locationId("3e6c62a3-b1e7-4eb7-9642-8cdcb3ac74e6")
                .name("Happy Tails Shelter")
                .shelterType(ShelterTypeEnum.SHELTER)
                // Add other required fields if any
                .build();

        var dogModel = DogModel.builder()
                .dogId("2cfa25c5-1d13-4a9c-ae2a-55e2a5ae2481")
                .name("Buddy")
                .age(3)
                .build();


        var adoptionPaper1 = AdoptionPaper.builder()
                .id("1")
                .adopterModel(adopterModel)
                .adoptionPaperIdentifier(new AdoptionPaperIdentifier("eef7bbff-e70b-4cb4-9be6-15688eb79da1"))
                .volunteerModel(volunteerModel)
                .locationModel(locationModel)
                .dogModel(dogModel)
                .build();

        adoptionPaperRepository.save(adoptionPaper1);

    }
}