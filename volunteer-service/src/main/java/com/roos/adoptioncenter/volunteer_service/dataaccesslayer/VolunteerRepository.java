package com.roos.adoptioncenter.volunteer_service.dataaccesslayer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VolunteerRepository extends JpaRepository<Volunteer, Integer> {
    Volunteer findVolunteerByVolunteerIdentifier_VolunteerId(String volunteerId);
}
