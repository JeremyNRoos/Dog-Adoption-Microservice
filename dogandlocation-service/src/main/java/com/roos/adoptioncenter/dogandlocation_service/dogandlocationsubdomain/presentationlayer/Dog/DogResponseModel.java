package com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.presentationlayer.Dog;

import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Dog.AvailabilityStatusEnum;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Dog.DogBreedEnum;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Dog.Kennel;
import com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.dataaccesslayer.Dog.VaccinationStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DogResponseModel extends RepresentationModel<DogResponseModel> {
    public String dogId;
    public String locationId;
    public String name;
    public DogBreedEnum breed;
    public Integer age;
    public Kennel dogKennel;
    public VaccinationStatusEnum vaccinationStatus;
    public AvailabilityStatusEnum availabilityStatus;
}
