package com.roos.adoptioncenter.volunteer_service.dataaccesslayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class VolunteerRepositoryTest {

    @Autowired
    private VolunteerRepository volunteerRepository;

    @BeforeEach
    public void setUp() {
        volunteerRepository.deleteAll();
    }

    @Test
    public void whenVolunteersExist_thenReturnAllVolunteers(){
        //arrange
        Volunteer volunteer1 = new Volunteer("John", "Doe", "123@gmail.com", 12345, TitleEnum2.CLERK,
                new VolunteerPhoneNumber(PhoneTypeEnum.HOME, "438-521-3774"), new VolunteerAddress("123 maple wood","montreal", "quebec", "canada", "m5a 1a1") );
        Volunteer volunteer2 = new Volunteer("Jane", "Doe", "123@gmail.com", 12345, TitleEnum2.CLERK, new VolunteerPhoneNumber(PhoneTypeEnum.HOME, "438-521-3774"), new VolunteerAddress("123 maple wood","montreal", "quebec", "canada", "m5a 1a1") );
        volunteerRepository.save(volunteer1);
        volunteerRepository.save(volunteer2);
        long afterSizeDB = volunteerRepository.count();

        //act
        List<Volunteer> volunteers = volunteerRepository.findAll();
        //assert
        assertNotNull(volunteers);
        assertNotEquals( 0, afterSizeDB);
        assertEquals(afterSizeDB, volunteers.size());
    }

    @Test
    public void whenVolunteersExists_thenReturnVolunteerByVolunteerId(){
        //arrange
        Volunteer volunteer1 = new Volunteer("John", "Doe", "123@gmail.com", 12345, TitleEnum2.CLERK, new VolunteerPhoneNumber(PhoneTypeEnum.HOME, "438-521-3774"), new VolunteerAddress("123 maple wood","montreal", "quebec", "canada", "m5a 1a1") );
        volunteer1.setVolunteerIdentifier(new VolunteerIdentifier());
        volunteerRepository.save(volunteer1);
        String Id = volunteer1.getVolunteerIdentifier().getVolunteerId();
        //act
        Volunteer volunteer = volunteerRepository.findVolunteerByVolunteerIdentifier_VolunteerId(Id);
        //assert
        assertNotNull(volunteer);
        assertEquals(volunteer1.getFName(), volunteer.getFName());
        assertEquals(Id, volunteer.getVolunteerIdentifier().getVolunteerId());
        assertEquals(volunteer1.getTitle(), volunteer.getTitle());
    }

    @Test
    public void whenVolunteerDoesNotExist_thenReturnNull(){
        //arrange
        Volunteer result = volunteerRepository.findVolunteerByVolunteerIdentifier_VolunteerId("non-existing-id");

        //assert
        assertNull(result);

    }

    @Test
    public void whenValidEntitySaved_thenPersistAndReturn() {
        //arrange
        Volunteer volunteer1 = new Volunteer("John", "Doe", "123@gmail.com", 12345, TitleEnum2.CLERK,
                new VolunteerPhoneNumber(PhoneTypeEnum.HOME, "438-521-3774"), new VolunteerAddress("123 maple wood","montreal", "quebec", "canada", "m5a 1a1") );
        volunteer1.setVolunteerIdentifier(new VolunteerIdentifier());
        //act
        Volunteer volunteer = volunteerRepository.save(volunteer1);
        //assert
        assertNotNull(volunteer);
        assertNotNull(volunteer.getVolunteerIdentifier().getVolunteerId());
        assertEquals("John", volunteer.getFName());
        assertEquals(TitleEnum2.CLERK, volunteer.getTitle());
    }

    @Test
    public void whenValidEntitySavedById_thenPersistAndReturn() {
        //arrange
        Volunteer volunteer1 = new Volunteer("John", "Doe", "123@gmail.com", 12345, TitleEnum2.CLERK,
                new VolunteerPhoneNumber(PhoneTypeEnum.HOME, "438-521-3774"), new VolunteerAddress("123 maple wood","montreal", "quebec", "canada", "m5a 1a1") );
        volunteer1.setVolunteerIdentifier(new VolunteerIdentifier());
        //act
        Volunteer volunteer = volunteerRepository.save(volunteer1);
        //assert
        assertNotNull(volunteer);
        assertNotNull(volunteer.getVolunteerIdentifier().getVolunteerId());
        assertEquals("John", volunteer.getFName());
        assertEquals(TitleEnum2.CLERK, volunteer.getTitle());
    }




}