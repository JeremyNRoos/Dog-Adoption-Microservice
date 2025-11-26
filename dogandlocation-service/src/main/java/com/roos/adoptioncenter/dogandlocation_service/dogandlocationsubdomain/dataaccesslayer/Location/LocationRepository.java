package com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Integer>{

    Location findLocationByLocationIdentifier_LocationId(String locationId);
}
