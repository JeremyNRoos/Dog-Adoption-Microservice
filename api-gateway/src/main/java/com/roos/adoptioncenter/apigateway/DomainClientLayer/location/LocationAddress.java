package com.roos.adoptioncenter.apigateway.DomainClientLayer.location;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import org.antlr.v4.runtime.misc.NotNull;

@Embeddable
@Builder
@Getter
public class LocationAddress {
    @Column(name = "address")
    private String streetAddress;
    private String city;
    private String province;
    private String country;
    @Column(name = "postal_code")
    private String postalCode;

    public LocationAddress(@NotNull String streetAddress, @NotNull String city, @NotNull String province, @NotNull  String country, @NotNull String postalCode) {
        this.streetAddress = streetAddress;
        this.city = city;
        this.province = province;
        this.country = country;
        this.postalCode = postalCode;

    }

    public LocationAddress() {
    }
}
