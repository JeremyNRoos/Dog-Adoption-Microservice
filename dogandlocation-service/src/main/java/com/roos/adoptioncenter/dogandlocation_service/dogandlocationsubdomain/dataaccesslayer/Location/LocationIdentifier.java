package com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Location;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.UUID;

@Getter
@Embeddable
public class LocationIdentifier {
    @Column(name = "location_id")
    private String locationId;
    public LocationIdentifier() {
        this.locationId = UUID.randomUUID().toString();
    }
    public LocationIdentifier(String locationId) {
        this.locationId = locationId;
    }
}
