package com.roos.adoptioncenter.apigateway.DomainClientLayer.adopter;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.roos.adoptioncenter.apigateway.ExceptionsHandling.HttpErrorInfo;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.InvalidInputException;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
import com.roos.adoptioncenter.apigateway.presentationlayer.adopter.AdopterResponseModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.adopter.AdopterRequestModel;
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
class AdopterServiceClientTest {

    @InjectMocks
    private AdopterServiceClient adopterServiceClient;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private String host = "localhost";
    private String port = "8080";

    @BeforeEach
    void setup() {
        adopterServiceClient = new AdopterServiceClient(restTemplate, objectMapper, host, port);
    }

    @Test
    void testGetAdopterById_Success() {
        String adopterId = "123";
        AdopterResponseModel mockAdopter = AdopterResponseModel.builder()
                .adopterId(adopterId)
                .fName("John")
                .lName("Doe")
                .build();
        when(restTemplate.getForObject(anyString(), eq(AdopterResponseModel.class)))
                .thenReturn(mockAdopter);

        AdopterResponseModel result = adopterServiceClient.getAdopterById(adopterId);

        assertNotNull(result);
        assertEquals(adopterId, result.getAdopterId());
        assertEquals("John", result.getFName());
    }

    @Test
    void testGetAllAdopters_Success() {
        List<AdopterResponseModel> adopters = List.of(AdopterResponseModel.builder()
                .adopterId("123")
                .fName("John")
                .lName("Doe")
                .build());
        ResponseEntity<List<AdopterResponseModel>> response = new ResponseEntity<>(adopters, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class))).thenReturn(response);

        List<AdopterResponseModel> result = adopterServiceClient.getAllAdopters();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testAddAdopter_Success() {
        AdopterRequestModel request = AdopterRequestModel.builder()
                .fName("John")
                .lName("Doe")
                .build();

        AdopterResponseModel response = AdopterResponseModel.builder()
                .adopterId("123")
                .fName("John")
                .lName("Doe")
                .build();

        when(restTemplate.postForObject(anyString(), eq(request), eq(AdopterResponseModel.class))).thenReturn(response);

        AdopterResponseModel result = adopterServiceClient.addAdopter(request);

        assertNotNull(result);
    }

    @Test
    void testUpdateAdopter_Success() {
        AdopterRequestModel request = AdopterRequestModel.builder()
                .fName("John")
                .lName("Doe")
                .build();

        AdopterResponseModel updated = AdopterResponseModel.builder()
                .adopterId("123")
                .fName("John")
                .lName("Doe Updated")
                .build();

        String adopterId = "123";

        when(restTemplate.getForObject(anyString(), eq(AdopterResponseModel.class)))
                .thenReturn(updated);

        AdopterResponseModel result = adopterServiceClient.updateAdopter(request, adopterId);

        assertNotNull(result);
        assertEquals("John", result.getFName());
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
        when(restTemplate.getForObject(anyString(), eq(AdopterResponseModel.class))).thenThrow(ex);
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.NOT_FOUND, "Not found", "/adopters/" + adopterId));

        assertThrows(NotFoundException.class, () -> adopterServiceClient.getAdopterById(adopterId));
    }

    @Test
    void testAddAdopter_InvalidInput() throws Exception {
        AdopterRequestModel request = AdopterRequestModel.builder()
                .fName("John")
                .lName("Doe")
                .build();
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.UNPROCESSABLE_ENTITY, "Invalid Input", null, null, null);
        when(restTemplate.postForObject(anyString(), eq(request), eq(AdopterResponseModel.class))).thenThrow(ex);
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
        AdopterRequestModel request = AdopterRequestModel.builder()
                .fName("John")
                .lName("Doe")
                .build();

        String adopterId = "123";

        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", null, null, null);
        when(restTemplate.getForObject(anyString(), eq(AdopterResponseModel.class))).thenThrow(ex);
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.NOT_FOUND, "Not found", "/adopters/" + adopterId));

        assertThrows(NotFoundException.class, () -> adopterServiceClient.updateAdopter(request, adopterId));
    }

    @Test
    void testGetAllAdopters_EmptyList() {
        ResponseEntity<List<AdopterResponseModel>> response = new ResponseEntity<>(List.of(), HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);

        List<AdopterResponseModel> result = adopterServiceClient.getAllAdopters();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAdopterById_Http404_ShouldThrowNotFoundException() throws Exception {
        var ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null);
        when(restTemplate.getForObject(anyString(), eq(AdopterResponseModel.class))).thenThrow(ex);
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.NOT_FOUND, "Not Found", "/adopters/123"));

        assertThrows(NotFoundException.class, () -> adopterServiceClient.getAdopterById("123"));
    }




    @Test
    void testGetAllAdopters_Empty() {
        var response = new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class))).thenReturn(response);

        var result = adopterServiceClient.getAllAdopters();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    void testAddAdopter_InvalidInput_ShouldThrow() throws Exception {
        var request = AdopterRequestModel.builder().fName("John").lName("Doe").build();
        var ex = HttpClientErrorException.create(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid", null, null, null);
        when(restTemplate.postForObject(anyString(), eq(request), eq(AdopterResponseModel.class))).thenThrow(ex);
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid", "/adopters"));

        assertThrows(InvalidInputException.class, () -> adopterServiceClient.addAdopter(request));
    }





    @Test
    void testDeleteAdopter_NotFound_ShouldThrow() throws Exception {
        var ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null);
        doThrow(ex).when(restTemplate).delete(anyString());
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.NOT_FOUND, "Not Found", "/adopters/123"));

        assertThrows(NotFoundException.class, () -> adopterServiceClient.deleteAdopter("123"));
    }


}
