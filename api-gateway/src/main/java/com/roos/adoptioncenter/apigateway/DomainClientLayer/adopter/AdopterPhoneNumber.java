package com.roos.adoptioncenter.apigateway.DomainClientLayer.adopter;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

@Embeddable
@Builder
@Getter
public class AdopterPhoneNumber {
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    public PhoneTypeEnum type;

    @Column(name = "phone_number")
    public String phoneNumber;

    public AdopterPhoneNumber(PhoneTypeEnum type, String phoneNumber) {
        this.type = type;
        this.phoneNumber = phoneNumber;
    }

    public AdopterPhoneNumber(){};
}
