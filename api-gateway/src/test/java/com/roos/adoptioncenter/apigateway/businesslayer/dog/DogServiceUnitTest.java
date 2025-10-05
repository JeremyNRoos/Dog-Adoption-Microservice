package com.roos.adoptioncenter.apigateway.businesslayer.dog;

import com.roos.adoptioncenter.apigateway.DomainClientLayer.dog.DogBreedEnum;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.dog.Kennel;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.dog.KennelSizeEnum;
import com.roos.adoptioncenter.apigateway.ExceptionsHandling.NotFoundException;
import com.roos.adoptioncenter.apigateway.DomainClientLayer.dog.DogServiceClient;
import com.roos.adoptioncenter.apigateway.presentationlayer.dog.DogRequestModel;
import com.roos.adoptioncenter.apigateway.presentationlayer.dog.DogResponseModel;
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
class DogServiceUnitTest {

    @Mock
    private DogServiceClient dogServiceClient;

    @InjectMocks
    private DogServiceImpl dogService;

    private DogRequestModel requestModel;
    private DogResponseModel responseModel;
    private String adopterId;
    private String dogId;

    @BeforeEach
    void setUp() {
        adopterId = UUID.randomUUID().toString();
        dogId = UUID.randomUUID().toString();

        requestModel = DogRequestModel.builder()
                .name("Buddy")
                .breed(DogBreedEnum.AFGHAN_HOUND)
                .build();

        responseModel = DogResponseModel.builder()
                .dogId(dogId)
                .name("Buddy")
                .breed(DogBreedEnum.AFGHAN_HOUND)
                .dogKennel(Kennel.builder().kennelSize(KennelSizeEnum.MEDIUM).build())
                .build();


    }

    @Test
    void whenGetDogById_thenReturnDog() {
        when(dogServiceClient.getDogById(adopterId, dogId)).thenReturn(responseModel);

        var result = dogService.getDogById(adopterId, dogId);

        assertNotNull(result);
        assertEquals(responseModel.getDogId(), result.getDogId());
        assertEquals(responseModel.getName(), result.getName());
        assertEquals(responseModel.getDogKennel().getKennelSize(), result.getDogKennel().getKennelSize());
    }

    @Test
    void whenGetAllDogs_thenReturnList() {
        when(dogServiceClient.getAllDogs(adopterId)).thenReturn(List.of(responseModel));

        var result = dogService.getAllDogs(adopterId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(responseModel.getDogId(), result.get(0).getDogId());
    }

    @Test
    void whenGetAllDogsIsEmpty_thenReturnEmptyList() {
        when(dogServiceClient.getAllDogs(adopterId)).thenReturn(Collections.emptyList());

        var result = dogService.getAllDogs(adopterId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void whenCreateDog_thenReturnCreatedDog() {
        when(dogServiceClient.addDog(adopterId, requestModel)).thenReturn(responseModel);

        var result = dogService.addDog(adopterId, requestModel);

        assertNotNull(result);
        assertEquals(responseModel.getDogId(), result.getDogId());
        assertEquals(responseModel.getName(), result.getName());
    }

    @Test
    void whenUpdateDog_thenReturnUpdatedDog() {
        when(dogServiceClient.updateDog(adopterId, requestModel, dogId)).thenReturn(responseModel);

        var result = dogService.updateDog(adopterId, requestModel, dogId);

        assertNotNull(result);
        assertEquals(responseModel.getDogId(), result.getDogId());
    }

    @Test
    void whenDeleteDog_thenVerifyClientCall() {
        doNothing().when(dogServiceClient).deleteDog(adopterId, dogId);

        dogService.deleteDog(adopterId, dogId);

        verify(dogServiceClient, times(1)).deleteDog(adopterId, dogId);
    }

    @Test
    void whenGetDogByIdNotFound_thenThrowNotFoundException() {
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            dogService.getDogById("nonexistent-adopter-id", "nonexistent-dog-id");
        });
    }

    @Test
    void whenDogServiceClientThrowsException_thenPropagate() {
        when(dogServiceClient.getDogById(anyString(), anyString()))
                .thenThrow(new RuntimeException("Service failure"));

        assertThrows(RuntimeException.class, () -> {
            dogService.getDogById("some-id", "some-dog-id");
        });
    }
}
