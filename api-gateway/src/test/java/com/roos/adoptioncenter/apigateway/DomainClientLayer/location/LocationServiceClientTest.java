package com.roos.adoptioncenter.apigateway.DomainClientLayer.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.HttpErrorInfo;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.InvalidInputException;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
import com.roos.adoptioncenter.apigateway.presentationlayer.location.LocationRequestModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.location.LocationResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceClientTest {

    @InjectMocks
    private LocationServiceClient locationServiceClient;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private String host = "localhost";
    private String port = "8081";

    @BeforeEach
    void setup() {
        locationServiceClient = new LocationServiceClient(restTemplate, objectMapper, host, port);
    }

    @Test
    void testGetLocationById_Success() {
        String locationId = "loc1";
        LocationResponseModel mockLocation = LocationResponseModel.builder()
                .locationId(locationId)
                .name("Main Shelter")
                .capacity(100)
                .availableSpace(20)
                .build();
        when(restTemplate.getForObject(anyString(), eq(LocationResponseModel.class)))
                .thenReturn(mockLocation);

        LocationResponseModel result = locationServiceClient.getLocationById(locationId);

        assertNotNull(result);
        assertEquals(locationId, result.getLocationId());
        assertEquals("Main Shelter", result.getName());
    }

    @Test
    void testGetAllLocations_Success() {
        List<LocationResponseModel> locations = List.of(
                LocationResponseModel.builder().locationId("loc1").name("Main Shelter").build()
        );
        ResponseEntity<List<LocationResponseModel>> response = new ResponseEntity<>(locations, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);

        List<LocationResponseModel> result = locationServiceClient.getAllLocations();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testAddLocation_Success() {
        LocationRequestModel request = LocationRequestModel.builder()
                .name("Main Shelter")
                .capacity(100)
                .availableSpace(20)
                .build();
        LocationResponseModel response = LocationResponseModel.builder()
                .locationId("loc1")
                .name("Main Shelter")
                .capacity(100)
                .availableSpace(20)
                .build();

        when(restTemplate.postForObject(anyString(), eq(request), eq(LocationResponseModel.class)))
                .thenReturn(response);

        LocationResponseModel result = locationServiceClient.addLocation(request);

        assertNotNull(result);
        assertEquals("loc1", result.getLocationId());
    }

    @Test
    void testUpdateLocation_Success() {
        LocationRequestModel request = LocationRequestModel.builder().name("Updated Shelter").build();
        LocationResponseModel updated = LocationResponseModel.builder().locationId("loc1").name("Updated Shelter").build();

        when(restTemplate.getForObject(anyString(), eq(LocationResponseModel.class))).thenReturn(updated);

        LocationResponseModel result = locationServiceClient.updateLocation("loc1", request);

        assertNotNull(result);
        assertEquals("Updated Shelter", result.getName());
    }

    @Test
    void testDeleteLocation_Success() {
        doNothing().when(restTemplate).delete(anyString());
        assertDoesNotThrow(() -> locationServiceClient.deleteLocation("loc1"));
    }

    @Test
    void testAddLocation_InvalidInput() throws Exception {
        LocationRequestModel request = LocationRequestModel.builder().name("Main Shelter").build();
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid Input", null, null, null);
        when(restTemplate.postForObject(anyString(), eq(request), eq(LocationResponseModel.class))).thenThrow(ex);
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid input", "/locations"));

        assertThrows(InvalidInputException.class, () -> locationServiceClient.addLocation(request));
    }

    @Test
    void testDeleteLocation_NotFound() throws Exception {
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null);
        doThrow(ex).when(restTemplate).delete(anyString());
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.NOT_FOUND, "Not found", "/locations/loc1"));

        assertThrows(NotFoundException.class, () -> locationServiceClient.deleteLocation("loc1"));
    }

    @Test
    void testGetAllLocations_HttpClientError() throws Exception {
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error", null, null, null);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(ex);

        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () -> locationServiceClient.getAllLocations());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatusCode());
    }



    @Test
    void testGetAllLocations_EmptyList() {
        ResponseEntity<List<LocationResponseModel>> response = new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);

        List<LocationResponseModel> result = locationServiceClient.getAllLocations();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


}
