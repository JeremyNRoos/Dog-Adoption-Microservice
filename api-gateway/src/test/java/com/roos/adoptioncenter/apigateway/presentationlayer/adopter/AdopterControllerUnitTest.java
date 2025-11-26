package com.roos.adoptioncenter.apigateway.presentationlayer.adopter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.InvalidInputException;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
import com.roos.adoptioncenter.apigateway.businesslayer.adopter.AdopterService;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.adopter.AdopterAddress;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.adopter.AdopterContactMethodPreference;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.adopter.AdopterPhoneNumber;
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

@WebMvcTest(AdopterController.class)
class AdopterControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdopterService adopterService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAdopterById_Success() throws Exception {
        AdopterResponseModel response = AdopterResponseModel.builder()
                .adopterId("adopter-1")
                .fName("John")
                .lName("Doe")
                .build();

        when(adopterService.getAdopterById("adopter-1")).thenReturn(response);

        mockMvc.perform(get("/api/v1/adopters/adopter-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.adopterId").value("adopter-1"));
    }

    @Test
    void testGetAdopterById_NotFound() throws Exception {
        when(adopterService.getAdopterById(anyString()))
                .thenThrow(new NotFoundException("Adopter not found"));

        mockMvc.perform(get("/api/v1/adopters/notfound"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllAdopters() throws Exception {
        AdopterResponseModel response = AdopterResponseModel.builder()
                .adopterId("adopter-1")
                .fName("John")
                .lName("Doe")
                .build();

        when(adopterService.getAllAdopters()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/adopters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].adopterId").value("adopter-1"));
    }

    @Test
    void testAddAdopter_Success() throws Exception {
        AdopterRequestModel request = AdopterRequestModel.builder()
                .fName("Jane")
                .lName("Smith")
                .address(AdopterAddress.builder()
                        .streetAddress("123 Street")
                        .city("City")
                        .province("Province")
                        .postalCode("A1A1A1")
                        .build())
                .phoneNumber(AdopterPhoneNumber.builder()
                        .phoneNumber("123-456-7890")
                        .build())
                .contactMethodPreference(AdopterContactMethodPreference.EMAIL)
                .build();

        AdopterResponseModel response = AdopterResponseModel.builder()
                .adopterId("adopter-2")
                .fName("Jane")
                .lName("Smith")
                .build();

        when(adopterService.createAdopter(any(AdopterRequestModel.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/adopters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.adopterId").value("adopter-2"));
    }

    @Test
    void testAddAdopter_InvalidInput() throws Exception {
        when(adopterService.createAdopter(any(AdopterRequestModel.class)))
                .thenThrow(new InvalidInputException("Invalid input"));

        AdopterRequestModel request = AdopterRequestModel.builder()
                .fName("Jane")
                .lName("Smith")
                .build(); // Missing required fields (e.g., address, phone)

        mockMvc.perform(post("/api/v1/adopters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testUpdateAdopter_Success() throws Exception {
        AdopterRequestModel request = AdopterRequestModel.builder()
                .fName("UpdatedName")
                .lName("UpdatedSurname")
                .address(AdopterAddress.builder()
                        .streetAddress("456 Avenue")
                        .city("NewCity")
                        .province("NewProvince")
                        .postalCode("B2B2B2")
                        .build())
                .phoneNumber(AdopterPhoneNumber.builder()
                        .phoneNumber("987-654-3210")
                        .build())
                .contactMethodPreference(AdopterContactMethodPreference.EMAIL)
                .build();

        AdopterResponseModel response = AdopterResponseModel.builder()
                .adopterId("adopter-1")
                .fName("UpdatedName")
                .lName("UpdatedSurname")
                .build();

        when(adopterService.updateAdopter(any(AdopterRequestModel.class), anyString()))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/adopters/adopter-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adopterId").value("adopter-1"));
    }

    @Test
    void testDeleteAdopter() throws Exception {
        mockMvc.perform(delete("/api/v1/adopters/adopter-1"))
                .andExpect(status().isNoContent());
    }
}
