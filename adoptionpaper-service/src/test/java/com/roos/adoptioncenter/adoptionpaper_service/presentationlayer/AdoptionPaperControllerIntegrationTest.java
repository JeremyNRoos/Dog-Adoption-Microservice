package com.roos.adoptioncenter.adoptionpaper_service.presentationlayer;

import com.roos.adoptioncenter.adoptionpaper_service.dataaccesslayer.AdoptionPaper;
import com.roos.adoptioncenter.adoptionpaper_service.dataaccesslayer.AdoptionPaperIdentifier;
import com.roos.adoptioncenter.adoptionpaper_service.dataaccesslayer.AdoptionPaperRepository;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.adopter.AdopterModel;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.dog.DogModel;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.dog.LocationModel;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.dog.ShelterTypeEnum;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.volunteer.VolunteerModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT) // Loads full application context, including actual service clients
@ActiveProfiles("test")
class AdoptionPaperControllerIntegrationTest { // Renamed slightly for clarity

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AdoptionPaperRepository adoptionPaperRepository;


    // --- NO @MockBean annotations for service clients ---

    private final String BASE_URI = "/api/v1/adoptionPapers";
    private String existingAdoptionPaperId; // Will be set in setUp

    // --- IDs MUST correspond to ACTUAL data in the RUNNING external services ---
    // --- You MUST ensure these IDs exist in your test environment's services ---
    private final String VALID_EXISTING_DOG_ID = "2cfa25c5-1d13-4a9c-ae2a-55e2a5ae2481"; // Example: Replace with REAL ID in test env
    private final String VALID_EXISTING_ADOPTER_ID = "fcbf86b1-8a76-4d2b-a352-75b10a8fd4a1"; // Example: Replace with REAL ID in test env
    private final String VALID_EXISTING_LOCATION_ID = "3e6c62a3-b1e7-4eb7-9642-8cdcb3ac74e6"; // Example: Replace with REAL ID in test env
    private final String VALID_EXISTING_VOLUNTEER_ID = "6a8aeaec-cff9-4ace-a8f0-146f8ed180e5"; // Example: Replace with REAL ID in test env

    // --- IDs for creating/updating - MUST also exist in external services ---
    private final String VALID_NEW_DOG_ID = "2cfa25c5-1d13-4a9c-ae2a-55e2a5ae2481"; // Example: Replace with REAL ID in test env
    private final String VALID_NEW_ADOPTER_ID = "fcbf86b1-8a76-4d2b-a352-75b10a8fd4a1"; // Example: Replace with REAL ID in test env
    private final String VALID_NEW_LOCATION_ID = "3e6c62a3-b1e7-4eb7-9642-8cdcb3ac74e6"; // Example: Replace with REAL ID in test env
    private final String VALID_NEW_VOLUNTEER_ID = "6a8aeaec-cff9-4ace-a8f0-146f8ed180e5"; // Example: Replace with REAL ID in test env

    // --- IDs known NOT to exist ---
    private final String NON_EXISTENT_ADOPTION_PAPER_ID = UUID.randomUUID().toString();
    private final String NON_EXISTENT_EXTERNAL_ADOPTER_ID = "adopter-non-existent-id"; // ID unlikely to exist in external service

    private final String INVALID_ADOPTION_PAPER_ID_FORMAT = "not-a-uuid";

    @BeforeEach
    void setup() {
        adoptionPaperRepository.deleteAll();

        existingAdoptionPaperId = UUID.randomUUID().toString();

        AdoptionPaper adoptionPaper = AdoptionPaper.builder()
                .adoptionPaperIdentifier(new AdoptionPaperIdentifier(existingAdoptionPaperId))
                .dogModel(DogModel.builder().dogId(VALID_EXISTING_DOG_ID).name("Buddy").age(2).build())
                .adopterModel(AdopterModel.builder().adopterId(VALID_EXISTING_ADOPTER_ID).FName("Alex").LName("Doe").build())
                .locationModel(LocationModel.builder().locationId(VALID_EXISTING_LOCATION_ID).name("Maple Shelter").shelterType(ShelterTypeEnum.SHELTER).build())
                .volunteerModel(VolunteerModel.builder().volunteerId(VALID_EXISTING_VOLUNTEER_ID).FName("Sam").LName("Smith").build())
                .build();

        adoptionPaperRepository.save(adoptionPaper);
    }

//    @Test
//    void whenGetAdoptionPaperById_ValidId_thenReturnOk() {
//        webTestClient.get()
//                .uri("/api/v1/adopters/" + VALID_EXISTING_ADOPTER_ID + "/adoptionPapers/" + existingAdoptionPaperId)
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isOk()
//                .expectHeader().contentType(MediaType.APPLICATION_JSON)
//                .expectBody(AdoptionPaperResponseModel.class)
//                .value(response -> {
//                    assertNotNull(response);
//                    assertEquals(existingAdoptionPaperId, response.getAdoptionPaperId());
//                    assertEquals(VALID_EXISTING_ADOPTER_ID, response.getAdopterId());
//                    assertNotNull(response.getAdopterFName());
//                    assertEquals(VALID_EXISTING_DOG_ID, response.getDogId());
//                    assertNotNull(response.getDogName());
//                    assertEquals(VALID_EXISTING_LOCATION_ID, response.getLocationId());
//                    assertNotNull(response.getLocationName());
//                    assertEquals(VALID_EXISTING_VOLUNTEER_ID, response.getVolunteerId());
//                    assertNotNull(response.getVolunteerFName());
//                });
//    }


    @Test
    void whenGetAdoptionPaperById_NonExistentId_thenReturnNotFound() {
        // Act & Assert
        webTestClient.get()
                .uri(BASE_URI + "/{adoptionPaperId}", NON_EXISTENT_ADOPTION_PAPER_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

//    @Test
//    void whenGetAdoptionPaperById_InvalidIdFormat_thenReturnUnprocessableEntity() {
//        webTestClient.get()
//                .uri("/api/v1/adopters/" + VALID_EXISTING_ADOPTER_ID + "/adoptionPapers/not-a-uuid")
//
//                .exchange()
//                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
//    }



    @Test
    void whenCreateAdoptionPaper_WithNonExistentExternalId_thenReturnNotFound() {
        // Arrange - Use an ID unlikely to exist in the REAL adopter service
        AdoptionPaperRequestModel requestModel = AdoptionPaperRequestModel.builder()
                .adopterId(NON_EXISTENT_EXTERNAL_ADOPTER_ID) // This ID should not exist in the running Adopter service
                .dogId(VALID_NEW_DOG_ID)
                .locationId(VALID_NEW_LOCATION_ID)
                .volunteerId(VALID_NEW_VOLUNTEER_ID)
                .build();

        // Act & Assert - Expecting the REAL service call to fail with a 404, propagated by the service layer
        webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound(); // Assumes your service layer correctly handles and propagates NotFoundException from clients
    }


    @Test
    void whenUpdateAdoptionPaper_NonExistentId_thenReturnNotFound() {
        // Arrange
        AdoptionPaperRequestModel updateRequest = AdoptionPaperRequestModel.builder()
                .adopterId(VALID_NEW_ADOPTER_ID)
                .dogId(VALID_NEW_DOG_ID)
                .locationId(VALID_NEW_LOCATION_ID)
                .volunteerId(VALID_NEW_VOLUNTEER_ID)
                .build();

        // Act & Assert
        webTestClient.put()
                .uri(BASE_URI + "/{adoptionPaperId}", NON_EXISTENT_ADOPTION_PAPER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenUpdateAdoptionPaper_WithNonExistentExternalId_thenReturnNotFound() {
        // Arrange - Use an ID unlikely to exist in the REAL adopter service
        AdoptionPaperRequestModel updateRequest = AdoptionPaperRequestModel.builder()
                .adopterId(NON_EXISTENT_EXTERNAL_ADOPTER_ID) // This ID should not exist in the running Adopter service
                .dogId(VALID_NEW_DOG_ID)
                .locationId(VALID_NEW_LOCATION_ID)
                .volunteerId(VALID_NEW_VOLUNTEER_ID)
                .build();

        // Act & Assert - Expecting the REAL service call during update to fail
        webTestClient.put()
                .uri(BASE_URI + "/{adoptionPaperId}", existingAdoptionPaperId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound(); // Assumes your service layer correctly handles and propagates NotFoundException from clients
    }


}