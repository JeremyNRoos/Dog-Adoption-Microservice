package com.roos.adoptioncenter.apigateway.DomainClientLayer.dog;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.HttpErrorInfo;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.InvalidInputException;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
import com.roos.adoptioncenter.apigateway.presentationlayer.dog.DogResponseModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.dog.DogRequestModel;
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
class DogServiceClientTest {

    @InjectMocks
    private DogServiceClient dogServiceClient;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private String host = "localhost";
    private String port = "8081";

    @BeforeEach
    void setup() {
        dogServiceClient = new DogServiceClient(restTemplate, objectMapper, host, port);
    }

    @Test
    void testGetDogById_Success() {
        String dogId = "d1";
        DogResponseModel mockDog = DogResponseModel.builder()
                .dogId(dogId)
                .name("Rex")
                .breed(DogBreedEnum.AFGHAN_HOUND)
                .build();
        when(restTemplate.getForObject(anyString(), eq(DogResponseModel.class)))
                .thenReturn(mockDog);

        DogResponseModel result = dogServiceClient.getDogById("adopter1", dogId);

        assertNotNull(result);
        assertEquals(dogId, result.getDogId());
        assertEquals("Rex", result.getName());
    }

    @Test
    void testGetAllDogs_Success() {
        List<DogResponseModel> dogs = List.of(DogResponseModel.builder()
                .dogId("d1").name("Rex").breed(DogBreedEnum.AFGHAN_HOUND).build());
        ResponseEntity<List<DogResponseModel>> response = new ResponseEntity<>(dogs, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class))).thenReturn(response);

        List<DogResponseModel> result = dogServiceClient.getAllDogs("adopter1");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testAddDog_Success() {
        DogRequestModel request = DogRequestModel.builder()
                .name("Rex")
                .breed(DogBreedEnum.AFGHAN_HOUND)
                .build();

        DogResponseModel response = DogResponseModel.builder()
                .dogId("d1")
                .name("Rex")
                .breed(DogBreedEnum.AFGHAN_HOUND)
                .build();

        when(restTemplate.postForObject(anyString(), eq(request), eq(DogResponseModel.class)))
                .thenReturn(response);

        DogResponseModel result = dogServiceClient.addDog("adopter1", request);

        assertNotNull(result);
        assertEquals("d1", result.getDogId());
        assertEquals("Rex", result.getName());
        assertEquals(DogBreedEnum.AFGHAN_HOUND, result.getBreed());
    }

    @Test
    void testUpdateDog_Success() {
        DogRequestModel request = DogRequestModel.builder().name("Rex").breed(DogBreedEnum.AFGHAN_HOUND).build();
        DogResponseModel updated = DogResponseModel.builder().dogId("d1").name("Rex").breed(DogBreedEnum.AFGHAN_HOUND).build();

        when(restTemplate.getForObject(anyString(), eq(DogResponseModel.class))).thenReturn(updated);

        DogResponseModel result = dogServiceClient.updateDog("adopter1", request, "d1");

        assertNotNull(result);
        assertEquals("Rex", result.getName());
    }

    @Test
    void testDeleteDog_Success() {
        doNothing().when(restTemplate).delete(anyString());
        assertDoesNotThrow(() -> dogServiceClient.deleteDog("adopter1", "d1"));
    }

    @Test
    void testGetDogById_NotFound() throws Exception {
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null);
        when(restTemplate.getForObject(anyString(), eq(DogResponseModel.class))).thenThrow(ex);
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.NOT_FOUND, "Not found", "/dogs/adopter1/d1"));

        assertThrows(NotFoundException.class, () -> dogServiceClient.getDogById("adopter1", "d1"));
    }

    @Test
    void testAddDog_InvalidInput() throws Exception {
        DogRequestModel request = DogRequestModel.builder().name("Rex").breed(DogBreedEnum.AFGHAN_HOUND).build();
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid Input", null, null, null);
        when(restTemplate.postForObject(anyString(), eq(request), eq(DogResponseModel.class))).thenThrow(ex);
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid input", "/dogs"));

        assertThrows(InvalidInputException.class, () -> dogServiceClient.addDog("adopter1", request));
    }

    @Test
    void testDeleteDog_NotFound() throws Exception {
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null);
        doThrow(ex).when(restTemplate).delete(anyString());
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.NOT_FOUND, "Not found", "/dogs/adopter1/d1"));

        assertThrows(NotFoundException.class, () -> dogServiceClient.deleteDog("adopter1", "d1"));
    }

    @Test
    void testGetAllDogs_HttpClientError() throws Exception {
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error", null, null, null);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(ex);

        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () -> dogServiceClient.getAllDogs("adopter1"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatusCode());
    }

    @Test
    void testUpdateDog_NotFoundOnGet() throws Exception {
        DogRequestModel request = DogRequestModel.builder().name("Rex").breed(DogBreedEnum.AFGHAN_HOUND).build();
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null);
        when(restTemplate.getForObject(anyString(), eq(DogResponseModel.class))).thenThrow(ex);
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.NOT_FOUND, "Not found", "/dogs/adopter1/d1"));

        assertThrows(NotFoundException.class, () -> dogServiceClient.updateDog("adopter1", request, "d1"));
    }

    @Test
    void testGetAllDogs_EmptyList() {
        ResponseEntity<List<DogResponseModel>> response = new ResponseEntity<>(List.of(), HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);

        List<DogResponseModel> result = dogServiceClient.getAllDogs("adopter1");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


}
