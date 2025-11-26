package com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.presentationlayer.Dog;

import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Dog.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Sql({"/data.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class DogControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DogRepository repository;

    private final String LOCATION_ID = "3e6c62a3-b1e7-4eb7-9642-8cdcb3ac74e6";
    private final String BASE_URI = "/api/v1/locations/" + LOCATION_ID + "/dogs";
    private final String VALID_ID = "2cfa25c5-1d13-4a9c-ae2a-55e2a5ae2481";
    private final String INVALID_ID = "invalid-dog-id";

    @Test
    void whenDogsExist_thenReturnAllDogsForLocation() {
        long sizeDB = repository.findAllByLocationIdentifier_LocationId(LOCATION_ID).size();

        webTestClient.get()
                .uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DogResponseModel.class)
                .value(list -> {
                    assertNotNull(list);
                    assertEquals(sizeDB, list.size());
                });
    }

    @Test
    void getDogById_validId_returnsDog() {
        webTestClient.get().uri(BASE_URI + "/" + VALID_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(DogResponseModel.class)
                .value(dog -> assertEquals(VALID_ID, dog.getDogId()));
    }

    @Test
    void getDogById_invalidId_returnsUnprocessableEntity() {
        webTestClient.get()
                .uri(BASE_URI + "/" + INVALID_ID)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid dogid: " + INVALID_ID);
    }

    @Test
    void postDog_valid_returnsCreated() {
        DogRequestModel model = DogRequestModel.builder()
                .name("Buddy")
                .breed(DogBreedEnum.AFGHAN_HOUND)
                .age(3)
                .availabilityStatus(AvailabilityStatusEnum.AVAILABLE)
                .dogKennel(new Kennel(KennelSizeEnum.LARGE))
                .build();

        webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(model)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(DogResponseModel.class)
                .value(response -> {assertEquals(model.getName(), response.getName());
                    assertEquals(model.getAge(), response.getAge());
                    assertEquals(model.getAvailabilityStatus(), response.getAvailabilityStatus());
                    assertEquals(model.getBreed(), response.getBreed());
                    assertEquals(model.getDogKennel().getKennelSize(), response.getDogKennel().getKennelSize());
                    assertNotNull(response.getDogId());
                });
    }

    @Test
    void putDog_valid_returnsUpdatedDog() {
        DogRequestModel update = DogRequestModel.builder()
                .name("Max")
                .breed(DogBreedEnum.AFGHAN_HOUND)
                .age(4)
                .availabilityStatus(AvailabilityStatusEnum.AVAILABLE)
                .dogKennel(new Kennel(KennelSizeEnum.LARGE))
                .build();

        webTestClient.put()
                .uri(BASE_URI + "/" + VALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isOk()
                .expectBody(DogResponseModel.class)
                .value(response -> {
                    assertEquals("Max", response.getName());
                    assertEquals(4, response.getAge());
                    assertEquals(VALID_ID, response.getDogId());
                    assertEquals(DogBreedEnum.AFGHAN_HOUND, response.getBreed());
                    assertEquals(AvailabilityStatusEnum.AVAILABLE, response.getAvailabilityStatus());
                    assertEquals(KennelSizeEnum.LARGE, response.getDogKennel().getKennelSize());
                });
    }

    @Test
    void deleteDog_validId_returnsNoContent() {
        webTestClient.delete()
                .uri(BASE_URI + "/" + VALID_ID)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void deleteDog_invalidId_returnsUnprocessableEntity() {
        webTestClient.delete()
                .uri(BASE_URI + "/" + INVALID_ID)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid dogid: " + INVALID_ID);
    }

}