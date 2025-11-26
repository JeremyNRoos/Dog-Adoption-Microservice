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

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DogServiceClientTest {

    @InjectMocks
    private DogServiceClient dogServiceClient;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private String locationId = "loc1";
    private String dogId = "dog123";

    @BeforeEach
    void setup() {
        dogServiceClient = new DogServiceClient(restTemplate, objectMapper, "localhost", "8080");
    }

    @Test
    void testGetDogById_Success() {
        DogModel dog = DogModel.builder().dogId(dogId).name("Buddy").age(5).build();
        when(restTemplate.getForObject(anyString(), eq(DogModel.class))).thenReturn(dog);

        DogModel result = dogServiceClient.getDogById(locationId, dogId);

        assertNotNull(result);
        assertEquals(dogId, result.getDogId());
    }

    @Test
    void testGetAllDogs_Success() {
        List<DogModel> dogs = List.of(DogModel.builder().dogId(dogId).name("Buddy").age(5).build());
        ResponseEntity<List<DogModel>> response = new ResponseEntity<>(dogs, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);

        List<DogModel> result = dogServiceClient.getAllDogs(locationId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testAddDog_Success() {
        DogModel request = DogModel.builder().dogId(dogId).name("Buddy").age(5).build();
        DogModel response = DogModel.builder().dogId(dogId).name("Buddy").age(5).build();
        when(restTemplate.postForObject(anyString(), eq(request), eq(DogModel.class))).thenReturn(response);

        DogModel result = dogServiceClient.addDog(locationId, request);

        assertNotNull(result);
        assertEquals(dogId, result.getDogId());
    }

    @Test
    void testUpdateDog_Success() {
        DogModel request = DogModel.builder().dogId(dogId).name("Buddy").age(5).build();
        DogModel updated = DogModel.builder().dogId(dogId).name("BuddyUpdated").age(6).build();
        when(restTemplate.getForObject(anyString(), eq(DogModel.class))).thenReturn(updated);
        doNothing().when(restTemplate).put(anyString(), eq(request));

        DogModel result = dogServiceClient.updateDog(locationId, request, dogId);

        assertNotNull(result);
        assertEquals("BuddyUpdated", result.getName());
    }

    @Test
    void testDeleteDog_Success() {
        doNothing().when(restTemplate).delete(anyString());
        assertDoesNotThrow(() -> dogServiceClient.deleteDog(locationId, dogId));
    }

    @Test
    void testGetDogById_NotFound() throws Exception {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", null, null, null);
        when(restTemplate.getForObject(anyString(), eq(DogModel.class))).thenThrow(ex);
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.NOT_FOUND, "Dog not found", "/locations/" + locationId + "/dogs/" + dogId));

        assertThrows(NotFoundException.class, () -> dogServiceClient.getDogById(locationId, dogId));
    }

    @Test
    void testAddDog_InvalidInput() throws Exception {
        DogModel request = DogModel.builder().dogId(dogId).name("Buddy").age(5).build();
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.UNPROCESSABLE_ENTITY, "Invalid Input", null, null, null);
        when(restTemplate.postForObject(anyString(), eq(request), eq(DogModel.class))).thenThrow(ex);
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid input", "/locations/" + locationId + "/dogs"));

        assertThrows(InvalidInputException.class, () -> dogServiceClient.addDog(locationId, request));
    }

    @Test
    void testDeleteDog_NotFound() throws Exception {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", null, null, null);
        doThrow(ex).when(restTemplate).delete(anyString());
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.NOT_FOUND, "Dog not found", "/locations/" + locationId + "/dogs/" + dogId));

        assertThrows(NotFoundException.class, () -> dogServiceClient.deleteDog(locationId, dogId));
    }

    @Test
    void testGetDogById_HttpClientError_InternalServerError() throws Exception {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error", null, null, null);
        when(restTemplate.getForObject(anyString(), eq(DogModel.class))).thenThrow(ex);

        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () -> dogServiceClient.getDogById(locationId, dogId));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatusCode());
    }

    @Test
    void testGetAllDogs_HttpClientError_NotFound() throws Exception {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", null, null, null);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(ex);
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.NOT_FOUND, "Dogs not found", "/locations/" + locationId + "/dogs"));

        assertThrows(NotFoundException.class, () -> dogServiceClient.getAllDogs(locationId));
    }

    @Test
    void testUpdateDog_HttpClientError_InvalidInput() throws Exception {
        DogModel request = DogModel.builder().dogId(dogId).name("Buddy").age(5).build();
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.UNPROCESSABLE_ENTITY, "Invalid Input", null, null, null);
        doThrow(ex).when(restTemplate).put(anyString(), eq(request));
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid input", "/locations/" + locationId + "/dogs/" + dogId));

        assertThrows(InvalidInputException.class, () -> dogServiceClient.updateDog(locationId, request, dogId));
    }

    @Test
    void testGetErrorMessage_IOExceptionFallback() {
        // Arrange
        String errorBody = "invalid json";
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error", null,
                errorBody.getBytes(), null);

        when(restTemplate.getForObject(anyString(), eq(DogModel.class))).thenThrow(ex);

        // Act & Assert
        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class,
                () -> dogServiceClient.getDogById(locationId, dogId));

        // Optional assertion
        assertEquals(ex.getMessage(), thrown.getMessage());
    }


    @Test
    void testHandleHttpClientException_NotFound() throws Exception {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", null, null, null);

        when(restTemplate.getForObject(anyString(), eq(DogModel.class))).thenThrow(ex);
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.NOT_FOUND, "Dog not found", "/locations/" + locationId + "/dogs/" + dogId));

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> dogServiceClient.getDogById(locationId, dogId));
        assertEquals("/locations/" + locationId + "/dogs/" + dogId, thrown.getMessage());
    }


    @Test
    void testHandleHttpClientException_InvalidInput() throws Exception {
        DogModel request = DogModel.builder()
                .dogId(dogId)
                .name("Buddy")
                .age(5)
                .build();

        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.UNPROCESSABLE_ENTITY, "Invalid Input", null, null, null);
        when(restTemplate.postForObject(anyString(), eq(request), eq(DogModel.class))).thenThrow(ex);
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid input", "/locations/" + locationId + "/dogs"));

        InvalidInputException thrown = assertThrows(InvalidInputException.class, () -> dogServiceClient.addDog(locationId, request));
        assertEquals("/locations/loc1/dogs", thrown.getMessage());
    }



    @Test
    void testGetAllDogs_HttpClientError_InternalServerError() throws Exception {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error", null, null, null);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(ex);
        lenient().when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", "/locations/" + locationId + "/dogs"));

        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () -> dogServiceClient.getAllDogs(locationId));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatusCode());
    }


    @Test
    void testGetAllDogs_EmptyList() {
        ResponseEntity<List<DogModel>> response = new ResponseEntity<>(List.of(), HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);

        List<DogModel> result = dogServiceClient.getAllDogs(locationId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testAddDog_ReturnsNull() {
        DogModel request = DogModel.builder().dogId(dogId).name("Buddy").age(5).build();
        when(restTemplate.postForObject(anyString(), eq(request), eq(DogModel.class))).thenReturn(null);

        DogModel result = dogServiceClient.addDog(locationId, request);

        assertNull(result);
    }

    @Test
    void testUpdateDog_ReturnsNull() {
        DogModel request = DogModel.builder().dogId(dogId).name("Buddy").age(5).build();
        when(restTemplate.getForObject(anyString(), eq(DogModel.class))).thenReturn(null);
        doNothing().when(restTemplate).put(anyString(), eq(request));

        DogModel result = dogServiceClient.updateDog(locationId, request, dogId);

        assertNull(result);
    }

    @Test
    void testDeleteDog_InternalServerError() {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", null, null, null);
        doThrow(ex).when(restTemplate).delete(anyString());

        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () -> dogServiceClient.deleteDog(locationId, dogId));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatusCode());
    }

    @Test
    void testGetDogById_ReturnsNull() {
        when(restTemplate.getForObject(anyString(), eq(DogModel.class))).thenReturn(null);

        DogModel result = dogServiceClient.getDogById(locationId, dogId);

        assertNull(result);
    }




    @Test
    void testGetDogById_NetworkFailure() {
        when(restTemplate.getForObject(anyString(), eq(DogModel.class)))
                .thenThrow(new org.springframework.web.client.ResourceAccessException("Network failure"));

        Exception ex = assertThrows(org.springframework.web.client.ResourceAccessException.class,
                () -> dogServiceClient.getDogById(locationId, dogId));

        assertEquals("Network failure", ex.getMessage());
    }


    @Test
    void testGetAllDogs_LargeList() {
        List<DogModel> dogs = java.util.stream.IntStream.range(0, 1000)
                .mapToObj(i -> DogModel.builder().dogId("dog" + i).name("Dog" + i).age(i).build())
                .toList();
        ResponseEntity<List<DogModel>> response = new ResponseEntity<>(dogs, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);

        List<DogModel> result = dogServiceClient.getAllDogs(locationId);

        assertNotNull(result);
        assertEquals(1000, result.size());
    }






}
