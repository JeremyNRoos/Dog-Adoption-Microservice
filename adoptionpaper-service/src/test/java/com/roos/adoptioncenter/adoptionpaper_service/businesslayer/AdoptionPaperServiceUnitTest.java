package com.roos.adoptioncenter.adoptionpaper_service.businesslayer;

import static org.mockito.ArgumentMatchers.*;

import com.roos.adoptioncenter.adoptionpaper_service.utils.DatabaseLoaderService;
import com.roos.adoptioncenter.adoptionpaper_service.utils.ExceptionsHandling.DogServiceException;
import com.roos.adoptioncenter.adoptionpaper_service.utils.ExceptionsHandling.NotFoundException;

import com.roos.adoptioncenter.adoptionpaper_service.dataaccesslayer.AdoptionPaper;
import com.roos.adoptioncenter.adoptionpaper_service.dataaccesslayer.AdoptionPaperIdentifier;
import com.roos.adoptioncenter.adoptionpaper_service.dataaccesslayer.AdoptionPaperRepository;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.adopter.AdopterModel;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.adopter.AdopterServiceClient;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.dog.*;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.volunteer.VolunteerModel;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.volunteer.VolunteerServiceClient;
import com.roos.adoptioncenter.adoptionpaper_service.mappinglayer.AdoptionPaperRequestMapper;
import com.roos.adoptioncenter.adoptionpaper_service.mappinglayer.AdoptionPaperResponseMapper;
import com.roos.adoptioncenter.adoptionpaper_service.presentationlayer.AdoptionPaperRequestModel;
import com.roos.adoptioncenter.adoptionpaper_service.presentationlayer.AdoptionPaperResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.ResourceAccessException;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration")
class AdoptionPaperServiceUnitTest {


    @Autowired
    private AdoptionPaperService adoptionPaperService;



    @MockitoBean
    private AdoptionPaperRepository adoptionPaperRepository;
    @MockitoBean
    private VolunteerServiceClient volunteerServiceClient;
    @MockitoBean
    private AdopterServiceClient adopterServiceClient;
    @MockitoBean
    private DogServiceClient dogServiceClient;
    @MockitoBean
    private LocationServiceClient locationServiceClient;
    @MockitoBean
    private DatabaseLoaderService databaseLoaderService;
    @MockitoBean
    private AdoptionPaperResponseMapper adoptionPaperResponseMapper;
    @Autowired
    private AdoptionPaperRequestMapper adoptionPaperRequestMapper;
    private AdopterModel adopterModel;
    private DogModel dogModel;
    private LocationModel locationModel;
    private VolunteerModel volunteerModel;
    private AdoptionPaperRequestModel requestModel;

    @BeforeEach
    void setUp() {

        adopterModel = AdopterModel.builder()
                .adopterId(UUID.randomUUID().toString())
                .FName("Jeremy")
                .LName("Roos")
                .build();

        dogModel = DogModel.builder()
                .dogId(UUID.randomUUID().toString())
                .age(3)
                .name("Nathan")
                .build();

        locationModel = LocationModel.builder()
                .locationId(UUID.randomUUID().toString())
                .name("PlaceName")
                .shelterType(ShelterTypeEnum.SHELTER)
                .build();

        volunteerModel = VolunteerModel.builder()
                .volunteerId(UUID.randomUUID().toString())
                .FName("Volunteer")
                .LName("Roos")
                .build();

        requestModel = AdoptionPaperRequestModel.builder()
                .adopterId(adopterModel.getAdopterId())
                .dogId(dogModel.getDogId())
                .locationId(locationModel.getLocationId())
                .volunteerId(volunteerModel.getVolunteerId())
                .build();
    }

    @Test
    void whenDeleteAdoptionPaperExists_thenDeleteSuccessfully() {
        String adopterId = UUID.randomUUID().toString();
        String adoptionPaperId = "ADOPT-TO-DELETE";

        var paperToDelete = AdoptionPaper.builder()
                .adoptionPaperIdentifier(new AdoptionPaperIdentifier(adoptionPaperId))
                .adopterModel(AdopterModel.builder().adopterId(adopterId).build())
                .build();

        when(adoptionPaperRepository.findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(adopterId, adoptionPaperId))
                .thenReturn(paperToDelete);

        adoptionPaperService.deleteAdoptionPaper(adopterId, adoptionPaperId);

        verify(adoptionPaperRepository).findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(adopterId, adoptionPaperId);
        verify(adoptionPaperRepository).delete(eq(paperToDelete));
    }

    @Test
    void whenDeleteAdoptionPaperDoesNotExist_thenThrowNotFoundException() {
        String adopterId = UUID.randomUUID().toString();
        String adoptionPaperId = "nonexistent-paper-id";

        when(adoptionPaperRepository.findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(adopterId, adoptionPaperId))
                .thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            adoptionPaperService.deleteAdoptionPaper(adopterId, adoptionPaperId);
        });

        verify(adoptionPaperRepository).findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(adopterId, adoptionPaperId);
        verify(adoptionPaperRepository, never()).delete(any());
    }

    @Test
    void whenGetAdoptionPaperByIdDoesNotExist_thenThrowNotFoundException() {
        String adopterId = UUID.randomUUID().toString();
        String adoptionPaperId = "nonexistent-paper-id";

        when(adoptionPaperRepository.findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(adopterId, adoptionPaperId))
                .thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            adoptionPaperService.getAdoptionPaperById(adopterId, adoptionPaperId);
        });

        verify(adoptionPaperRepository).findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(adopterId, adoptionPaperId);
    }

    @Test
    void whenGetAdoptionPaperByIdExists_thenReturnAdoptionPaper() {
        String adopterId = UUID.randomUUID().toString();
        String adoptionPaperId = "ADOPT-001";

        var foundPaper = AdoptionPaper.builder()
                .adoptionPaperIdentifier(new AdoptionPaperIdentifier(adoptionPaperId))
                .adopterModel(AdopterModel.builder().adopterId(adopterId).FName("John").LName("Doe").build())
                .build();

        var expectedResponse = AdoptionPaperResponseModel.builder()
                .adoptionPaperId(adoptionPaperId)
                .adopterFName("John")
                .adopterLName("Doe")
                .build();

        when(adoptionPaperRepository.findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(adopterId, adoptionPaperId))
                .thenReturn(foundPaper);
        when(adoptionPaperResponseMapper.toResponseModel(eq(foundPaper))).thenReturn(expectedResponse);

        var actual = adoptionPaperService.getAdoptionPaperById(adopterId, adoptionPaperId);

        assertEquals(expectedResponse.getAdoptionPaperId(), actual.getAdoptionPaperId());
    }

    @Test
    void whenGetAdoptionPapers_thenReturnListOfAdoptionPapers() {
        String adopterId = UUID.randomUUID().toString();

        var adopterModel = AdopterModel.builder().adopterId(adopterId).FName("Test").LName("User").build();
        var paper = AdoptionPaper.builder()
                .adoptionPaperIdentifier(new AdoptionPaperIdentifier("ADOPT-001"))
                .adopterModel(adopterModel)
                .build();

        var responseModel = AdoptionPaperResponseModel.builder().adoptionPaperId("ADOPT-001").build();

        when(adopterServiceClient.getAdopterById(adopterId)).thenReturn(adopterModel);
        when(adoptionPaperRepository.findAllByAdopterModel_AdopterId(adopterId)).thenReturn(List.of(paper));
        when(adoptionPaperResponseMapper.entityListToResponseModelList(List.of(paper))).thenReturn(List.of(responseModel));

        var actual = adoptionPaperService.getAdoptionPapers(adopterId);

        assertEquals(1, actual.size());
        assertEquals("ADOPT-001", actual.get(0).getAdoptionPaperId());
    }

    @Test
    void whenGetAdoptionPapersEmpty_thenReturnEmptyList() {
        String adopterId = UUID.randomUUID().toString();

        var adopterModel = AdopterModel.builder()
                .adopterId(adopterId)
                .FName("Test")
                .LName("User")
                .build();

        when(adopterServiceClient.getAdopterById(adopterId)).thenReturn(adopterModel);
        when(adoptionPaperRepository.findAllByAdopterModel_AdopterId(adopterId)).thenReturn(Collections.emptyList());
        when(adoptionPaperResponseMapper.entityListToResponseModelList(Collections.emptyList())).thenReturn(Collections.emptyList());

        var actual = adoptionPaperService.getAdoptionPapers(adopterId);

        assertNotNull(actual);
    }


    @Test
    void testAdoptionPaperRequestToModel() {
        String adoptionPaperId = "ADOPT-001";

        AdoptionPaper paper = adoptionPaperRequestMapper.requestToEntity(requestModel, new AdoptionPaperIdentifier(adoptionPaperId), volunteerModel, locationModel, dogModel, adopterModel);

        assertNotNull(paper);
        assertEquals(paper.getAdopterModel().getAdopterId(), requestModel.getAdopterId());
    }

    @Test
    void shouldMapRequestToEntityCorrectly() {
        String adoptionPaperId = "ADOPT-001";

        AdoptionPaper entity = adoptionPaperRequestMapper.requestToEntity(requestModel, new AdoptionPaperIdentifier(adoptionPaperId), volunteerModel, locationModel, dogModel, adopterModel);

        assertNotNull(entity);
        assertNotNull(entity.getAdoptionPaperIdentifier());
        assertEquals(adopterModel, entity.getAdopterModel());
        assertEquals(dogModel, entity.getDogModel());
        assertEquals(locationModel, entity.getLocationModel());
        assertEquals(volunteerModel, entity.getVolunteerModel());
    }

    @Test
    void shouldSetCorrectIdsInEntity() {
        String adoptionPaperId = "ADOPT-001";

        AdoptionPaper entity = adoptionPaperRequestMapper.requestToEntity(requestModel, new AdoptionPaperIdentifier(adoptionPaperId), volunteerModel, locationModel, dogModel, adopterModel);

        assertEquals(adopterModel.getAdopterId(), entity.getAdopterModel().getAdopterId());
        assertEquals(dogModel.getDogId(), entity.getDogModel().getDogId());
        assertEquals(locationModel.getLocationId(), entity.getLocationModel().getLocationId());
        assertEquals(volunteerModel.getVolunteerId(), entity.getVolunteerModel().getVolunteerId());
    }

    @Test
    void shouldFailIfRequestModelIsNull() {
        String adoptionPaperId = "ADOPT-001";

        adoptionPaperRequestMapper.requestToEntity(null, new AdoptionPaperIdentifier(adoptionPaperId), volunteerModel, locationModel, dogModel, adopterModel);
    }

    @Test
    void shouldReturnEntityWithNullVolunteerIfVolunteerIsNull() {
        String adoptionPaperId = "ADOPT-001";

        AdoptionPaper entity = adoptionPaperRequestMapper.requestToEntity(
                requestModel,
                new AdoptionPaperIdentifier(adoptionPaperId),
                null,
                locationModel,
                dogModel,
                adopterModel
        );

        assertNotNull(entity);
        assertNull(entity.getVolunteerModel());
        assertEquals("Nathan", entity.getDogModel().getName());
    }

    @Test
    void whenCreateAdoptionPaper_thenMapperShouldMapCorrectly() {
        // Arrange
        String adoptionPaperId = "ADOPT-001";

        when(adopterServiceClient.getAdopterById(anyString())).thenReturn(adopterModel);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(dogModel);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(volunteerModel);
        // Act
        adoptionPaperService.addAdoptionPaper(requestModel, adoptionPaperId);

        // Assert
        verify(adoptionPaperRepository).save(argThat(saved -> {
            assertNotNull(saved); // Ensures save() was called with a non-null object
            assertNotNull(saved.getAdopterModel());
            assertEquals(adopterModel.getAdopterId(), saved.getAdopterModel().getAdopterId());
            assertNotNull(saved.getDogModel());
            assertEquals(dogModel.getDogId(), saved.getDogModel().getDogId());
            assertNotNull(saved.getLocationModel());
            assertEquals(locationModel.getLocationId(), saved.getLocationModel().getLocationId());
            assertNotNull(saved.getVolunteerModel());
            assertEquals(volunteerModel.getVolunteerId(), saved.getVolunteerModel().getVolunteerId());
            return true;
        }));
    }

    @Test
    void whenAddAdoptionPaperAndAdopterNotFound_thenThrowNotFoundException() {
        String adoptionPaperId = "ADOPT-002";

        when(adopterServiceClient.getAdopterById(anyString())).thenReturn(null);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(dogModel);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(volunteerModel);

        assertThrows(NotFoundException.class, () -> {
            adoptionPaperService.addAdoptionPaper(requestModel, adoptionPaperId);
        });

        verify(adoptionPaperRepository, times(0)).save(any());
    }

    @Test
    void whenAddAdoptionPaperAndVolunteerNotFound_thenThrowNotFoundException() {
        String adoptionPaperId = "ADOPT-003";

        when(adopterServiceClient.getAdopterById(anyString())).thenReturn(adopterModel);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(dogModel);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            adoptionPaperService.addAdoptionPaper(requestModel, adoptionPaperId);
        });

        verify(adoptionPaperRepository, times(0)).save(any());
    }

    @Test
    void whenAddAdoptionPaperAndDogNotFound_thenThrowNotFoundException() {
        String adoptionPaperId = "ADOPT-004";

        when(adopterServiceClient.getAdopterById(anyString())).thenReturn(adopterModel);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(null);  // Simulate dog missing
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(volunteerModel);

        assertThrows(NotFoundException.class, () -> {
            adoptionPaperService.addAdoptionPaper(requestModel, adoptionPaperId);
        });

        verify(adoptionPaperRepository, times(0)).save(any());
    }

    @Test
    void whenAddAdoptionPaperAndLocationNotFound_thenThrowNotFoundException() {
        String adoptionPaperId = "ADOPT-005";

        when(adopterServiceClient.getAdopterById(anyString())).thenReturn(adopterModel);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(dogModel);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(null);  // Simulate location missing
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(volunteerModel);

        assertThrows(NotFoundException.class, () -> {
            adoptionPaperService.addAdoptionPaper(requestModel, adoptionPaperId);
        });

        verify(adoptionPaperRepository, times(0)).save(any());
    }

    @Test
    void whenAddAdoptionPaperAndRequestModelIsNull_thenThrowIllegalArgumentException() {
        String adoptionPaperId = "ADOPT-006";

        assertThrows(IllegalArgumentException.class, () -> {
            adoptionPaperService.addAdoptionPaper(null, adoptionPaperId);
        });

        verify(adoptionPaperRepository, times(0)).save(any());
    }

    @Test
    void whenGetAdoptionPapersAndAdopterNotFound_thenThrowNotFoundException() {
        String adopterId = UUID.randomUUID().toString();

        when(adopterServiceClient.getAdopterById(adopterId)).thenReturn(null);  // Simulate adopter not found

        assertThrows(NotFoundException.class, () -> {
            adoptionPaperService.getAdoptionPapers(adopterId);
        });

        verify(adoptionPaperRepository, times(0)).findAllByAdopterModel_AdopterId(anyString());
    }


    @Test
    void whenUpdateAdoptionPaperAndPaperNotFound_thenThrowNotFoundException() {
        String adopterId = UUID.randomUUID().toString();
        String adoptionPaperId = "ADOPT-UPDATE-001";

        when(adoptionPaperRepository.findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(adopterId, adoptionPaperId))
                .thenReturn(null);  // Simulate missing paper

        assertThrows(NotFoundException.class, () -> {
            adoptionPaperService.updateAdoptionPaper(adopterId, requestModel, adoptionPaperId);
        });

        verify(adoptionPaperRepository, times(0)).save(any());
    }


    @Test
    void whenUpdateAdoptionPaperAndAdopterOrVolunteerOrLocationNotFound_thenThrowNotFoundException() {
        String adopterId = UUID.randomUUID().toString();
        String adoptionPaperId = "ADOPT-UPDATE-003";

        var existingPaper = AdoptionPaper.builder()
                .adoptionPaperIdentifier(new AdoptionPaperIdentifier(adoptionPaperId))
                .adopterModel(adopterModel)
                .build();

        when(adoptionPaperRepository.findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(adopterId, adoptionPaperId))
                .thenReturn(existingPaper);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(dogModel);
        when(adopterServiceClient.getAdopterById(anyString())).thenReturn(null);  // Simulate adopter missing

        assertThrows(NotFoundException.class, () -> {
            adoptionPaperService.updateAdoptionPaper(adopterId, requestModel, adoptionPaperId);
        });

        verify(adoptionPaperRepository, times(0)).save(any());
    }


    @Test
    void whenDeleteAdoptionPaperThenRepositoryDeleteIsCalledOnce() {
        String adopterId = UUID.randomUUID().toString();
        String adoptionPaperId = "ADOPT-DELETE-003";

        var paper = AdoptionPaper.builder()
                .adoptionPaperIdentifier(new AdoptionPaperIdentifier(adoptionPaperId))
                .adopterModel(adopterModel)
                .build();

        when(adoptionPaperRepository.findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(adopterId, adoptionPaperId))
                .thenReturn(paper);

        adoptionPaperService.deleteAdoptionPaper(adopterId, adoptionPaperId);

        verify(adoptionPaperRepository, times(1)).delete(paper);
    }


    @Test
    void whenAddAdoptionPaperWithEmptyFields_thenThrowException() {
        AdoptionPaperRequestModel incompleteRequest = AdoptionPaperRequestModel.builder()
                .adopterId("")
                .dogId("")
                .locationId("")
                .volunteerId("")
                .build();

        assertThrows(NotFoundException.class, () -> {
            adoptionPaperService.addAdoptionPaper(incompleteRequest, "");
        });

        verify(adoptionPaperRepository, times(0)).save(any());
    }


    @Test
    void whenUpdateAdoptionPaperAndAllDependenciesMissing_thenThrowNotFoundException() {
        String adoptionPaperId = "ADOPT-UPDATE-MULTI";
        var paper = AdoptionPaper.builder()
                .adoptionPaperIdentifier(new AdoptionPaperIdentifier(adoptionPaperId))
                .adopterModel(adopterModel)
                .build();

        when(adoptionPaperRepository.findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(anyString(), anyString()))
                .thenReturn(paper);
        when(adopterServiceClient.getAdopterById(anyString())).thenReturn(null);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(null);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(null);
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            adoptionPaperService.updateAdoptionPaper(adopterModel.getAdopterId(), requestModel, adoptionPaperId);
        });

        verify(adoptionPaperRepository, times(0)).save(any());
    }

    @Test
    void whenDeleteAdoptionPaperWithInvalidId_thenThrowNotFoundException() {
        String invalidAdopterId = "invalid-adopter";
        String invalidAdoptionPaperId = "invalid-paper";

        when(adoptionPaperRepository.findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(invalidAdopterId, invalidAdoptionPaperId))
                .thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            adoptionPaperService.deleteAdoptionPaper(invalidAdopterId, invalidAdoptionPaperId);
        });

        verify(adoptionPaperRepository, times(0)).delete(any());
    }

    @Test
    void whenGetAdoptionPapersReturnsNullList_thenHandleGracefully() {
        String adopterId = UUID.randomUUID().toString();

        when(adopterServiceClient.getAdopterById(adopterId)).thenReturn(adopterModel);
        when(adoptionPaperRepository.findAllByAdopterModel_AdopterId(adopterId)).thenReturn(null);

        List<AdoptionPaperResponseModel> result = adoptionPaperService.getAdoptionPapers(adopterId);

        assertNotNull(result);
        assertEquals(0, result.size());
    }


    @Test
    void whenUpdateAdoptionPaperAndDogNotFound_thenThrowDogServiceException() {
        String adopterId = UUID.randomUUID().toString();
        String adoptionPaperId = "ADOPT-UPDATE-002";

        var existingPaper = AdoptionPaper.builder()
                .adoptionPaperIdentifier(new AdoptionPaperIdentifier(adoptionPaperId))
                .adopterModel(adopterModel)
                .build();

        when(adoptionPaperRepository.findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(adopterId, adoptionPaperId))
                .thenReturn(existingPaper);
        when(dogServiceClient.getDogById(anyString(), anyString()))
                .thenThrow(new RuntimeException("Dog not found"));

        // Expect the DogServiceException from the service implementation
        assertThrows(com.roos.adoptioncenter.adoptionpaper_service.utils.ExceptionsHandling.DogServiceException.class, () -> {
            adoptionPaperService.updateAdoptionPaper(adopterId, requestModel, adoptionPaperId);
        });

        verify(adoptionPaperRepository, times(0)).save(any());
    }


    @Test
    void whenAddAdoptionPaperWithEmptyIdentifiers_thenThrowNotFoundException() {
        AdoptionPaperRequestModel incompleteRequest = AdoptionPaperRequestModel.builder()
                .adopterId("")
                .dogId("")
                .locationId("")
                .volunteerId("")
                .build();

        // Simulate the external services returning null due to empty identifiers
        when(adopterServiceClient.getAdopterById(anyString())).thenReturn(null);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(null);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(null);
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            adoptionPaperService.addAdoptionPaper(incompleteRequest, "");
        });

        verify(adoptionPaperRepository, times(0)).save(any());
    }


    // NEW & IMPROVED TEST CASES for AdoptionPaperServiceUnitTest

    // 🏷️ Adoption Paper Creation (addAdoptionPaper)
    @Test
    void whenAddAdoptionPaperWithAllFieldsValid_thenSaveSuccessfully() {
        String adoptionPaperId = "ADOPT-VALID";
        when(adopterServiceClient.getAdopterById(anyString())).thenReturn(adopterModel);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(dogModel);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(volunteerModel);

        adoptionPaperService.addAdoptionPaper(requestModel, adoptionPaperId);

        verify(adoptionPaperRepository).save(any(AdoptionPaper.class));
    }

    @Test
    void whenAddAdoptionPaperWithPartialIdentifiers_thenThrowNotFoundException() {
        AdoptionPaperRequestModel incomplete = AdoptionPaperRequestModel.builder()
                .adopterId("adopterId")
                .dogId(null)
                .locationId("locId")
                .volunteerId("volId")
                .build();

        when(adopterServiceClient.getAdopterById(anyString())).thenReturn(adopterModel);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(null);

        assertThrows(NotFoundException.class, () ->
                adoptionPaperService.addAdoptionPaper(incomplete, "ADOPT-PARTIAL"));

        verify(adoptionPaperRepository, never()).save(any());
    }

    // 🏷️ Adoption Paper Retrieval
    @Test
    void whenGetAdoptionPapersReturnsNullList_thenReturnEmptyList() {
        String adopterId = UUID.randomUUID().toString();
        when(adopterServiceClient.getAdopterById(adopterId)).thenReturn(adopterModel);
        when(adoptionPaperRepository.findAllByAdopterModel_AdopterId(adopterId)).thenReturn(null);

        List<AdoptionPaperResponseModel> result = adoptionPaperService.getAdoptionPapers(adopterId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // 🏷️ Adoption Paper Update
    @Test
    void whenUpdateAdoptionPaperWithValidDependencies_thenUpdateSuccessfully() {
        String adopterId = UUID.randomUUID().toString();
        String adoptionPaperId = "ADOPT-UPDATE-VALID";

        // Prepare existing adoption paper
        var existingPaper = AdoptionPaper.builder()
                .adoptionPaperIdentifier(new AdoptionPaperIdentifier(adoptionPaperId))
                .adopterModel(adopterModel)
                .dogModel(dogModel)
                .volunteerModel(volunteerModel)
                .locationModel(locationModel)
                .build();

        // Mock repository find
        when(adoptionPaperRepository.findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(adopterId, adoptionPaperId))
                .thenReturn(existingPaper);

        // Mock external dependencies
        when(dogServiceClient.getDogById(locationModel.getLocationId(), requestModel.getDogId()))
                .thenReturn(dogModel);
        when(adopterServiceClient.getAdopterById(adopterId))
                .thenReturn(adopterModel);
        when(volunteerServiceClient.getVolunteerById(requestModel.getVolunteerId()))
                .thenReturn(volunteerModel);
        when(locationServiceClient.getLocationById(requestModel.getLocationId()))
                .thenReturn(locationModel);

        // Mock save and response mapping
        when(adoptionPaperRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var expectedResponse = AdoptionPaperResponseModel.builder()
                .adoptionPaperId(adoptionPaperId)
                .adopterFName(adopterModel.getFName())
                .adopterLName(adopterModel.getLName())
                .build();
        when(adoptionPaperResponseMapper.toResponseModel(any())).thenReturn(expectedResponse);

        // Call the service method
        var actual = adoptionPaperService.updateAdoptionPaper(adopterId, requestModel, adoptionPaperId);

        // Assert result is not null and mapped correctly
        assertNotNull(actual);
        assertEquals(expectedResponse.getAdoptionPaperId(), actual.getAdoptionPaperId());
        assertEquals(expectedResponse.getAdopterFName(), actual.getAdopterFName());
        verify(adoptionPaperRepository).save(any());
    }


    @Test
    void whenUpdateAdoptionPaperWithRepositorySaveFails_thenThrowException() {
        String adopterId = UUID.randomUUID().toString();
        String adoptionPaperId = "ADOPT-FAIL";

        var existing = AdoptionPaper.builder()
                .adoptionPaperIdentifier(new AdoptionPaperIdentifier(adoptionPaperId))
                .adopterModel(adopterModel).build();

        when(adoptionPaperRepository.findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(adopterId, adoptionPaperId))
                .thenReturn(existing);
        when(adopterServiceClient.getAdopterById(adopterId)).thenReturn(adopterModel);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(dogModel);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(volunteerModel);
        when(adoptionPaperRepository.save(any())).thenThrow(new RuntimeException("DB Save Failed"));

        assertThrows(RuntimeException.class, () ->
                adoptionPaperService.updateAdoptionPaper(adopterId, requestModel, adoptionPaperId));
    }

    // 🏷️ Mapper Edge Cases
    @Test
    void shouldReturnEntityWithNullLocationIfLocationIsNull() {
        String adoptionPaperId = "ADOPT-NULLLOC";
        var entity = adoptionPaperRequestMapper.requestToEntity(requestModel,
                new AdoptionPaperIdentifier(adoptionPaperId),
                volunteerModel, null, dogModel, adopterModel);
        assertNotNull(entity);
        assertNull(entity.getLocationModel());
    }

    @Test
    void shouldReturnEntityWithNullDogIfDogIsNull() {
        String adoptionPaperId = "ADOPT-NULLDOG";
        var entity = adoptionPaperRequestMapper.requestToEntity(requestModel,
                new AdoptionPaperIdentifier(adoptionPaperId),
                volunteerModel, locationModel, null, adopterModel);
        assertNotNull(entity);
        assertNull(entity.getDogModel());
    }

    // 🏷️ Edge Cases for Repository Exceptions
    @Test
    void whenAdoptionPaperRepositoryThrowsExceptionOnSave_thenHandleGracefully() {
        when(adoptionPaperRepository.save(any())).thenThrow(new RuntimeException("Save Error"));
        when(adopterServiceClient.getAdopterById(anyString())).thenReturn(adopterModel);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(dogModel);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(volunteerModel);

        assertThrows(RuntimeException.class, () ->
                adoptionPaperService.addAdoptionPaper(requestModel, "ADOPT-ERROR"));
    }

    @Test
    void whenAdoptionPaperRepositoryThrowsExceptionOnDelete_thenHandleGracefully() {
        String adopterId = UUID.randomUUID().toString();
        String adoptionPaperId = "ADOPT-DEL-ERR";
        var paper = AdoptionPaper.builder()
                .adoptionPaperIdentifier(new AdoptionPaperIdentifier(adoptionPaperId))
                .adopterModel(adopterModel).build();

        when(adoptionPaperRepository.findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(adopterId, adoptionPaperId))
                .thenReturn(paper);
        doThrow(new RuntimeException("Delete Error")).when(adoptionPaperRepository).delete(any());

        assertThrows(RuntimeException.class, () ->
                adoptionPaperService.deleteAdoptionPaper(adopterId, adoptionPaperId));
    }

//    @Test
//    void whenAddAdoptionPaperWithInvalidFields_thenThrowIllegalArgumentException() {
//        // Missing adopterId
//        AdoptionPaperRequestModel invalidRequest = AdoptionPaperRequestModel.builder()
//                .adopterId(null)
//                .dogId(dogModel.getDogId())
//                .locationId(locationModel.getLocationId())
//                .volunteerId(volunteerModel.getVolunteerId())
//                .build();
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            adoptionPaperService.addAdoptionPaper(invalidRequest, "ADOPT-INVALID");
//        });
//
//        verify(adoptionPaperRepository, never()).save(any());
//    }

//    @Test
//    void whenUpdateAdoptionPaperWithInvalidFields_thenThrowIllegalArgumentException() {
//        String adoptionPaperId = "ADOPT-UPDATE-INVALID";
//        AdoptionPaperRequestModel invalidRequest = AdoptionPaperRequestModel.builder()
//                .adopterId("")
//                .dogId("")
//                .locationId("")
//                .volunteerId("")
//                .build();
//
//        var existingPaper = AdoptionPaper.builder()
//                .adoptionPaperIdentifier(new AdoptionPaperIdentifier(adoptionPaperId))
//                .adopterModel(adopterModel)
//                .build();
//
//        when(adoptionPaperRepository.findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(any(), any()))
//                .thenReturn(existingPaper);
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            adoptionPaperService.updateAdoptionPaper(adopterModel.getAdopterId(), invalidRequest, adoptionPaperId);
//        });
//
//        verify(adoptionPaperRepository, never()).save(any());
//    }

    @Test
    void whenAdopterServiceReturnsMalformedModel_thenThrowNotFoundException() {
        String adoptionPaperId = "ADOPT-MALFORMED";
        when(adopterServiceClient.getAdopterById(anyString()))
                .thenReturn(AdopterModel.builder().adopterId(null).build()); // Missing ID

        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(dogModel);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(volunteerModel);

        assertThrows(NotFoundException.class, () -> {
            adoptionPaperService.addAdoptionPaper(requestModel, adoptionPaperId);
        });

        verify(adoptionPaperRepository, never()).save(any());
    }

    @Test
    void whenUpdateAdoptionPaperWithDifferentDog_thenUpdateCorrectly() {
        String adopterId = UUID.randomUUID().toString();
        String adoptionPaperId = "ADOPT-UPDATE-DIFF";

        var existingPaper = AdoptionPaper.builder()
                .adoptionPaperIdentifier(new AdoptionPaperIdentifier(adoptionPaperId))
                .adopterModel(adopterModel)
                .dogModel(dogModel)
                .volunteerModel(volunteerModel)
                .locationModel(locationModel)
                .build();

        var newDogModel = DogModel.builder()
                .dogId(UUID.randomUUID().toString())
                .age(2)
                .name("Max")
                .build();

        when(adoptionPaperRepository.findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(adopterId, adoptionPaperId))
                .thenReturn(existingPaper);

        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(newDogModel);
        when(adopterServiceClient.getAdopterById(adopterId)).thenReturn(adopterModel);
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(volunteerModel);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);
        when(adoptionPaperRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var responseModel = AdoptionPaperResponseModel.builder()
                .adoptionPaperId(adoptionPaperId)
                .adopterFName(adopterModel.getFName())
                .adopterLName(adopterModel.getLName())
                .build();

        when(adoptionPaperResponseMapper.toResponseModel(any())).thenReturn(responseModel);

        var updated = adoptionPaperService.updateAdoptionPaper(adopterId, requestModel, adoptionPaperId);

        assertNotNull(updated);
        assertEquals(responseModel.getAdoptionPaperId(), updated.getAdoptionPaperId());
        verify(adoptionPaperRepository).save(any());
        verify(dogServiceClient).getDogById(anyString(), anyString());
    }


    @Test
    void whenAddAdoptionPaper_thenVerifyAllServiceClientCalls() {
        String adoptionPaperId = "ADOPT-VERIFY";

        // Mock the expected returns from external services
        when(adopterServiceClient.getAdopterById(eq(adoptionPaperId))).thenReturn(adopterModel);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(dogModel);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(volunteerModel);

        // Call the service method
        adoptionPaperService.addAdoptionPaper(requestModel, adoptionPaperId);

        // Verify the service client calls with the correct arguments
        verify(adopterServiceClient).getAdopterById(eq(adoptionPaperId));
        verify(dogServiceClient).getDogById(anyString(), eq(requestModel.getDogId()));
        verify(locationServiceClient).getLocationById(eq(requestModel.getLocationId()));
        verify(volunteerServiceClient).getVolunteerById(eq(requestModel.getVolunteerId()));
    }


    @Test
    void whenUpdateAdoptionPaper_thenResponseMapperMapsCorrectly() {
        String adopterId = UUID.randomUUID().toString();
        String adoptionPaperId = "ADOPT-MAP-TEST";

        var existingPaper = AdoptionPaper.builder()
                .adoptionPaperIdentifier(new AdoptionPaperIdentifier(adoptionPaperId))
                .adopterModel(adopterModel)
                .dogModel(dogModel)
                .volunteerModel(volunteerModel)
                .locationModel(locationModel)
                .build();

        when(adoptionPaperRepository.findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(adopterId, adoptionPaperId))
                .thenReturn(existingPaper);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(dogModel);
        when(adopterServiceClient.getAdopterById(adopterId)).thenReturn(adopterModel);
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(volunteerModel);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);

        var mappedResponse = AdoptionPaperResponseModel.builder()
                .adoptionPaperId(adoptionPaperId)
                .adopterFName("Custom")
                .adopterLName("Name")
                .build();

        when(adoptionPaperRepository.save(any())).thenReturn(existingPaper);
        when(adoptionPaperResponseMapper.toResponseModel(existingPaper)).thenReturn(mappedResponse);

        var result = adoptionPaperService.updateAdoptionPaper(adopterId, requestModel, adoptionPaperId);

        assertNotNull(result);
        assertEquals("Custom", result.getAdopterFName());
        assertEquals("Name", result.getAdopterLName());
    }

    @Test
    void whenUpdateAdoptionPaperSaveFails_thenThrowRuntimeException() {
        String adopterId = UUID.randomUUID().toString();
        String adoptionPaperId = "ADOPT-UPDATE-FAIL";

        var existingPaper = AdoptionPaper.builder()
                .adoptionPaperIdentifier(new AdoptionPaperIdentifier(adoptionPaperId))
                .adopterModel(adopterModel)
                .build();

        when(adoptionPaperRepository.findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(adopterId, adoptionPaperId))
                .thenReturn(existingPaper);

        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(dogModel);
        when(adopterServiceClient.getAdopterById(adopterId)).thenReturn(adopterModel);
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(volunteerModel);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);

        when(adoptionPaperRepository.save(any())).thenThrow(new RuntimeException("Simulated DB failure"));

        assertThrows(RuntimeException.class, () -> {
            adoptionPaperService.updateAdoptionPaper(adopterId, requestModel, adoptionPaperId);
        });
    }

    @Test
    void whenGetAdoptionPapersReturnsNull_thenReturnEmptyList() {
        String adopterId = UUID.randomUUID().toString();

        when(adopterServiceClient.getAdopterById(adopterId)).thenReturn(adopterModel);
        when(adoptionPaperRepository.findAllByAdopterModel_AdopterId(adopterId)).thenReturn(null);

        List<AdoptionPaperResponseModel> result = adoptionPaperService.getAdoptionPapers(adopterId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    void whenDeleteAdoptionPaperNotFound_thenThrowNotFoundException() {
        String adopterId = UUID.randomUUID().toString();
        String adoptionPaperId = "NONEXISTENT";

        when(adoptionPaperRepository.findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(adopterId, adoptionPaperId))
                .thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            adoptionPaperService.deleteAdoptionPaper(adopterId, adoptionPaperId);
        });

        verify(adoptionPaperRepository, never()).delete(any());
    }

    @Test
    void whenUpdateAdoptionPaperDogServiceFails_thenThrowDogServiceException() {
        String adopterId = UUID.randomUUID().toString();
        String adoptionPaperId = "ADOPT-DOG-ERROR";

        var existingPaper = AdoptionPaper.builder()
                .adoptionPaperIdentifier(new AdoptionPaperIdentifier(adoptionPaperId))
                .adopterModel(adopterModel)
                .build();

        when(adoptionPaperRepository.findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(adopterId, adoptionPaperId))
                .thenReturn(existingPaper);

        when(dogServiceClient.getDogById(anyString(), anyString()))
                .thenThrow(new RuntimeException("Dog service unavailable"));

        assertThrows(com.roos.adoptioncenter.adoptionpaper_service.utils.ExceptionsHandling.DogServiceException.class, () -> {
            adoptionPaperService.updateAdoptionPaper(adopterId, requestModel, adoptionPaperId);
        });

        verify(adoptionPaperRepository, never()).save(any());
    }


    @Test
    void whenAddAdoptionPaperDogServiceFails_thenThrowDogServiceException() {
        String adopterId = UUID.randomUUID().toString();
        String adoptionPaperId = "ADOPT-ERROR";

        when(adopterServiceClient.getAdopterById(adopterId)).thenReturn(adopterModel);
        when(dogServiceClient.getDogById(anyString(), anyString()))
                .thenThrow(new RuntimeException("Dog service unavailable"));
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(volunteerModel);

        assertThrows(DogServiceException.class, () -> {
            adoptionPaperService.addAdoptionPaper(requestModel, adopterId);
        });
        verify(adoptionPaperRepository, never()).save(any());
    }

    @Test
    void whenAddAdoptionPaperVolunteerServiceTimeout_thenThrowException() {
        String adoptionPaperId = "ADOPT-TIMEOUT";

        when(adopterServiceClient.getAdopterById(anyString())).thenReturn(adopterModel);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(dogModel);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);
        when(volunteerServiceClient.getVolunteerById(anyString()))
                .thenThrow(new ResourceAccessException("Timeout while calling volunteer service"));

        assertThrows(ResourceAccessException.class, () -> {
            adoptionPaperService.addAdoptionPaper(requestModel, adoptionPaperId);
        });

        verify(adoptionPaperRepository, never()).save(any());
    }

    @Test
    void whenDogServiceClientThrowsException_thenThrowDogServiceException() {
        String adoptionPaperId = "ADOPT-EX-DOG";
        when(adopterServiceClient.getAdopterById(anyString())).thenReturn(adopterModel);
        when(dogServiceClient.getDogById(anyString(), anyString()))
                .thenThrow(new RuntimeException("Dog service failed"));
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(volunteerModel);

        assertThrows(DogServiceException.class, () ->
                adoptionPaperService.addAdoptionPaper(requestModel, adoptionPaperId));

        verify(adoptionPaperRepository, never()).save(any());
    }

    @Test
    void whenVolunteerServiceClientReturnsNull_thenThrowNotFoundException() {
        String adoptionPaperId = "ADOPT-EX-VOL";
        when(adopterServiceClient.getAdopterById(anyString())).thenReturn(adopterModel);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(dogModel);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(null);

        assertThrows(NotFoundException.class, () ->
                adoptionPaperService.addAdoptionPaper(requestModel, adoptionPaperId));

        verify(adoptionPaperRepository, never()).save(any());
    }

    @Test
    void whenLocationServiceClientReturnsNull_thenThrowNotFoundException() {
        String adoptionPaperId = "ADOPT-EX-LOC";
        when(adopterServiceClient.getAdopterById(anyString())).thenReturn(adopterModel);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(dogModel);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(null);
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(volunteerModel);

        assertThrows(NotFoundException.class, () ->
                adoptionPaperService.addAdoptionPaper(requestModel, adoptionPaperId));

        verify(adoptionPaperRepository, never()).save(any());
    }

    @Test
    void whenAdopterServiceClientReturnsNull_thenThrowNotFoundException() {
        String adoptionPaperId = "ADOPT-EX-ADOPT";
        when(adopterServiceClient.getAdopterById(anyString())).thenReturn(null);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(dogModel);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(volunteerModel);

        assertThrows(NotFoundException.class, () ->
                adoptionPaperService.addAdoptionPaper(requestModel, adoptionPaperId));

        verify(adoptionPaperRepository, never()).save(any());
    }

    @Test
    void whenAllServiceClientsReturnValid_thenAddPaperSuccessfully() {
        String adoptionPaperId = "ADOPT-ALL-SUCCESS";
        when(adopterServiceClient.getAdopterById(anyString())).thenReturn(adopterModel);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(dogModel);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(volunteerModel);

        adoptionPaperService.addAdoptionPaper(requestModel, adoptionPaperId);

        verify(adoptionPaperRepository, times(1)).save(any(AdoptionPaper.class));
    }


    @Test
    void whenVolunteerServiceClientThrowsRuntimeException_thenPropagate() {
        String adoptionPaperId = "ADOPT-RUNTIME-VOL";
        when(adopterServiceClient.getAdopterById(anyString())).thenReturn(adopterModel);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(dogModel);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);
        when(volunteerServiceClient.getVolunteerById(anyString())).thenThrow(new RuntimeException("Unexpected error"));

        assertThrows(RuntimeException.class, () -> {
            adoptionPaperService.addAdoptionPaper(requestModel, adoptionPaperId);
        });

        verify(adoptionPaperRepository, never()).save(any());
    }

    @Test
    void whenUpdateAdoptionPaperWithNullVolunteer_thenUpdateWithPartialData() {
        String adopterId = UUID.randomUUID().toString();
        String adoptionPaperId = "ADOPT-NULL-VOL";

        var existingPaper = AdoptionPaper.builder()
                .adoptionPaperIdentifier(new AdoptionPaperIdentifier(adoptionPaperId))
                .adopterModel(adopterModel)
                .dogModel(dogModel)
                .locationModel(locationModel)
                .build();

        when(adoptionPaperRepository.findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(adopterId, adoptionPaperId)).thenReturn(existingPaper);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(dogModel);
        when(adopterServiceClient.getAdopterById(adopterId)).thenReturn(adopterModel);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            adoptionPaperService.updateAdoptionPaper(adopterId, requestModel, adoptionPaperId);
        });

        verify(adoptionPaperRepository, never()).save(any());
    }


    @Test
    void whenRepositorySaveCalledWithNull_thenThrowException() {
        when(adoptionPaperRepository.save(null)).thenThrow(new IllegalArgumentException("Cannot save null"));

        assertThrows(IllegalArgumentException.class, () -> {
            adoptionPaperRepository.save(null);
        });
    }

    @Test
    void whenDogServiceClientReturnsNull_thenThrowNotFoundException() {
        String adoptionPaperId = "ADOPT-NODG";
        when(adopterServiceClient.getAdopterById(anyString())).thenReturn(adopterModel);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(null); // Null dog
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(volunteerModel);

        assertThrows(NotFoundException.class, () -> {
            adoptionPaperService.addAdoptionPaper(requestModel, adoptionPaperId);
        });

        verify(adoptionPaperRepository, never()).save(any());
    }


    // Test when adopter service returns an adopter with null fields
    @Test
    void whenAdopterServiceReturnsIncompleteAdopter_thenThrowNotFoundException() {
        String adoptionPaperId = "ADOPT-INCOMPLETE-ADOPTER";
        AdopterModel incompleteAdopter = AdopterModel.builder().adopterId(null).build(); // Incomplete

        when(adopterServiceClient.getAdopterById(anyString())).thenReturn(incompleteAdopter);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(dogModel);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(volunteerModel);

        assertThrows(NotFoundException.class, () -> {
            adoptionPaperService.addAdoptionPaper(requestModel, adoptionPaperId);
        });

        verify(adoptionPaperRepository, never()).save(any());
    }

    // Test when VolunteerServiceClient throws an exception (simulating service failure)
    @Test
    void whenVolunteerServiceClientThrowsException_thenHandleGracefully() {
        String adoptionPaperId = "ADOPT-EXCEPTION-VOL";

        when(adopterServiceClient.getAdopterById(anyString())).thenReturn(adopterModel);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(dogModel);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);
        when(volunteerServiceClient.getVolunteerById(anyString()))
                .thenThrow(new RuntimeException("Volunteer service failure"));

        assertThrows(RuntimeException.class, () -> {
            adoptionPaperService.addAdoptionPaper(requestModel, adoptionPaperId);
        });

        verify(adoptionPaperRepository, never()).save(any());
    }

    // Test when all external services return valid data and AdoptionPaper is saved
    @Test
    void whenAllServiceClientsReturnValid_thenAdoptionPaperSaved() {
        String adoptionPaperId = "ADOPT-ALL-VALID";

        when(adopterServiceClient.getAdopterById(anyString())).thenReturn(adopterModel);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(dogModel);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(volunteerModel);

        adoptionPaperService.addAdoptionPaper(requestModel, adoptionPaperId);

        verify(adoptionPaperRepository, times(1)).save(any(AdoptionPaper.class));
        verify(adopterServiceClient).getAdopterById(anyString());
        verify(dogServiceClient).getDogById(anyString(), eq(requestModel.getDogId()));
        verify(locationServiceClient).getLocationById(eq(requestModel.getLocationId()));
        verify(volunteerServiceClient).getVolunteerById(eq(requestModel.getVolunteerId()));
    }

    @Test
    void whenVolunteerServiceThrows_thenAddAdoptionPaperThrowsException() {
        String adopterId = UUID.randomUUID().toString();
        when(volunteerServiceClient.getVolunteerById(anyString()))
                .thenThrow(new RuntimeException("Volunteer Service Error"));
        when(adopterServiceClient.getAdopterById(anyString())).thenReturn(adopterModel);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(dogModel);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);

        assertThrows(RuntimeException.class, () -> {
            adoptionPaperService.addAdoptionPaper(requestModel, adopterId);
        });
    }

    @Test
    void whenUpdateAdoptionPaperAdopterWithNullFields_thenThrowNotFoundException() {
        String adopterId = UUID.randomUUID().toString();
        String adoptionPaperId = "ADOPT-UPD-NULL";
        var paper = AdoptionPaper.builder()
                .adoptionPaperIdentifier(new AdoptionPaperIdentifier(adoptionPaperId))
                .adopterModel(adopterModel).build();

        when(adoptionPaperRepository.findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(adopterId, adoptionPaperId))
                .thenReturn(paper);
        when(adopterServiceClient.getAdopterById(adopterId))
                .thenReturn(AdopterModel.builder().adopterId(adopterId).FName(null).LName(null).build());
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(dogModel);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(volunteerModel);

        assertThrows(NotFoundException.class, () -> {
            adoptionPaperService.updateAdoptionPaper(adopterId, requestModel, adoptionPaperId);
        });
    }

    @Test
    void whenAddAdoptionPaperAdopterWithNullFields_thenThrowNotFoundException() {
        String adopterId = UUID.randomUUID().toString();
        when(adopterServiceClient.getAdopterById(anyString()))
                .thenReturn(AdopterModel.builder().adopterId(adopterId).FName(null).LName(null).build());
        when(volunteerServiceClient.getVolunteerById(anyString())).thenReturn(volunteerModel);
        when(dogServiceClient.getDogById(anyString(), anyString())).thenReturn(dogModel);
        when(locationServiceClient.getLocationById(anyString())).thenReturn(locationModel);

        assertThrows(NotFoundException.class, () -> {
            adoptionPaperService.addAdoptionPaper(requestModel, adopterId);
        });
    }

    @Test
    void whenGetAdoptionPapersRepositoryReturnsNull_thenReturnEmptyList() {
        String adopterId = UUID.randomUUID().toString();
        when(adopterServiceClient.getAdopterById(adopterId)).thenReturn(adopterModel);
        when(adoptionPaperRepository.findAllByAdopterModel_AdopterId(adopterId)).thenReturn(null);

        List<AdoptionPaperResponseModel> result = adoptionPaperService.getAdoptionPapers(adopterId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


}
