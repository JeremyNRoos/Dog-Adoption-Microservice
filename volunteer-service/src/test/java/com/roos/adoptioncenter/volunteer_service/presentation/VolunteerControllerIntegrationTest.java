package com.roos.adoptioncenter.volunteer_service.presentation;

import com.roos.adoptioncenter.volunteer_service.dataaccesslayer.VolunteerPhoneNumber;
import com.roos.adoptioncenter.volunteer_service.dataaccesslayer.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Sql({"/data-psql.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class VolunteerControllerIntegrationTest {

        @Autowired
        private WebTestClient webTestClient;

        @Autowired
        private VolunteerRepository repository;

        private final String BASE_URI = "/api/v1/volunteers";
        private final String VALID_ID = "6a8aeaec-cff9-4ace-a8f0-146f8ed180e5";
        private final String NOT_FOUND_ID = "00000000-0000-0000-0000-000000000000";
    private final String INVALID_ID = "00000000-0000-0000-0000-0000000000000";


    @Test
    void whenVolunteerExists_thenReturnAllVolunteer() {
        long sizeDB = this.repository.count();

        this.webTestClient.get()
                .uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(VolunteerResponseModel.class)
                .value((list) -> {
                    assertNotNull(list);
                    assertNotEquals(0, sizeDB);
                    assertEquals(sizeDB, list.size());
                    list.forEach((authorResponseModel) -> {
                        assertNotNull(authorResponseModel);
                        assertNotNull(authorResponseModel.getVolunteerId());
                        assertNotNull(authorResponseModel.getFName());
                        assertNotNull(authorResponseModel.getLName());
                        assertNotNull(authorResponseModel.getEmail());
                        assertNotNull(authorResponseModel.getVolunteerAddress());
                        assertNotNull(authorResponseModel.getVolunteerPhoneNumber());
                        assertNotNull(authorResponseModel.getTitle());
                        assertNotNull(authorResponseModel.getSalary());
                    });
                });
    }
    @Test
    void getAllAccounts_emptyDB_returnsEmptyList() {
        repository.deleteAll();
        webTestClient.get().uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(VolunteerResponseModel.class)
                .value(list -> assertEquals(0, list.size()));
    }

    @Test
    void getAccountById_validId_returnsAccount() {
        webTestClient.get().uri(BASE_URI + "/" + VALID_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(VolunteerResponseModel.class)
                .value(account -> assertEquals(VALID_ID, account.getVolunteerId()));
    }


    @Test
    void getAccountById_nonExistent_returns404() {
        webTestClient.get().uri(BASE_URI + "/" + NOT_FOUND_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Unknown volunteerid: " + NOT_FOUND_ID);
    }


    @Test
    void createValidAccount_returnsCreated() {
        VolunteerResponseModel model = VolunteerResponseModel.builder()
                .fName("John").lName("Doe").email("john.doe@example.com").volunteerPhoneNumber(new VolunteerPhoneNumber(PhoneTypeEnum.HOME, "438-521-3774")).build();

        webTestClient.post().uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(model)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(VolunteerResponseModel.class)
                .value(response -> assertEquals(model.getFName(), response.getFName()));
    }


    @Test
    void notFoundException_returnsProperFormat() {
        webTestClient.get()
                .uri(BASE_URI + "/" + NOT_FOUND_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().jsonPath("$.httpStatus").isEqualTo("NOT_FOUND");
    }

    /*--> Validation tests <--*/

    @Test
    void whenVolunteerExists_thenReturnAllVolunteers() {
        long sizeDB = this.repository.count();

        this.webTestClient.get()
                .uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(VolunteerResponseModel.class)
                .value((list) -> {
                    assertNotNull(list);
                    assertNotEquals(0, sizeDB);
                    assertEquals(sizeDB, list.size());
                });
    }

    @Test
    void whenVolunteerIdExists_thenReturnIsOk() {
        this.webTestClient.get()
                .uri(BASE_URI + "/" + VALID_ID)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaTypes.HAL_JSON)
                .expectBody(VolunteerResponseModel.class)
                .value((volunteerResponseModel) -> {
                    assertNotNull(volunteerResponseModel);
                    assertNotNull(volunteerResponseModel.getVolunteerAddress());
                    assertNotNull(volunteerResponseModel.getEmail());
                    assertNotNull(volunteerResponseModel.getSalary());
                    assertNotNull(volunteerResponseModel.getVolunteerId());
                    assertNotNull(volunteerResponseModel.getFName());
                    assertNotNull(volunteerResponseModel.getLName());
                    assertNotNull(volunteerResponseModel.getTitle());
                    assertNotNull(volunteerResponseModel.getVolunteerPhoneNumber());
                });
    }

    @Test
    void whenVolunteerExist_thenReturnNewVolunteer() {
        VolunteerRequestModel volunteerRequestModel = VolunteerRequestModel.builder()
                .fName("Jeremy")
                .lName("Roos")
                .title(TitleEnum2.MANAGER)
                .build();

        this.webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaTypes.HAL_JSON)
                .bodyValue(volunteerRequestModel)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaTypes.HAL_JSON)
                .expectBody(VolunteerResponseModel.class)
                .value((volunteerResponseModel) -> {
                    assertNotNull(volunteerResponseModel);
                    assertNotNull(volunteerResponseModel.getVolunteerId());
                    assertNotNull(volunteerResponseModel.getFName());
                    assertNotNull(volunteerResponseModel.getLName());
                    assertNotNull(volunteerResponseModel.getTitle());

                    assertEquals(volunteerRequestModel.getFName(), volunteerResponseModel.getFName());
                    assertEquals(volunteerRequestModel.getLName(), volunteerResponseModel.getLName());
                    assertEquals(volunteerRequestModel.getTitle(), volunteerResponseModel.getTitle());
                });
    }

    @Test
    void whenVolunteerExistsOnUpdate_thenReturnIsOk() {
        VolunteerRequestModel volunteerRequestModel = VolunteerRequestModel.builder()
                .fName("Jeremy")
                .lName("Roos")
                .title(TitleEnum2.MANAGER)
                .build();

        this.webTestClient.put()
                .uri(BASE_URI + "/" + VALID_ID)
                .contentType(MediaTypes.HAL_JSON)
                .bodyValue(volunteerRequestModel)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaTypes.HAL_JSON)
                .expectBody(VolunteerResponseModel.class)
                .value((volunteerResponseModel) -> {
                    assertNotNull(volunteerResponseModel);
                    assertNotNull(volunteerResponseModel.getVolunteerId());
                    assertNotNull(volunteerResponseModel.getFName());
                    assertNotNull(volunteerResponseModel.getLName());

                    assertEquals(volunteerRequestModel.getFName(), volunteerResponseModel.getFName());
                    assertEquals(volunteerRequestModel.getLName(), volunteerResponseModel.getLName());
                    assertEquals(volunteerRequestModel.getTitle(), volunteerResponseModel.getTitle());
                });
    }

    @Test
    void whenVolunteerExistsOnDelete_thenReturnNoContent() {
        this.webTestClient.delete()
                .uri(BASE_URI + "/" + VALID_ID)
                .exchange()
                .expectStatus().isNoContent();

        this.webTestClient.get()
                .uri(BASE_URI + "/" + VALID_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Unknown volunteerid: " + VALID_ID);
    }

    /*--> GET Tests <--*/

    @Test
    void whenVolunteerIdIsInvalidOnGet_thenReturnUnprocessableEntity() {
        this.webTestClient.get()
                .uri(BASE_URI + "/" + INVALID_ID)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid volunteerid: " +  INVALID_ID);
    }

    @Test
    void whenVolunteerIdIsNotFoundOnGet_thenReturnNotFound() {
        this.webTestClient.get()
                .uri(BASE_URI + "/" + NOT_FOUND_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Unknown volunteerid: " + NOT_FOUND_ID);
    }

    /*--> POST Tests <--*/

    @Test
    void whenVolunteerFirstNameIsInvalidOnPost_thenReturnBadRequest() {
        VolunteerRequestModel volunteerRequestModel = VolunteerRequestModel.builder()
                .fName("")
                .lName("Roos")
                .title(TitleEnum2.MANAGER)
                .build();

        this.webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(volunteerRequestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid firstName: " + volunteerRequestModel.getFName());
    }

    @Test
    void whenVolunteerLastNameIsInvalidOnPost_thenReturnBadRequest() {
        VolunteerRequestModel volunteerRequestModel = VolunteerRequestModel.builder()
                .fName("Jeremy")
                .lName("")
                .title(TitleEnum2.MANAGER)
                .build();

        this.webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(volunteerRequestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid lastName: " + volunteerRequestModel.getLName());
    }

    /*--> PUT Tests <--*/

    @Test
    void whenVolunteerIdIsInvalidOnPut_thenReturnBadRequest() {
        VolunteerRequestModel volunteerRequestModel = VolunteerRequestModel.builder()
                .fName("Jeremy")
                .lName("Roos")
                .title(TitleEnum2.MANAGER)
                .build();

        this.webTestClient.put()
                .uri(BASE_URI + "/" + INVALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(volunteerRequestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid volunteerid: " +  INVALID_ID);
    }

    @Test
    void whenVolunteerIdIsNotFoundOnPut_thenReturnNotFound() {
        VolunteerRequestModel volunteerRequestModel = VolunteerRequestModel.builder()
                .fName("Jeremy")
                .lName("Wallace")
                .title(TitleEnum2.MANAGER)
                .build();

        this.webTestClient.put()
                .uri(BASE_URI + "/" + NOT_FOUND_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(volunteerRequestModel)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Unknown volunteerid: " + NOT_FOUND_ID);
    }

    @Test
    void whenVolunteerFirstNameIsInvalidOnPut_thenReturnUnprocessableEntity() {
        VolunteerRequestModel volunteerRequestModel = VolunteerRequestModel.builder()
                .fName("") // Invalid
                .lName("Smith")
                .email("test@example.com")
                .salary("30000")
                .title(TitleEnum2.CLERK)
                .volunteerPhoneNumber(new VolunteerPhoneNumber(PhoneTypeEnum.MOBILE, "1234567890"))
                .volunteerAddress(new VolunteerAddress("123 Main St", "Montreal", "Quebec", "Canada", "H1A1A1"))
                .build();

        this.webTestClient.put()
                .uri(BASE_URI + "/" + VALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(volunteerRequestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid firstName: " + volunteerRequestModel.getFName());
    }

    @Test
    void whenVolunteerLastNameIsInvalidOnPut_thenReturnUnprocessableEntity() {
        VolunteerRequestModel volunteerRequestModel = VolunteerRequestModel.builder()
                .fName("John")
                .lName("") // Invalid
                .email("test@example.com")
                .salary("30000")
                .title(TitleEnum2.CLERK)
                .volunteerPhoneNumber(new VolunteerPhoneNumber(PhoneTypeEnum.MOBILE, "1234567890"))
                .volunteerAddress(new VolunteerAddress("123 Main St", "Montreal", "Quebec", "Canada", "H1A1A1"))
                .build();

        this.webTestClient.put()
                .uri(BASE_URI + "/" + VALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(volunteerRequestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid lastName: " + volunteerRequestModel.getLName());
    }
}

