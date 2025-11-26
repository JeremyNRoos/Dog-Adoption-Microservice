package com.roos.adoptioncenter.apigateway.presentationlayer.adoptionPaper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.InvalidInputException;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
import com.roos.adoptioncenter.apigateway.businesslayer.adoptionPaper.AdoptionPaperService;
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

@WebMvcTest(AdoptionPaperController.class)
class AdoptionPaperControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdoptionPaperService adoptionPaperService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAdoptionPaperById_Success() throws Exception {
        AdoptionPaperResponseModel response = AdoptionPaperResponseModel.builder()
                .adoptionPaperId("paper-1")
                .adopterId("adopter-1")
                .dogId("dog-1")
                .locationId("loc-1")
                .volunteerId("vol-1")
                .build();

        when(adoptionPaperService.getAdoptionPaperById("adopter-1", "paper-1")).thenReturn(response);

        mockMvc.perform(get("/api/v1/adopters/adopter-1/adoptionpapers/paper-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.adoptionPaperId").value("paper-1"));
    }

    @Test
    void testGetAdoptionPaperById_NotFound() throws Exception {
        when(adoptionPaperService.getAdoptionPaperById(anyString(), anyString()))
                .thenThrow(new NotFoundException("Adoption paper not found"));

        mockMvc.perform(get("/api/v1/adopters/adopter-1/adoptionpapers/notfound"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllAdoptionPapers() throws Exception {
        AdoptionPaperResponseModel response = AdoptionPaperResponseModel.builder()
                .adoptionPaperId("paper-1")
                .adopterId("adopter-1")
                .dogId("dog-1")
                .locationId("loc-1")
                .volunteerId("vol-1")
                .build();

        when(adoptionPaperService.getAdoptionPapers("adopter-1")).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/adopters/adopter-1/adoptionpapers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].adoptionPaperId").value("paper-1"));
    }

    @Test
    void testAddAdoptionPaper_Success() throws Exception {
        AdoptionPaperRequestModel request = AdoptionPaperRequestModel.builder()
                .dogId("dog-1")
                .locationId("loc-1")
                .volunteerId("vol-1")
                .build();

        AdoptionPaperResponseModel response = AdoptionPaperResponseModel.builder()
                .adoptionPaperId("paper-1")
                .adopterId("adopter-1")
                .dogId("dog-1")
                .locationId("loc-1")
                .volunteerId("vol-1")
                .build();

        when(adoptionPaperService.addAdoptionPaper(any(AdoptionPaperRequestModel.class), anyString()))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/adopters/adopter-1/adoptionpapers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.adoptionPaperId").value("paper-1"));
    }

    @Test
    void testAddAdoptionPaper_InvalidInput() throws Exception {
        when(adoptionPaperService.addAdoptionPaper(any(AdoptionPaperRequestModel.class), anyString()))
                .thenThrow(new InvalidInputException("Invalid input"));

        AdoptionPaperRequestModel request = AdoptionPaperRequestModel.builder()
                .dogId("dog-1")
                .locationId("loc-1")
                .volunteerId("vol-1")
                .build();

        mockMvc.perform(post("/api/v1/adopters/adopter-1/adoptionpapers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testUpdateAdoptionPaper() throws Exception {
        AdoptionPaperRequestModel request = AdoptionPaperRequestModel.builder()
                .dogId("dog-1")
                .locationId("loc-1")
                .volunteerId("vol-1")
                .build();

        AdoptionPaperResponseModel response = AdoptionPaperResponseModel.builder()
                .adoptionPaperId("paper-1")
                .adopterId("adopter-1")
                .dogId("dog-1")
                .locationId("loc-1")
                .volunteerId("vol-1")
                .build();

        when(adoptionPaperService.updateAdoptionPaper(anyString(), any(AdoptionPaperRequestModel.class), anyString()))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/adopters/adopter-1/adoptionpapers/paper-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adoptionPaperId").value("paper-1"));
    }

    @Test
    void testDeleteAdoptionPaper() throws Exception {
        mockMvc.perform(delete("/api/v1/adopters/adopter-1/adoptionpapers/paper-1"))
                .andExpect(status().isNoContent());
    }
}
