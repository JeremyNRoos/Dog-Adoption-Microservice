package com.roos.adoptioncenter.apigateway.presentationlayer.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.InvalidInputException;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
import com.roos.adoptioncenter.apigateway.businesslayer.location.LocationService;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.location.LocationAddress;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.location.ShelterTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationController.class)
class LocationControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LocationService locationService;

    @Autowired
    private ObjectMapper objectMapper;

    private LocationResponseModel locationResponseModel;
    private LocationRequestModel locationRequestModel;

    @BeforeEach
    void setUp() {
        LocationAddress address = LocationAddress.builder()
                .streetAddress("123 Main Street")
                .city("Springfield")
                .province("Province")
                .country("Country")
                .postalCode("12345")
                .build();

        locationResponseModel = LocationResponseModel.builder()
                .locationId(UUID.randomUUID().toString())
                .name("Happy Shelter")
                .shelterType(ShelterTypeEnum.SHELTER)
                .address(address)
                .capacity(50)
                .availableSpace(30)
                .build();

        locationRequestModel = LocationRequestModel.builder()
                .name("Happy Shelter")
                .shelterType(ShelterTypeEnum.SHELTER)
                .streetAddress("123 Main Street")
                .city("Springfield")
                .province("Province")
                .country("Country")
                .postalCode("12345")
                .capacity(50)
                .availableSpace(30)
                .build();
    }

    @Test
    void testGetLocationById_Success() throws Exception {
        when(locationService.getLocationById(anyString())).thenReturn(locationResponseModel);

        mockMvc.perform(get("/api/v1/locations/" + locationResponseModel.getLocationId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.locationId").value(locationResponseModel.getLocationId()));
    }

    @Test
    void testGetLocationById_NotFound() throws Exception {
        when(locationService.getLocationById(anyString())).thenThrow(new NotFoundException("Location not found"));

        mockMvc.perform(get("/api/v1/locations/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllLocations() throws Exception {
        when(locationService.getAllLocations()).thenReturn(List.of(locationResponseModel));

        mockMvc.perform(get("/api/v1/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].locationId").value(locationResponseModel.getLocationId()));
    }

    @Test
    void testAddLocation_Success() throws Exception {
        when(locationService.addLocation(any(LocationRequestModel.class))).thenReturn(locationResponseModel);

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationRequestModel)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.locationId").value(locationResponseModel.getLocationId()));
    }

    @Test
    void testAddLocation_InvalidInput() throws Exception {
        when(locationService.addLocation(any(LocationRequestModel.class)))
                .thenThrow(new InvalidInputException("Invalid input"));

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationRequestModel)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testUpdateLocation_Success() throws Exception {
        when(locationService.updateLocation(anyString(), any(LocationRequestModel.class)))
                .thenReturn(locationResponseModel);

        mockMvc.perform(put("/api/v1/locations/" + locationResponseModel.getLocationId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationRequestModel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locationId").value(locationResponseModel.getLocationId()));
    }

    @Test
    void testDeleteLocation_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/locations/" + locationResponseModel.getLocationId()))
                .andExpect(status().isNoContent());
    }
}
