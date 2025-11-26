package com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.adopter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roos.adoptioncenter.adoptionpaper_service.utils.ExceptionsHandling.HttpErrorInfo;
import com.roos.adoptioncenter.adoptionpaper_service.utils.ExceptionsHandling.InvalidInputException;
import com.roos.adoptioncenter.adoptionpaper_service.utils.ExceptionsHandling.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdopterServiceClientTest {

    @InjectMocks
    private AdopterServiceClient adopterServiceClient;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Value("${app.adopter-service.host}")
    private String host = "localhost";

    @Value("${app.adopter-service.port}")
    private String port = "8080";

    @BeforeEach
    void setup() {
        adopterServiceClient = new AdopterServiceClient(restTemplate, objectMapper, host, port);
    }

    @Test
    void testGetAdopterById_Success() {
        String adopterId = "123";
        AdopterModel mockAdopter = AdopterModel.builder()
                .adopterId(adopterId)
                .FName("John")
                .LName("Doe")
                .build();
        when(restTemplate.getForObject(anyString(), eq(AdopterModel.class)))
                .thenReturn(mockAdopter);

        AdopterModel result = adopterServiceClient.getAdopterById(adopterId);

        assertNotNull(result);
        assertEquals(adopterId, result.getAdopterId());
        assertEquals("John", result.getFName());
    }

    @Test
    void testGetAllAdopters_Success() {
        List<AdopterModel> adopters = List.of(AdopterModel.builder()
                .adopterId("123")
                .FName("John")
                .LName("Doe")
                .build());
        ResponseEntity<List<AdopterModel>> response = new ResponseEntity<>(adopters, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class))).thenReturn(response);

        List<AdopterModel> result = adopterServiceClient.getAllAdopters();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testAddAdopter_Success() {
        AdopterModel request = AdopterModel.builder()
                .adopterId("123")
                .FName("John")
                .LName("Doe")
                .build();

        AdopterModel response = AdopterModel.builder()
                .adopterId("123")
                .FName("John")
                .LName("Doe")
                .build();

        when(restTemplate.postForObject(anyString(), eq(request), eq(AdopterModel.class))).thenReturn(response);

        AdopterModel result = adopterServiceClient.addAdopter(request);

        assertNotNull(result);
    }

    @Test
    void testUpdateAdopter_Success() {
        AdopterModel request = AdopterModel.builder()
                .adopterId("123")
                .FName("John")
                .LName("Doe")
                .build();

        AdopterModel updated = AdopterModel.builder()
                .adopterId("123")
                .FName("John")
                .LName("Doe Updated")
                .build();

        String adopterId = "123";

        when(restTemplate.getForObject(anyString(), eq(AdopterModel.class)))
                .thenReturn(updated);

        AdopterModel result = adopterServiceClient.updateAdopter(request, adopterId);

        assertNotNull(result);
        assertEquals("John", result.getFName());  // Optional: add more assertions to check the data
    }


    @Test
    void testDeleteAdopter_Success() {
        doNothing().when(restTemplate).delete(anyString());

        assertDoesNotThrow(() -> adopterServiceClient.deleteAdopter("123"));
    }

    @Test
    void testGetAdopterById_NotFound() throws Exception {
        String adopterId = "123";
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", null, null, null);
        when(restTemplate.getForObject(anyString(), eq(AdopterModel.class))).thenThrow(ex);
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.NOT_FOUND, "Not found", "/some/path"));

        assertThrows(NotFoundException.class, () -> adopterServiceClient.getAdopterById(adopterId));
    }

    @Test
    void testAddAdopter_InvalidInput() throws Exception {
        AdopterModel request = AdopterModel.builder()
                .adopterId("123")
                .FName("John")
                .LName("Doe")
                .build();
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.UNPROCESSABLE_ENTITY, "Invalid Input", null, null, null);
        when(restTemplate.postForObject(anyString(), eq(request), eq(AdopterModel.class))).thenThrow(ex);
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid input", "/adopters"));

        assertThrows(InvalidInputException.class, () -> adopterServiceClient.addAdopter(request));
    }

    @Test
    void testDeleteAdopter_NotFound() throws Exception {
        String adopterId = "123";
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", null, null, null);
        doThrow(ex).when(restTemplate).delete(anyString());
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.NOT_FOUND, "Not found", "/adopters/" + adopterId));

        assertThrows(NotFoundException.class, () -> adopterServiceClient.deleteAdopter(adopterId));
    }

    @Test
    void testGetAllAdopters_HttpClientError() throws Exception {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error", null, null, null);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(ex);

        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () -> adopterServiceClient.getAllAdopters());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatusCode());
    }

    @Test
    void testUpdateAdopter_NotFoundOnGet() throws Exception {
        AdopterModel request = AdopterModel.builder()
                .adopterId("123")
                .FName("John")
                .LName("Doe")
                .build();

        String adopterId = "123";

        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", null, null, null);
        when(restTemplate.getForObject(anyString(), eq(AdopterModel.class))).thenThrow(ex);
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.NOT_FOUND, "Not found", "/adopters/" + adopterId));

        assertThrows(NotFoundException.class, () -> adopterServiceClient.updateAdopter(request, adopterId));
    }


}
