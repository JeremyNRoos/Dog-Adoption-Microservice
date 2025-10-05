package com.roos.adoptioncenter.apigateway.presentationlayer.dog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.dog.*;
import com.roos.adoptioncenter.apigateway.businesslayer.dog.DogService;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.InvalidInputException;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(DogController.class)
class DogControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DogService dogService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetDogById_Success() throws Exception {
        DogResponseModel response = DogResponseModel.builder()
                .dogId("dog-1")
                .locationId("location-1")
                .name("Buddy")
                .breed(DogBreedEnum.AFGHAN_HOUND)
                .age(3)
                .dogKennel(Kennel.builder()
                        .kennelSize(KennelSizeEnum.SMALL)  // Replace with correct KennelSizeEnum value
                        .build())
                .vaccinationStatus(VaccinationStatusEnum.VACCINATED)
                .availabilityStatus(AvailabilityStatusEnum.AVAILABLE)
                .build();

        when(dogService.getDogById("location-1", "dog-1")).thenReturn(response);

        mockMvc.perform(get("/api/v1/locations/location-1/dogs/dog-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.dogId").value("dog-1"));
    }

    @Test
    void testGetDogById_NotFound() throws Exception {
        when(dogService.getDogById("location-1", "notfound"))
                .thenThrow(new NotFoundException("Dog not found"));

        mockMvc.perform(get("/api/v1/locations/location-1/dogs/notfound"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllDogs() throws Exception {
        when(dogService.getAllDogs("location-1")).thenReturn(
                List.of(DogResponseModel.builder()
                        .dogId("dog-1")
                        .locationId("location-1")
                        .name("Buddy")
                        .breed(DogBreedEnum.AFGHAN_HOUND)
                        .age(3)
                        .dogKennel(Kennel.builder()
                                .kennelSize(KennelSizeEnum.SMALL)  // Replace with correct value
                                .build())
                        .vaccinationStatus(VaccinationStatusEnum.VACCINATED)
                        .availabilityStatus(AvailabilityStatusEnum.AVAILABLE)
                        .build())
        );

        mockMvc.perform(get("/api/v1/locations/location-1/dogs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dogId").value("dog-1"));
    }

    @Test
    void testAddDog() throws Exception {
        DogRequestModel request = DogRequestModel.builder()
                .locationId("location-1")
                .name("Buddy")
                .breed(DogBreedEnum.AFGHAN_HOUND)
                .age(3)
                .dogKennel(Kennel.builder()
                        .kennelSize(KennelSizeEnum.SMALL)  // Replace with correct value
                        .build())
                .vaccinationStatus(VaccinationStatusEnum.VACCINATED)
                .availabilityStatus(AvailabilityStatusEnum.AVAILABLE)
                .build();

        DogResponseModel response = DogResponseModel.builder()
                .dogId("dog-1")
                .locationId("location-1")
                .name("Buddy")
                .breed(DogBreedEnum.AFGHAN_HOUND)
                .age(3)
                .dogKennel(Kennel.builder()
                        .kennelSize(KennelSizeEnum.SMALL)  // Replace with correct value
                        .build())
                .vaccinationStatus(VaccinationStatusEnum.VACCINATED)
                .availabilityStatus(AvailabilityStatusEnum.AVAILABLE)
                .build();

        when(dogService.addDog(anyString(), any(DogRequestModel.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/locations/location-1/dogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dogId").value("dog-1"));
    }

    @Test
    void testAddDog_InvalidInput() throws Exception {
        when(dogService.addDog(anyString(), any(DogRequestModel.class)))
                .thenThrow(new InvalidInputException("Invalid input"));

        DogRequestModel request = DogRequestModel.builder()
                .locationId("location-1")
                .name("Buddy")
                .breed(DogBreedEnum.AFGHAN_HOUND)
                .age(3)
                .dogKennel(Kennel.builder()
                        .kennelSize(KennelSizeEnum.SMALL)  // Replace with correct value
                        .build())
                .vaccinationStatus(VaccinationStatusEnum.VACCINATED)
                .availabilityStatus(AvailabilityStatusEnum.AVAILABLE)
                .build();

        mockMvc.perform(post("/api/v1/locations/location-1/dogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testUpdateDog() throws Exception {
        DogRequestModel request = DogRequestModel.builder()
                .locationId("location-1")
                .name("Buddy")
                .breed(DogBreedEnum.AFGHAN_HOUND)
                .age(3)
                .dogKennel(Kennel.builder()
                        .kennelSize(KennelSizeEnum.SMALL)  // Replace with correct value
                        .build())
                .vaccinationStatus(VaccinationStatusEnum.VACCINATED)
                .availabilityStatus(AvailabilityStatusEnum.AVAILABLE)
                .build();

        DogResponseModel response = DogResponseModel.builder()
                .dogId("dog-1")
                .locationId("location-1")
                .name("Buddy")
                .breed(DogBreedEnum.AFGHAN_HOUND)
                .age(3)
                .dogKennel(Kennel.builder()
                        .kennelSize(KennelSizeEnum.SMALL)  // Replace with correct value
                        .build())
                .vaccinationStatus(VaccinationStatusEnum.VACCINATED)
                .availabilityStatus(AvailabilityStatusEnum.AVAILABLE)
                .build();

        when(dogService.updateDog(anyString(), any(DogRequestModel.class), anyString()))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/locations/location-1/dogs/dog-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dogId").value("dog-1"));
    }

    @Test
    void testDeleteDog() throws Exception {
        mockMvc.perform(delete("/api/v1/locations/location-1/dogs/dog-1"))
                .andExpect(status().isNoContent());
    }
}