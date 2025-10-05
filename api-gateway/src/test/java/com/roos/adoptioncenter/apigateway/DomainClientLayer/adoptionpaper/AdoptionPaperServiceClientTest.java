package com.roos.adoptioncenter.apigateway.DomainClientLayer.adoptionpaper;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.HttpErrorInfo;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.InvalidInputException;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
import com.roos.adoptioncenter.apigateway.presentationlayer.adoptionPaper.AdoptionPaperRequestModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.adoptionPaper.AdoptionPaperResponseModel;
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
class AdoptionPaperServiceClientTest {

    @InjectMocks
    private AdoptionPaperServiceClient adoptionPaperServiceClient;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        String host = "localhost";  // or mock URL
        String port = "8080";       // or desired port for testing
        adoptionPaperServiceClient = new AdoptionPaperServiceClient(restTemplate, objectMapper, host, port);
    }


    @Test
    void testGetAdoptionPaperById_Success() {
        String adopterId = "adopter1";
        String paperId = "paper1";
        AdoptionPaperResponseModel response = AdoptionPaperResponseModel.builder()
                .adoptionPaperId(paperId)
                .adopterId(adopterId)
                .build();

        when(restTemplate.getForObject(anyString(), eq(AdoptionPaperResponseModel.class))).thenReturn(response);

        var result = adoptionPaperServiceClient.getAdoptionById(adopterId, paperId);
        assertNotNull(result);
        assertEquals(paperId, result.getAdoptionPaperId());
    }

    @Test
    void testGetAllAdoptionPapers_Success() {
        String adopterId = "adopter1";
        List<AdoptionPaperResponseModel> papers = List.of(AdoptionPaperResponseModel.builder().adoptionPaperId("p1").build());
        ResponseEntity<List<AdoptionPaperResponseModel>> response = new ResponseEntity<>(papers, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class))).thenReturn(response);

        var result = adoptionPaperServiceClient.getAllAdoptioPapers(adopterId);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testAddAdoptionPaper_Success() {
        AdoptionPaperRequestModel request = AdoptionPaperRequestModel.builder().dogId("d1").locationId("loc").build();
        AdoptionPaperResponseModel response = AdoptionPaperResponseModel.builder().adoptionPaperId("p1").build();
        when(restTemplate.postForObject(anyString(), eq(request), eq(AdoptionPaperResponseModel.class))).thenReturn(response);

        var result = adoptionPaperServiceClient.addAdoptionPaper("adopter1", request);
        assertNotNull(result);
    }

    @Test
    void testUpdateAdoptionPaper_Success() {
        AdoptionPaperRequestModel request = AdoptionPaperRequestModel.builder().dogId("d1").locationId("loc").build();
        AdoptionPaperResponseModel response = AdoptionPaperResponseModel.builder().adoptionPaperId("p1").build();
        when(restTemplate.getForObject(anyString(), eq(AdoptionPaperResponseModel.class))).thenReturn(response);

        var result = adoptionPaperServiceClient.updateAdoptionPaper("adopter1", request, "p1");
        assertNotNull(result);
    }

    @Test
    void testDeleteAdoptionPaper_Success() {
        doNothing().when(restTemplate).delete(anyString());
        assertDoesNotThrow(() -> adoptionPaperServiceClient.deleteAdoptionPaper("adopter1", "p1"));
    }


    @Test
    void testAddAdoptionPaper_InvalidInput() throws Exception {
        AdoptionPaperRequestModel request = AdoptionPaperRequestModel.builder().dogId("d1").locationId("loc").build();
        var ex = HttpClientErrorException.create(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid Input", null, null, null);
        when(restTemplate.postForObject(anyString(), eq(request), eq(AdoptionPaperResponseModel.class))).thenThrow(ex);
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid input", "/adoptionpapers"));

        assertThrows(InvalidInputException.class, () -> adoptionPaperServiceClient.addAdoptionPaper("adopter1", request));
    }

    @Test
    void testDeleteAdoptionPaper_NotFound() throws Exception {
        String paperId = "p1";
        var ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null);
        doThrow(ex).when(restTemplate).delete(anyString());
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(HttpStatus.NOT_FOUND, "Not found", "/adoptionpapers/" + paperId));

        assertThrows(NotFoundException.class, () -> adoptionPaperServiceClient.deleteAdoptionPaper("adopter1", paperId));
    }

    @Test
    void testGetAllAdoptionPapers_HttpClientError() {
        var ex = HttpClientErrorException.create(HttpStatus.INTERNAL_SERVER_ERROR, "Error", null, null, null);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class))).thenThrow(ex);

        var thrown = assertThrows(HttpClientErrorException.class, () -> adoptionPaperServiceClient.getAllAdoptioPapers("adopter1"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatusCode());
    }




    @Test
    void testGetAllAdoptionPapers_Empty() {
        var response = new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class))).thenReturn(response);

        var result = adoptionPaperServiceClient.getAllAdoptioPapers("adopter1");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


}
