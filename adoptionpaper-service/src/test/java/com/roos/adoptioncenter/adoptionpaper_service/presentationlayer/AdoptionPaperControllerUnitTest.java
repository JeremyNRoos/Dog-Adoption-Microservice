package com.roos.adoptioncenter.adoptionpaper_service.presentationlayer;

import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.adopter.AdopterModel;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.adopter.AdopterServiceClient;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.dog.*;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.volunteer.VolunteerModel;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.volunteer.VolunteerServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AdoptionPaperControllerUnitTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplate restTemplate;

    @MockitoBean
    private AdopterServiceClient adopterServiceClient;

    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    private DogServiceClient dogServiceClient;

    @MockitoBean
    private LocationServiceClient locationServiceClient;

    @MockitoBean
    private VolunteerServiceClient volunteerServiceClient;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/v1/adopters";
    }


//    private final String validAdopterId = "fcbf86b1-8a76-4d2b-a352-75b10a8fd4a1";
//    private final String validDogId = "2cfa25c5-1d13-4a9c-ae2a-55e2a5ae2481";
//    private final String validVolunteerId = "6a8aeaec-cff9-4ace-a8f0-146f8ed180e5";
//    private final String validLocationId = "3e6c62a3-b1e7-4eb7-9642-8cdcb3ac74e6";



    private AdopterModel adopterModel;
    private DogModel dogModel;
    private LocationModel locationModel;
    private VolunteerModel volunteerModel;
    private AdoptionPaperRequestModel requestModel;

    @BeforeEach
    void setUp() {

        adopterModel = AdopterModel.builder()
                .adopterId(UUID.randomUUID().toString())
                .FName("Jeremy")
                .LName("Roos")
                .build();

        dogModel = DogModel.builder()
                .dogId(UUID.randomUUID().toString())
                .age(3)
                .name("Nathan")
                .build();

        locationModel = LocationModel.builder()
                .locationId(UUID.randomUUID().toString())
                .name("PlaceName")
                .shelterType(ShelterTypeEnum.SHELTER)
                .build();

        volunteerModel = VolunteerModel.builder()
                .volunteerId(UUID.randomUUID().toString())
                .FName("Volunteer")
                .LName("Roos")
                .build();

        requestModel = AdoptionPaperRequestModel.builder()
                .adopterId(adopterModel.getAdopterId())
                .dogId(dogModel.getDogId())
                .locationId(locationModel.getLocationId())
                .volunteerId(volunteerModel.getVolunteerId())
                .build();

        when(adopterServiceClient.getAdopterById(adopterModel.getAdopterId()))
                .thenReturn(adopterModel);

        when(dogServiceClient.getDogById(locationModel.getLocationId(), dogModel.getDogId()))
                .thenReturn(dogModel);

        when(locationServiceClient.getLocationById(locationModel.getLocationId()))
                .thenReturn(locationModel);

        when(volunteerServiceClient.getVolunteerById(volunteerModel.getVolunteerId()))
                .thenReturn(volunteerModel);
    }


    @Test
    void whenGetAllAdoptionPapers_thenReturnOkAndList() {
        // Arrange
        String url = getBaseUrl() + "/" + adopterModel.getAdopterId() + "/adoptionpapers";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AdoptionPaperRequestModel> request = new HttpEntity<>(requestModel, headers);

        ResponseEntity<AdoptionPaperResponseModel> postResponse =
                restTemplate.postForEntity(url, request, AdoptionPaperResponseModel.class);

        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        String adoptionPaperId = Objects.requireNonNull(postResponse.getBody()).getAdoptionPaperId();
        assertNotNull(adoptionPaperId);

        // Act
        ResponseEntity<AdoptionPaperResponseModel[]> response =
                restTemplate.getForEntity(url, AdoptionPaperResponseModel[].class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 1);
    }


    @Test
    void whenPostValidAdoptionPaper_thenReturnCreated() {
        String url = getBaseUrl() + "/" + adopterModel.getAdopterId() + "/adoptionpapers"; // Use mocked data

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AdoptionPaperRequestModel> request = new HttpEntity<>(requestModel, headers);

        ResponseEntity<AdoptionPaperResponseModel> response =
                restTemplate.postForEntity(url, request, AdoptionPaperResponseModel.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body should not be null");

        AdoptionPaperResponseModel responseBody = response.getBody();
        assertNotNull(responseBody.getDogName(), "Dog name in response should not be null");
        assertEquals(dogModel.getName(), responseBody.getDogName(), "Dog name should match the mock data");
    }

    @Test
    void whenGetInvalidUUID_thenReturnUnprocessableEntity() {
        String adopterId = adopterModel.getAdopterId(); // Use mocked adopter ID
        String invalidUUID = "not-a-uuid";

        try {
            String url = getBaseUrl() + "/" + adopterId + "/adoptionpapers/" + invalidUUID;
            ResponseEntity<AdoptionPaperResponseModel> response =
                    restTemplate.getForEntity(url, AdoptionPaperResponseModel.class);

            // This line should not be reached if the server returns 422
            fail("Expected HttpClientErrorException to be thrown");
        } catch (HttpClientErrorException ex) {
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ex.getStatusCode(), "Expected HTTP 422 Unprocessable Entity");
            String responseBody = ex.getResponseBodyAsString();
            assertNotNull(responseBody, "Response body should not be null");
            assertTrue(responseBody.contains("Invalid UUID"), "Response body should contain 'Invalid UUID'");
        }
    }

    @Test
    void whenDeleteExistingAdoptionPaper_thenReturnNoContent() {
        String createUrl = getBaseUrl() + "/" + adopterModel.getAdopterId() + "/adoptionpapers";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AdoptionPaperRequestModel> request = new HttpEntity<>(requestModel, headers);

        ResponseEntity<AdoptionPaperResponseModel> postResponse =
                restTemplate.postForEntity(createUrl, request, AdoptionPaperResponseModel.class);

        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode(), "Expected 201 Created");
        assertNotNull(postResponse.getBody(), "Response body should not be null");

        String adoptionPaperId = postResponse.getBody().getAdoptionPaperId();
        assertNotNull(adoptionPaperId, "Adoption Paper ID should not be null");

        // Correct casing and structure for DELETE URL
        String deleteUrl = getBaseUrl() + "/" + adopterModel.getAdopterId() + "/adoptionpapers/" + adoptionPaperId;

        restTemplate.delete(deleteUrl);

        // Verify deletion with GET expecting 404
        try {
            restTemplate.getForEntity(deleteUrl, String.class);
            fail("Expected HttpClientErrorException to be thrown due to missing resource");
        } catch (HttpClientErrorException ex) {
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode(), "Expected HTTP 404 Not Found after deletion");
            String responseBody = ex.getResponseBodyAsString();
            assertNotNull(responseBody, "Response body should not be null");
            assertTrue(responseBody.contains("No adoption paper with ID") || responseBody.toLowerCase().contains("not found"),
                    "Response should contain error message indicating missing adoption paper");
        }
    }

    @Test
    void whenGetWithInvalidUUID_thenReturnUnprocessableEntity() {
        String url = getBaseUrl() + "/" + adopterModel.getAdopterId() + "/adoptionpapers/invalid-uuid";

        try {
            restTemplate.getForEntity(url, String.class);
            fail("Expected HttpClientErrorException to be thrown");
        } catch (HttpClientErrorException ex) {
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ex.getStatusCode());
            assertTrue(ex.getResponseBodyAsString().contains("Invalid UUID format"));
        }
    }

    @Test
    void whenUpdateWithInvalidUUID_thenReturnUnprocessableEntity() {
        String url = getBaseUrl() + "/" + adopterModel.getAdopterId() + "/adoptionpapers/invalid-uuid";

        AdoptionPaperRequestModel updatedModel = AdoptionPaperRequestModel.builder()
                .adopterId(adopterModel.getAdopterId())
                .dogId(dogModel.getDogId())
                .volunteerId(volunteerModel.getVolunteerId())
                .locationId(locationModel.getLocationId())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdoptionPaperRequestModel> request = new HttpEntity<>(updatedModel, headers);

        try {
            restTemplate.exchange(url, HttpMethod.PUT, request, AdoptionPaperResponseModel.class);
            fail("Expected HttpClientErrorException to be thrown");
        } catch (HttpClientErrorException ex) {
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ex.getStatusCode());
            assertTrue(ex.getResponseBodyAsString().contains("Invalid UUID format"));
        }
    }


    @Test
    void whenDeleteWithInvalidUUID_thenReturnUnprocessableEntity() {
        String url = getBaseUrl() + "/" + adopterModel.getAdopterId() + "/adoptionpapers/invalid-uuid";

        try {
            restTemplate.delete(url);
            fail("Expected HttpClientErrorException to be thrown");
        } catch (HttpClientErrorException ex) {
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ex.getStatusCode());
            assertTrue(ex.getResponseBodyAsString().contains("Invalid UUID format"));
        }
    }

    @Test
    void whenGetAllAdoptionPapersForAdopterWithNoPapers_thenReturnEmptyList() {
        String url = getBaseUrl() + "/" + adopterModel.getAdopterId() + "/adoptionpapers";

        // Assuming no papers were created
        ResponseEntity<AdoptionPaperResponseModel[]> response =
                restTemplate.getForEntity(url, AdoptionPaperResponseModel[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().length, "Expected empty list when no papers are present");
    }
    @Test
    void whenPostWithMissingFields_thenReturnBadRequest() {
        String url = getBaseUrl() + "/" + adopterModel.getAdopterId() + "/adoptionpapers";

        // Create a request model with missing fields
        AdoptionPaperRequestModel incompleteModel = new AdoptionPaperRequestModel(); // No fields set

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AdoptionPaperRequestModel> request = new HttpEntity<>(incompleteModel, headers);

        try {
            restTemplate.postForEntity(url, request, String.class);
            fail("Expected HttpClientErrorException to be thrown for bad request");
        } catch (HttpClientErrorException ex) {
            assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode(), "Expected HTTP 400 Bad Request");
        }
    }

    @Test
    void whenUpdateAdoptionPaperWithValidData_thenReturnUpdated() {
        // First create a new adoption paper
        String createUrl = getBaseUrl() + "/" + adopterModel.getAdopterId() + "/adoptionpapers";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdoptionPaperRequestModel> request = new HttpEntity<>(requestModel, headers);

        ResponseEntity<AdoptionPaperResponseModel> postResponse =
                restTemplate.postForEntity(createUrl, request, AdoptionPaperResponseModel.class);

        String adoptionPaperId = postResponse.getBody().getAdoptionPaperId();

        // Now update it
        String updateUrl = createUrl + "/" + adoptionPaperId;
        AdoptionPaperRequestModel updatedModel = AdoptionPaperRequestModel.builder()
                .adopterId(adopterModel.getAdopterId())
                .dogId(dogModel.getDogId())
                .volunteerId(volunteerModel.getVolunteerId())
                .locationId(locationModel.getLocationId())
                .build();

        HttpEntity<AdoptionPaperRequestModel> updateRequest = new HttpEntity<>(updatedModel, headers);

        ResponseEntity<AdoptionPaperResponseModel> updateResponse =
                restTemplate.exchange(updateUrl, HttpMethod.PUT, updateRequest, AdoptionPaperResponseModel.class);

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        assertEquals(adoptionPaperId, updateResponse.getBody().getAdoptionPaperId());
    }

    @Test
    void whenGetByNonexistentValidUUID_thenReturnNotFound() {
        String url = getBaseUrl() + "/" + adopterModel.getAdopterId() + "/adoptionpapers/" + UUID.randomUUID();

        try {
            restTemplate.getForEntity(url, String.class);
            fail("Expected HttpClientErrorException to be thrown for not found");
        } catch (HttpClientErrorException ex) {
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode(), "Expected HTTP 404 Not Found");
        }
    }

    @Test
    void whenDeleteByNonexistentValidUUID_thenReturnNotFound() {
        String url = getBaseUrl() + "/" + adopterModel.getAdopterId() + "/adoptionpapers/" + UUID.randomUUID();

        try {
            restTemplate.delete(url);
            fail("Expected HttpClientErrorException to be thrown for not found");
        } catch (HttpClientErrorException ex) {
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode(), "Expected HTTP 404 Not Found");
        }
    }


    @Test
    void whenPostWithNonExistentAdopter_thenReturnNotFound() {
        // Arrange
        String url = getBaseUrl() + "/" + UUID.randomUUID() + "/adoptionpapers";
        AdoptionPaperRequestModel badRequest = requestModel.toBuilder()
                .adopterId("non-existent-adopter")
                .build();

        when(adopterServiceClient.getAdopterById("non-existent-adopter"))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdoptionPaperRequestModel> request = new HttpEntity<>(badRequest, headers);

        try {
            restTemplate.postForEntity(url, request, String.class);
            fail("Expected HttpClientErrorException");
        } catch (HttpClientErrorException ex) {
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        }
    }
    @Test
    void whenPostWithNonExistentDog_thenReturnNotFound() {
        // Arrange
        String url = getBaseUrl() + "/" + adopterModel.getAdopterId() + "/adoptionpapers";
        AdoptionPaperRequestModel badRequest = AdoptionPaperRequestModel.builder()
                .adopterId(requestModel.getAdopterId())
                .dogId("non-existent-dog")
                .volunteerId(requestModel.getVolunteerId())
                .locationId(requestModel.getLocationId())
                .build();

        // Act & Assert
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(badRequest)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(body -> assertTrue(body.contains("Dog retrieval failed") || body.contains("not found"), "Response should indicate missing dog"));
    }


    @Test
    void whenPostWithNonExistentLocation_thenReturnNotFound() {
        String url = getBaseUrl() + "/" + adopterModel.getAdopterId() + "/adoptionpapers";
        AdoptionPaperRequestModel badRequest = requestModel.toBuilder()
                .locationId("non-existent-location")
                .build();

        when(locationServiceClient.getLocationById("non-existent-location"))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdoptionPaperRequestModel> request = new HttpEntity<>(badRequest, headers);

        try {
            restTemplate.postForEntity(url, request, String.class);
            fail("Expected HttpClientErrorException");
        } catch (HttpClientErrorException ex) {
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        }
    }

    @Test
    void whenPostWithNonExistentVolunteer_thenReturnNotFound() {
        // Arrange
        String url = getBaseUrl() + "/" + adopterModel.getAdopterId() + "/adoptionpapers";
        AdoptionPaperRequestModel badRequest = AdoptionPaperRequestModel.builder()
                .adopterId(requestModel.getAdopterId())
                .dogId(requestModel.getDogId())
                .volunteerId("non-existent-volunteer")
                .locationId(requestModel.getLocationId())
                .build();

        // Act & Assert
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(badRequest)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(body -> assertTrue(body.contains("Volunteer retrieval failed") || body.contains("not found"), "Response should indicate missing volunteer"));
    }


}
