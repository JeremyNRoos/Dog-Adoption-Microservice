package com.roos.adoptioncenter.apigateway.presentationlayer.adopter;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.roos.adoptioncenter.apigateway.DomainClientLayer.adopter.AdopterAddress;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.adopter.AdopterContactMethodPreference;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.adopter.AdopterPhoneNumber;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.InvalidInputException;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
import com.roos.adoptioncenter.apigateway.businesslayer.adopter.AdopterService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;



import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdopterController.class)
class AdopterControllerIntegrationTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdopterService adopterService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAdopterById_Success() throws Exception {
        AdopterResponseModel mockResponse = AdopterResponseModel.builder()
                .adopterId("123")
                .fName("John")
                .lName("Doe")
                .build();

        when(adopterService.getAdopterById("123")).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/adopters/123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect((ResultMatcher) jsonPath("$.adopterId").value("123"));
    }


    @Test
    void testGetAdopterById_NotFound() throws Exception {
        when(adopterService.getAdopterById("999"))
                .thenThrow(new NotFoundException("Adopter not found"));

        mockMvc.perform(get("/api/v1/adopters/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllAdopters() throws Exception {
        when(adopterService.getAllAdopters()).thenReturn(
                List.of(AdopterResponseModel.builder()
                        .adopterId("123")
                        .fName("John")
                        .lName("Doe")
                        .build())
        );

        mockMvc.perform(get("/api/v1/adopters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].adopterId").value("123"));
    }

    @Test
    void testAddAdopter() throws Exception {
        AdopterRequestModel request = AdopterRequestModel.builder()
                .fName("Jane")
                .lName("Smith")
                .build();

        AdopterResponseModel response = AdopterResponseModel.builder()
                .adopterId("456")
                .fName("Jane")
                .lName("Smith")
                .build();

        when(adopterService.createAdopter(any(AdopterRequestModel.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/adopters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.adopterId").value("456"));
    }

    @Test
    void testAddAdopter_InvalidInput() throws Exception {
        when(adopterService.createAdopter(any(AdopterRequestModel.class)))
                .thenThrow(new InvalidInputException("Invalid input"));

        AdopterAddress address = AdopterAddress.builder()
                .streetAddress("123 Street")
                .city("City")
                .province("province")
                .postalCode("12345")
                .build();

        AdopterPhoneNumber phoneNumber = AdopterPhoneNumber.builder()
                .phoneNumber("123-456-7890")
                .build();

        AdopterRequestModel request = AdopterRequestModel.builder()
                .fName("Jane")
                .lName("Smith")
                .address(address)
                .phoneNumber(phoneNumber)
                .contactMethodPreference(AdopterContactMethodPreference.EMAIL)
                .build();

        mockMvc.perform(post("/api/v1/adopters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testUpdateAdopter() throws Exception {
        AdopterRequestModel request = AdopterRequestModel.builder()
                .fName("John")
                .lName("Doe")
                .build();

        AdopterResponseModel response = AdopterResponseModel.builder()
                .adopterId("123")
                .fName("John")
                .lName("Doe")
                .build();

        when(adopterService.updateAdopter(any(AdopterRequestModel.class), anyString()))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/adopters/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adopterId").value("123"));
    }

    @Test
    void testDeleteAdopter() throws Exception {
        mockMvc.perform(delete("/api/v1/adopters/123"))
                .andExpect(status().isNoContent());
    }
}