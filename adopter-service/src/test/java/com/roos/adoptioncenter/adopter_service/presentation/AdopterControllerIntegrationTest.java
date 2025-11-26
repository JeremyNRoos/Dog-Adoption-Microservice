package com.roos.adoptioncenter.adopter_service.presentation;

import com.roos.adoptioncenter.adopter_service.dataaccesslayer.AdopterAddress;
import com.roos.adoptioncenter.adopter_service.dataaccesslayer.AdopterPhoneNumber;
import com.roos.adoptioncenter.adopter_service.dataaccesslayer.AdopterRepository;
import com.roos.adoptioncenter.adopter_service.dataaccesslayer.PhoneTypeEnum;
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
class AdopterControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AdopterRepository repository;

    private final String BASE_URI = "/api/v1/adopters";

    private final String NOT_FOUND_ID = "fcbf86b1-8a76-4d2b-a352-75b10a8fd4a2";

    private final String VALID_ID = "fcbf86b1-8a76-4d2b-a352-75b10a8fd4a1";
    private final String INVALID_ID = "not-a-valid-id";

    @Test
    void whenAdoptersExist_thenReturnAllAdopters() {
        long sizeDB = this.repository.count();

        this.webTestClient.get()
                .uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AdopterResponseModel.class)
                .value((list) -> {
                    assertNotNull(list);
                    assertEquals(sizeDB, list.size());
                });
    }

    @Test
    void getAdopterById_validId_returnsAdopter() {
        webTestClient.get().uri(BASE_URI + "/" + VALID_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AdopterResponseModel.class)
                .value(adopter -> assertEquals(VALID_ID, adopter.getAdopterId()));
    }

    @Test
    void getAdopterById_nonExistent_returnsNotFound() {
        webTestClient.get().uri(BASE_URI + "/" + NOT_FOUND_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().jsonPath("$.message").isEqualTo("Unknown adopterid: " + NOT_FOUND_ID);
    }

    @Test
    void createValidAdopter_returnsCreated() {
        AdopterRequestModel model = AdopterRequestModel.builder()
                .fName("Alice")
                .lName("Smith")
                .phoneNumber(new AdopterPhoneNumber(PhoneTypeEnum.HOME, "123-456-7890"))
                .address(new AdopterAddress("1 Elm St", "Ottawa", "Ontario", "Canada", "K1A0B1"))
                .build();

        webTestClient.post().uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(model)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AdopterResponseModel.class)
                .value(response -> assertEquals(model.getFName(), response.getFName()));
    }

    @Test
    void deleteAdopter_validId_returnsNoContent() {
        webTestClient.delete()
                .uri(BASE_URI + "/" + VALID_ID)
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get()
                .uri(BASE_URI + "/" + VALID_ID)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateAdopter_validId_returnsUpdatedAdopter() {
        AdopterRequestModel updateRequest = AdopterRequestModel.builder()
                .fName("Bob")
                .lName("Jones")
                .phoneNumber(new AdopterPhoneNumber(PhoneTypeEnum.WORK, "111-222-3333"))
                .address(new AdopterAddress("12 King St", "Toronto", "Ontario", "Canada", "M5V2T6"))
                .build();

        this.webTestClient.put()
                .uri(BASE_URI + "/" + VALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AdopterResponseModel.class)
                .value(response -> {
                    assertEquals("Bob", response.getFName());
                    assertEquals("Jones", response.getLName());
                });
    }

    @Test
    void getAdopterById_invalidId_returnsUnprocessableEntity() {
        webTestClient.get()
                .uri(BASE_URI + "/" + INVALID_ID)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid adopterid: " + INVALID_ID);
    }

    @Test
    void updateAdopter_invalidId_returnsUnprocessableEntity() {
        AdopterRequestModel request = AdopterRequestModel.builder()
                .fName("Jane")
                .lName("Doe")
                .build();

        this.webTestClient.put()
                .uri(BASE_URI + "/" + INVALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void postAdopter_invalidFirstName_returnsUnprocessableEntity() {
        AdopterRequestModel request = AdopterRequestModel.builder()
                .fName("")
                .lName("Smith")
                .build();

        this.webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid firstName: " + request.getFName());
    }

    @Test
    void postAdopter_invalidLastName_returnsUnprocessableEntity() {
        AdopterRequestModel request = AdopterRequestModel.builder()
                .fName("John")
                .lName("")
                .build();

        this.webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid lastName: ");
    }




    @Test
    void putAdopter_nonexistentId_returnsNotFound() {
        AdopterRequestModel update = AdopterRequestModel.builder()
                .fName("Non")
                .lName("Exist")
                .phoneNumber(new AdopterPhoneNumber(PhoneTypeEnum.WORK, "111-111-1111"))
                .address(new AdopterAddress("Somewhere", "Nowhere", "ZZ", "Country", "Z9Z9Z9"))
                .build();

        webTestClient.put()
                .uri(BASE_URI + "/" + NOT_FOUND_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Unknown adopterid: " + NOT_FOUND_ID);
    }

    @Test
    void deleteAdopter_invalidId_returnsUnprocessableEntity() {
        webTestClient.delete()
                .uri(BASE_URI + "/" + INVALID_ID)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid adopterid: " + INVALID_ID);
    }

    @Test
    void createAndFetchAdopter_returnsSameData() {
        AdopterRequestModel model = AdopterRequestModel.builder()
                .fName("Maria")
                .lName("Lopez")
                .phoneNumber(new AdopterPhoneNumber(PhoneTypeEnum.WORK, "222-333-4444"))
                .address(new AdopterAddress("9 Pine Ave", "Calgary", "Alberta", "Canada", "T2T2T2"))
                .build();

        AdopterResponseModel response = webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(model)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AdopterResponseModel.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        webTestClient.get()
                .uri(BASE_URI + "/" + response.getAdopterId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(AdopterResponseModel.class)
                .value(body -> {
                    assertEquals("Maria", body.getFName());
                    assertEquals("Lopez", body.getLName());
                });
    }

    @Test
    void postAdopter_withMinimalValidData_returnsCreated() {
        AdopterRequestModel model = AdopterRequestModel.builder()
                .fName("Min")
                .lName("Valid")
                .phoneNumber(new AdopterPhoneNumber(PhoneTypeEnum.MOBILE, "111-111-1111"))
                .address(new AdopterAddress("Short St", "Tiny", "PEI", "Canada", "A1A1A1"))
                .build();

        webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(model)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.fName").isEqualTo("Min");
    }

    @Test
    void putAdopter_valid_updateAllFields() {
        AdopterRequestModel update = AdopterRequestModel.builder()
                .fName("Updated")
                .lName("Fields")
                .phoneNumber(new AdopterPhoneNumber(PhoneTypeEnum.HOME, "000-000-0000"))
                .address(new AdopterAddress("New Addr", "New City", "New Prov", "New Country", "Z0Z0Z0"))
                .build();

        webTestClient.put()
                .uri(BASE_URI + "/" + VALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.fName").isEqualTo("Updated")
                .jsonPath("$.lName").isEqualTo("Fields");
    }

    @Test
    void postAdopter_thenListIncludesNewAdopter() {
        AdopterRequestModel model = AdopterRequestModel.builder()
                .fName("Browse")
                .lName("All")
                .phoneNumber(new AdopterPhoneNumber(PhoneTypeEnum.MOBILE, "888-888-8888"))
                .address(new AdopterAddress("100 Maple", "Winnipeg", "MB", "Canada", "R2C1C1"))
                .build();

        webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(model)
                .exchange()
                .expectStatus().isCreated();

        webTestClient.get()
                .uri(BASE_URI)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AdopterResponseModel.class)
                .value(list -> assertTrue(list.stream().anyMatch(a -> a.getFName().equals("Browse"))));
    }
}