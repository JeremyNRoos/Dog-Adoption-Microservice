package com.roos.adoptioncenter.volunteer_service.dataaccesslayer;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;


@Embeddable
@NoArgsConstructor
@Getter
public class VolunteerPhoneNumber {
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    public PhoneTypeEnum type;

    @Column(name = "phone_number")
    public String phoneNumber;


    public VolunteerPhoneNumber(@NotNull PhoneTypeEnum phoneTypeEnum,@NotNull String phoneNumber) {
        this.type = phoneTypeEnum;
        this.phoneNumber = phoneNumber;
    }
}

