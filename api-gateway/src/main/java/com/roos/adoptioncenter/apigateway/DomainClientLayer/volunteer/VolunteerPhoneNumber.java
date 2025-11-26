package com.roos.adoptioncenter.apigateway.DomainClientLayer.volunteer;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter

public class VolunteerPhoneNumber {
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    public PhoneTypeEnum type;

    @Column(name = "phone_number")
    public String phoneNumber;

    public VolunteerPhoneNumber(String s) {
    }
}

