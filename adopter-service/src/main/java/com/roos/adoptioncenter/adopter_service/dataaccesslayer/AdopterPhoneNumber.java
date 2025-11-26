package com.roos.adoptioncenter.adopter_service.dataaccesslayer;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import org.antlr.v4.runtime.misc.NotNull;

@Embeddable
@Getter
public class AdopterPhoneNumber {
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    public PhoneTypeEnum type;

    @Column(name = "phone_number")
    public String phoneNumber;

    public AdopterPhoneNumber(@NotNull PhoneTypeEnum type, @NotNull String phoneNumber) {
        this.type = type;
        this.phoneNumber = phoneNumber;
    }

    public AdopterPhoneNumber(){};


}
