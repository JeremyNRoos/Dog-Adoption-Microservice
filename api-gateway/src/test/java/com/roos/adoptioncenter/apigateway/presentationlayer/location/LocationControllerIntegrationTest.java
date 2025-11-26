package com.roos.adoptioncenter.apigateway.presentationlayer.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roos.adoptioncenter.apigateway.businesslayer.location.LocationService;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.location.LocationAddress;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.location.ShelterTypeEnum;
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


@WebMvcTest(LocationController.class)
class LocationControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LocationService locationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetLocationById_Success() throws Exception {
        LocationResponseModel response = LocationResponseModel.builder()
                .locationId("loc-1")
                .name("Shelter One")
                .shelterType(ShelterTypeEnum.SHELTER)
                .address(LocationAddress.builder()
                        .streetAddress("123 Main St")
                        .city("City")
                        .province("Province")
                        .country("Country")
                        .postalCode("12345")
                        .build())
                .capacity(100)
                .availableSpace(50)
                .build();

        when(locationService.getLocationById("loc-1")).thenReturn(response);

        mockMvc.perform(get("/api/v1/locations/loc-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.locationId").value("loc-1"));
    }

    @Test
    void testGetLocationById_NotFound() throws Exception {
        when(locationService.getLocationById("notfound"))
                .thenThrow(new NotFoundException("Location not found"));

        mockMvc.perform(get("/api/v1/locations/notfound"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllLocations() throws Exception {
        LocationResponseModel response = LocationResponseModel.builder()
                .locationId("loc-1")
                .name("Shelter One")
                .shelterType(ShelterTypeEnum.SHELTER)
                .address(LocationAddress.builder()
                        .streetAddress("123 Main St")
                        .city("City")
                        .province("Province")
                        .country("Country")
                        .postalCode("12345")
                        .build())
                .capacity(100)
                .availableSpace(50)
                .build();

        when(locationService.getAllLocations()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].locationId").value("loc-1"));
    }

    @Test
    void testAddLocation() throws Exception {
        LocationRequestModel request = LocationRequestModel.builder()
                .name("Shelter One")
                .shelterType(ShelterTypeEnum.SHELTER)
                .streetAddress("123 Main St")
                .city("City")
                .province("Province")
                .country("Country")
                .postalCode("12345")
                .capacity(100)
                .availableSpace(50)
                .build();

        LocationResponseModel response = LocationResponseModel.builder()
                .locationId("loc-1")
                .name("Shelter One")
                .shelterType(ShelterTypeEnum.SHELTER)
                .address(LocationAddress.builder()
                        .streetAddress("123 Main St")
                        .city("City")
                        .province("Province")
                        .country("Country")
                        .postalCode("12345")
                        .build())
                .capacity(100)
                .availableSpace(50)
                .build();

        when(locationService.addLocation(any(LocationRequestModel.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.locationId").value("loc-1"));
    }

    @Test
    void testAddLocation_InvalidInput() throws Exception {
        when(locationService.addLocation(any(LocationRequestModel.class)))
                .thenThrow(new InvalidInputException("Invalid input"));

        LocationRequestModel request = LocationRequestModel.builder()
                .name("Shelter One")
                .shelterType(ShelterTypeEnum.SHELTER)
                .streetAddress("123 Main St")
                .city("City")
                .province("Province")
                .country("Country")
                .postalCode("12345")
                .capacity(100)
                .availableSpace(50)
                .build();

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testUpdateLocation() throws Exception {
        LocationRequestModel request = LocationRequestModel.builder()
                .name("Shelter One Updated")
                .shelterType(ShelterTypeEnum.SHELTER)
                .streetAddress("123 Updated St")
                .city("Updated City")
                .province("Updated Province")
                .country("Updated Country")
                .postalCode("54321")
                .capacity(150)
                .availableSpace(100)
                .build();

        LocationResponseModel response = LocationResponseModel.builder()
                .locationId("loc-1")
                .name("Shelter One Updated")
                .shelterType(ShelterTypeEnum.SHELTER)
                .address(LocationAddress.builder()
                        .streetAddress("123 Updated St")
                        .city("Updated City")
                        .province("Updated Province")
                        .country("Updated Country")
                        .postalCode("54321")
                        .build())
                .capacity(150)
                .availableSpace(100)
                .build();

        when(locationService.updateLocation(anyString(), any(LocationRequestModel.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/locations/loc-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locationId").value("loc-1"));
    }

    @Test
    void testDeleteLocation() throws Exception {
        mockMvc.perform(delete("/api/v1/locations/loc-1"))
                .andExpect(status().isNoContent());
    }
}