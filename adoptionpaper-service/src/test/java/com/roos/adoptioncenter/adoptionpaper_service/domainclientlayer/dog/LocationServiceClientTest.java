package com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.dog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roos.adoptioncenter.adoptionpaper_service.utils.ExceptionsHandling.HttpErrorInfo;
import com.roos.adoptioncenter.adoptionpaper_service.utils.ExceptionsHandling.InvalidInputException;
import com.roos.adoptioncenter.adoptionpaper_service.utils.ExceptionsHandling.NotFoundException;
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

    private String locationId = "loc1";

    @BeforeEach
    void setup() {
        locationServiceClient = new LocationServiceClient(restTemplate, objectMapper, "localhost", "8080");
    }

    @Test
    void testGetLocationById_Success() {
        LocationModel location = LocationModel.builder().locationId(locationId).name("Happy Shelter").shelterType(ShelterTypeEnum.SHELTER).build();
        when(restTemplate.getForObject(anyString(), eq(LocationModel.class))).thenReturn(location);

        LocationModel result = locationServiceClient.getLocationById(locationId);

        assertNotNull(result);
        assertEquals(locationId, result.getLocationId());
    }

    @Test
    void testGetAllLocations_Success() {
        List<LocationModel> locations = List.of(LocationModel.builder().locationId(locationId).name("Happy Shelter").shelterType(ShelterTypeEnum.SHELTER).build());
        ResponseEntity<List<LocationModel>> response = new ResponseEntity<>(locations, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);

        List<LocationModel> result = locationServiceClient.getAllLocations();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testAddLocation_Success() {
        LocationModel request = LocationModel.builder().locationId(locationId).name("Happy Shelter").shelterType(ShelterTypeEnum.SHELTER).build();
        LocationModel response = request;
        when(restTemplate.postForObject(anyString(), eq(request), eq(LocationModel.class))).thenReturn(response);

        LocationModel result = locationServiceClient.addLocation(request);

        assertNotNull(result);
        assertEquals(locationId, result.getLocationId());
    }

    @Test
    void testUpdateLocation_Success() {
        LocationModel request = LocationModel.builder().locationId(locationId).name("Happy Shelter").shelterType(ShelterTypeEnum.SHELTER).build();
        LocationModel updated = LocationModel.builder().locationId(locationId).name("Updated Shelter").shelterType(ShelterTypeEnum.FOSTERHOME).build();
        when(restTemplate.getForObject(anyString(), eq(LocationModel.class))).thenReturn(updated);
        doNothing().when(restTemplate).put(anyString(), eq(request));

        LocationModel result = locationServiceClient.updateLocation(locationId, request);

        assertNotNull(result);
        assertEquals("Updated Shelter", result.getName());
    }

    @Test
    void testDeleteLocation_Success() {
        doNothing().when(restTemplate).delete(anyString());
        assertDoesNotThrow(() -> locationServiceClient.deleteLocation(locationId));
    }

    @Test
    void testGetLocationById_NotFound() throws Exception {
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null);
        when(restTemplate.getForObject(anyString(), eq(LocationModel.class))).thenThrow(ex);
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.NOT_FOUND, "Location not found", "/locations/" + locationId));

        assertThrows(NotFoundException.class, () -> locationServiceClient.getLocationById(locationId));
    }

    @Test
    void testAddLocation_InvalidInput() throws Exception {
        LocationModel request = LocationModel.builder().locationId(locationId).name("Happy Shelter").shelterType(ShelterTypeEnum.SHELTER).build();
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid Input", null, null, null);
        when(restTemplate.postForObject(anyString(), eq(request), eq(LocationModel.class))).thenThrow(ex);
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid input", "/locations"));

        assertThrows(InvalidInputException.class, () -> locationServiceClient.addLocation(request));
    }

    @Test
    void testDeleteLocation_NotFound() throws Exception {
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null);
        doThrow(ex).when(restTemplate).delete(anyString());
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.NOT_FOUND, "Location not found", "/locations/" + locationId));

        assertThrows(NotFoundException.class, () -> locationServiceClient.deleteLocation(locationId));
    }

    @Test
    void testGetAllLocations_HttpClientError() throws Exception {
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error", null, null, null);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class))).thenThrow(ex);

        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () -> locationServiceClient.getAllLocations());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatusCode());
    }

    @Test
    void testUpdateLocation_HttpClientError_InvalidInput() throws Exception {
        LocationModel request = LocationModel.builder().locationId(locationId).name("Happy Shelter").shelterType(ShelterTypeEnum.SHELTER).build();
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid Input", null, null, null);
        doThrow(ex).when(restTemplate).put(anyString(), eq(request));
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid input", "/locations/" + locationId));

        assertThrows(InvalidInputException.class, () -> locationServiceClient.updateLocation(locationId, request));
    }
}