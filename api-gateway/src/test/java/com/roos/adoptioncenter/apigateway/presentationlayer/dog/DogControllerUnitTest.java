package com.roos.adoptioncenter.apigateway.presentationlayer.dog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roos.adoptioncenter.apigateway.businesslayer.dog.DogService;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.InvalidInputException;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.dog.AvailabilityStatusEnum;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.dog.DogBreedEnum;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.dog.Kennel;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.dog.KennelSizeEnum;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.dog.VaccinationStatusEnum;
import org.junit.jupiter.api.BeforeEach;
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
class DogControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DogService dogService;

    private DogRequestModel dogRequest;
    private DogResponseModel dogResponse;

    @BeforeEach
    void setUp() {
        Kennel kennel = Kennel.builder()
                .kennelSize(KennelSizeEnum.LARGE)
                .build();

        dogRequest = DogRequestModel.builder()
                .locationId("location-1")
                .name("Buddy")
                .breed(DogBreedEnum.AFGHAN_HOUND)
                .age(4)
                .dogKennel(kennel)
                .vaccinationStatus(VaccinationStatusEnum.VACCINATED)
                .availabilityStatus(AvailabilityStatusEnum.AVAILABLE)
                .build();

        dogResponse = DogResponseModel.builder()
                .dogId("dog-1")
                .locationId("location-1")
                .name("Buddy")
                .breed(DogBreedEnum.AFGHAN_HOUND)
                .age(4)
                .dogKennel(kennel)
                .vaccinationStatus(VaccinationStatusEnum.VACCINATED)
                .availabilityStatus(AvailabilityStatusEnum.AVAILABLE)
                .build();
    }

    @Test
    void testGetDogById_Success() throws Exception {
        when(dogService.getDogById("location-1", "dog-1")).thenReturn(dogResponse);

        mockMvc.perform(get("/api/v1/locations/location-1/dogs/dog-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dogId").value("dog-1"));
    }

    @Test
    void testGetDogById_NotFound() throws Exception {
        when(dogService.getDogById("location-1", "dog-999")).thenThrow(new NotFoundException("Dog not found"));

        mockMvc.perform(get("/api/v1/locations/location-1/dogs/dog-999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllDogs_Success() throws Exception {
        when(dogService.getAllDogs("location-1")).thenReturn(List.of(dogResponse));

        mockMvc.perform(get("/api/v1/locations/location-1/dogs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dogId").value("dog-1"));
    }

    @Test
    void testAddDog_Success() throws Exception {
        when(dogService.addDog(anyString(), any(DogRequestModel.class))).thenReturn(dogResponse);

        mockMvc.perform(post("/api/v1/locations/location-1/dogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dogRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dogId").value("dog-1"));
    }

    @Test
    void testAddDog_InvalidInput() throws Exception {
        when(dogService.addDog(anyString(), any(DogRequestModel.class)))
                .thenThrow(new InvalidInputException("Invalid input"));

        mockMvc.perform(post("/api/v1/locations/location-1/dogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dogRequest)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testUpdateDog_Success() throws Exception {
        when(dogService.updateDog(anyString(), any(DogRequestModel.class), anyString()))
                .thenReturn(dogResponse);

        mockMvc.perform(put("/api/v1/locations/location-1/dogs/dog-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dogRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dogId").value("dog-1"));
    }

    @Test
    void testDeleteDog_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/locations/location-1/dogs/dog-1"))
                .andExpect(status().isNoContent());
    }
}
