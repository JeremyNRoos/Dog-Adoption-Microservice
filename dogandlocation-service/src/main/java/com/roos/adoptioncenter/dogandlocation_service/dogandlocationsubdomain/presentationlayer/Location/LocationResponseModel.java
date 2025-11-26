package com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.presentationlayer.Location;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Location.LocationAddress;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Location.ShelterTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponseModel extends RepresentationModel<LocationResponseModel>{
    public String locationId;
    public String name;
    public ShelterTypeEnum shelterType;
    public LocationAddress address;
    public Integer capacity;
    public Integer availableSpace;
//    public Kennel kennel;
}
