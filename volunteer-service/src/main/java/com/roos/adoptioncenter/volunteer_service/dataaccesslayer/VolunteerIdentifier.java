package com.roos.adoptioncenter.volunteer_service.dataaccesslayer;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.UUID;

@Getter
@Embeddable
public class VolunteerIdentifier {
    @Column(name = "volunteer_id")
    private String volunteerId;
    public VolunteerIdentifier() {
        this.volunteerId = UUID.randomUUID().toString();
    }
    public VolunteerIdentifier(String volunteerId) {
        this.volunteerId = volunteerId;
    }
}
