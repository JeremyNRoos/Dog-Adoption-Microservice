package com.roos.adoptioncenter.apigateway.businesslayer.adoptionPaper;

import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.adoptionpaper.AdoptionPaperServiceClient;
import com.roos.adoptioncenter.apigateway.presentationlayer.adoptionPaper.AdoptionPaperRequestModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.adoptionPaper.AdoptionPaperResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdoptionPaperServiceUnitTest {

    @Mock
    private AdoptionPaperServiceClient adoptionPaperServiceClient;

    @InjectMocks
    private AdoptionPaperServiceImpl adoptionPaperService;

    private AdoptionPaperRequestModel requestModel;
    private AdoptionPaperResponseModel responseModel;
    private String adopterId;
    private String adoptionPaperId;

    @BeforeEach
    void setUp() {
        adopterId = UUID.randomUUID().toString();
        adoptionPaperId = UUID.randomUUID().toString();

        requestModel = AdoptionPaperRequestModel.builder()
                .adopterId(adopterId)
                .dogId("dog123")
                .locationId("loc456")
                .volunteerId("vol789")
                .build();

        responseModel = AdoptionPaperResponseModel.builder()
                .adoptionPaperId(adoptionPaperId)
                .adopterId(adopterId)
                .adopterFName("John")
                .adopterLName("Doe")
                .dogId("dog123")
                .dogName("Buddy")
                .dogAge(3)
                .locationId("loc456")
                .locationName("City Shelter")
                .locationShelterType("Public")
                .volunteerId("vol789")
                .volunteerFName("Jane")
                .volunteerLName("Smith")
                .build();
    }

    @Test
    void whenGetAdoptionPaperById_thenReturnAdoptionPaper() {
        when(adoptionPaperServiceClient.getAdoptionById(adopterId, adoptionPaperId)).thenReturn(responseModel);

        var result = adoptionPaperService.getAdoptionPaperById(adopterId, adoptionPaperId);

        assertNotNull(result);
        assertEquals(responseModel.getAdoptionPaperId(), result.getAdoptionPaperId());
        assertEquals(responseModel.getDogName(), result.getDogName());
    }

    @Test
    void whenGetAllAdoptionPapers_thenReturnList() {
        when(adoptionPaperServiceClient.getAllAdoptioPapers(adopterId)).thenReturn(List.of(responseModel));

        var result = adoptionPaperService.getAdoptionPapers(adopterId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(responseModel.getAdoptionPaperId(), result.get(0).getAdoptionPaperId());
    }

    @Test
    void whenGetAllAdoptionPapersIsEmpty_thenReturnEmptyList() {
        when(adoptionPaperServiceClient.getAllAdoptioPapers(adopterId)).thenReturn(Collections.emptyList());

        var result = adoptionPaperService.getAdoptionPapers(adopterId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void whenCreateAdoptionPaper_thenReturnCreatedAdoptionPaper() {
        when(adoptionPaperServiceClient.addAdoptionPaper(adopterId, requestModel)).thenReturn(responseModel);

        var result = adoptionPaperService.addAdoptionPaper(requestModel, adopterId);

        assertNotNull(result);
        assertEquals(responseModel.getAdoptionPaperId(), result.getAdoptionPaperId());
        assertEquals(responseModel.getDogName(), result.getDogName());
    }

    @Test
    void whenUpdateAdoptionPaper_thenReturnUpdatedAdoptionPaper() {
        when(adoptionPaperServiceClient.updateAdoptionPaper(adopterId, requestModel, adoptionPaperId)).thenReturn(responseModel);

        var result = adoptionPaperService.updateAdoptionPaper(adopterId, requestModel, adoptionPaperId);

        assertNotNull(result);
        assertEquals(responseModel.getAdoptionPaperId(), result.getAdoptionPaperId());
    }

    @Test
    void whenDeleteAdoptionPaper_thenVerifyClientCall() {
        doNothing().when(adoptionPaperServiceClient).deleteAdoptionPaper(adopterId, adoptionPaperId);

        adoptionPaperService.deleteAdoptionPaper(adopterId, adoptionPaperId);

        verify(adoptionPaperServiceClient, times(1)).deleteAdoptionPaper(adopterId, adoptionPaperId);
    }

    @Test
    void whenGetAdoptionPaperByIdNotFound_thenThrowNotFoundException() {
        when(adoptionPaperServiceClient.getAdoptionById(anyString(), anyString())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            adoptionPaperService.getAdoptionPaperById("nonexistent-adopter-id", "nonexistent-paper-id");
        });
    }

    @Test
    void whenAdoptionPaperServiceClientThrowsException_thenPropagate() {
        when(adoptionPaperServiceClient.getAdoptionById(anyString(), anyString()))
                .thenThrow(new RuntimeException("Service failure"));

        assertThrows(RuntimeException.class, () -> {
            adoptionPaperService.getAdoptionPaperById("some-id", "some-paper-id");
        });
    }
}
