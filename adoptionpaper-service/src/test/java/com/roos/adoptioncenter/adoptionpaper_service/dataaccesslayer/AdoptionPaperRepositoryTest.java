package com.roos.adoptioncenter.adoptionpaper_service.dataaccesslayer;


import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.adopter.AdopterModel;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.dog.DogModel;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.dog.LocationModel;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.dog.ShelterTypeEnum;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.volunteer.VolunteerModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class AdoptionPaperRepositoryTest {

    @Autowired
    private AdoptionPaperRepository adoptionPaperRepository;

    @BeforeEach
    public void setUp() {
        adoptionPaperRepository.deleteAll();
    }

    private AdoptionPaper createSamplePaper(AdoptionPaperIdentifier identifier) {
        AdopterModel adopter = AdopterModel.builder()
                .adopterId(UUID.randomUUID().toString())
                .FName("Alice")
                .LName("Smith")
                .build();

        DogModel dog = DogModel.builder()
                .dogId(UUID.randomUUID().toString())
                .name("Buddy")
                .age(3)
                .build();

        LocationModel location = LocationModel.builder()
                .locationId(UUID.randomUUID().toString())
                .name("Main Shelter")
                .shelterType(ShelterTypeEnum.SHELTER)
                .build();

        VolunteerModel volunteer = VolunteerModel.builder()
                .volunteerId(UUID.randomUUID().toString())
                .FName("John")
                .LName("Doe")
                .build();

        return AdoptionPaper.builder()
                .adoptionPaperIdentifier(identifier)
                .adopterModel(adopter)
                .dogModel(dog)
                .locationModel(location)
                .volunteerModel(volunteer)
                .build();
    }

    @Test
    public void whenValidAdoptionPaperSaved_thenItIsPersisted() {
        // Arrange
        AdoptionPaperIdentifier identifier = new AdoptionPaperIdentifier();
        AdoptionPaper paper = createSamplePaper(identifier);

        // Act
        AdoptionPaper saved = adoptionPaperRepository.save(paper);

        // Assert
        assertNotNull(saved);
        assertEquals(identifier.getAdoptionPaperId(), saved.getAdoptionPaperIdentifier().getAdoptionPaperId());
        assertEquals("Alice", saved.getAdopterModel().getFName());
        assertEquals("Buddy", saved.getDogModel().getName());
    }

    @Test
    public void whenAdoptionPapersExist_thenReturnAll() {
        // Arrange
        adoptionPaperRepository.save(createSamplePaper(new AdoptionPaperIdentifier()));
        adoptionPaperRepository.save(createSamplePaper(new AdoptionPaperIdentifier()));

        // Act
        List<AdoptionPaper> papers = adoptionPaperRepository.findAll();

        // Assert
        assertEquals(2, papers.size());
    }

//    @Test
//    public void whenAdoptionPaperIdExists_thenReturnPaper() {
//        // Arrange
//        AdoptionPaperIdentifier identifier = new AdoptionPaperIdentifier(); // this will generate a random ID
//        String expectedId = identifier.getAdoptionPaperId(); // get that ID
//
//        // Build and save paper using this identifier
//        AdoptionPaper paper = createSamplePaper(identifier); // only use this one
//
//        adoptionPaperRepository.save(paper);
//
//        // Act
//        AdoptionPaper found = adoptionPaperRepository
//                .findAdoptionPaperByAdoptionPaperIdentifier_AdoptionPaperId(expectedId);
//
//        // Assert
//        assertNotNull(found);
//        assertEquals(expectedId, found.getAdoptionPaperIdentifier().getAdoptionPaperId());
//    }


    @Test
    public void whenAdoptionPaperIdDoesNotExist_thenReturnNull() {
        // Act
        AdoptionPaper found = adoptionPaperRepository
                .findAdoptionPaperByAdoptionPaperIdentifier_AdoptionPaperId("non-existent-id");

        // Assert
        assertNull(found);
    }
}