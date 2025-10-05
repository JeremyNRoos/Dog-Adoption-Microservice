package com.roos.adoptioncenter.adopter_service.dataaccesslayer;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Embeddable
@NoArgsConstructor
@Getter
public class AdopterAddress {
    @Column(name = "address")
    private String streetAddress;
    @Column(name = "city")
    private String city;
    @Column(name = "province")
    private String province;
    @Column(name = "country")
    private String country;
    @Column(name = "postal_code")
    private String postalCode;

    public AdopterAddress(@NotNull String streetAddress, @NotNull String city, @NotNull String province, @NotNull  String country, @NotNull String postalCode) {
        this.streetAddress = streetAddress;
        this.city = city;
        this.province = province;
        this.country = country;
        this.postalCode = postalCode;

    }
}
