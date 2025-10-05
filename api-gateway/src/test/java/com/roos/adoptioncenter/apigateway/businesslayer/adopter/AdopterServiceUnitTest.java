package com.roos.adoptioncenter.apigateway.businesslayer.adopter;


import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
import org.mockito.junit.jupiter.MockitoExtension;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.adopter.AdopterServiceClient;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.adoptionpaper.AdopterModel;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.adoptionpaper.AdoptionPaperServiceClient;
import com.roos.adoptioncenter.apigateway.presentationlayer.adopter.AdopterRequestModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.adopter.AdopterResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdopterServiceUnitTest {

    @Mock
    private AdoptionPaperServiceClient adoptionPaperServiceClient;

    @Mock
    private AdopterServiceClient adopterServiceClient;

    @InjectMocks
    private AdopterServiceImpl adopterService;

    private AdopterModel adopterModel;
    private AdopterRequestModel requestModel;
    private AdopterResponseModel responseModel;

    @BeforeEach
    void setUp() {
        String adopterId = UUID.randomUUID().toString();

        adopterModel = AdopterModel.builder()
                .id(adopterId)
                .FName("John")
                .LName("Doe")
                .build();

        requestModel = AdopterRequestModel.builder()
                .fName("John")
                .lName("Doe")
                .build();

        responseModel = AdopterResponseModel.builder()
                .adopterId(adopterId)
                .fName("John")
                .lName("Doe")
                .build();
    }

    @Test
    void whenGetAdopterById_thenReturnAdopter() {
        when(adopterServiceClient.getAdopterById(adopterModel.getId())).thenReturn(responseModel);

        var result = adopterService.getAdopterById(adopterModel.getId());

        assertNotNull(result);
        assertEquals(responseModel.getAdopterId(), result.getAdopterId());
        assertEquals(responseModel.getFName(), result.getFName());
        assertEquals(responseModel.getLName(), result.getLName());
    }

    @Test
    void whenGetAllAdopters_thenReturnList() {
        when(adopterServiceClient.getAllAdopters()).thenReturn(List.of(responseModel));

        var result = adopterService.getAllAdopters();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(responseModel.getAdopterId(), result.get(0).getAdopterId());
    }

    @Test
    void whenGetAllAdoptersIsEmpty_thenReturnEmptyList() {
        when(adopterServiceClient.getAllAdopters()).thenReturn(Collections.emptyList());

        var result = adopterService.getAllAdopters();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void whenCreateAdopter_thenReturnCreatedAdopter() {
        when(adopterServiceClient.addAdopter(requestModel)).thenReturn(responseModel);

        var result = adopterService.createAdopter(requestModel);

        assertNotNull(result);
        assertEquals(responseModel.getAdopterId(), result.getAdopterId());
        assertEquals(responseModel.getFName(), result.getFName());
    }

    @Test
    void whenUpdateAdopter_thenReturnUpdatedAdopter() {
        when(adopterServiceClient.updateAdopter(requestModel, adopterModel.getId())).thenReturn(responseModel);

        var result = adopterService.updateAdopter(requestModel, adopterModel.getId());

        assertNotNull(result);
        assertEquals(responseModel.getAdopterId(), result.getAdopterId());
    }

    @Test
    void whenDeleteAdopter_thenVerifyClientCall() {
        doNothing().when(adopterServiceClient).deleteAdopter(adopterModel.getId());

        adopterService.deleteAdopter(adopterModel.getId());

        verify(adopterServiceClient, times(1)).deleteAdopter(adopterModel.getId());
    }

    @Test
    void whenGetAdopterByIdNotFound_thenThrowNotFoundException() {
        when(adopterServiceClient.getAdopterById(anyString())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            adopterService.getAdopterById("nonexistent-id");
        });
    }


    @Test
    void whenAdopterServiceClientThrowsException_thenPropagate() {
        when(adopterServiceClient.getAdopterById(anyString())).thenThrow(new RuntimeException("Service failure"));

        assertThrows(RuntimeException.class, () -> {
            adopterService.getAdopterById("some-id");
        });
    }
}
