package com.roos.adoptioncenter.adopter_service.mappinglayer;

import com.roos.adoptioncenter.adopter_service.dataaccesslayer.Adopter;
import com.roos.adoptioncenter.adopter_service.dataaccesslayer.AdopterAddress;
import com.roos.adoptioncenter.adopter_service.dataaccesslayer.AdopterIdentifier;
import com.roos.adoptioncenter.adopter_service.dataaccesslayer.AdopterPhoneNumber;
import com.roos.adoptioncenter.adopter_service.presentation.AdopterRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface AdopterRequestMapper {
    @Mappings({
            @Mapping(expression = "java(adopterIdentifier)", target = "adopterIdentifier"),
            @Mapping(source = "adopterRequestModel.fName", target = "fName"),
            @Mapping(source = "adopterRequestModel.lName", target = "lName"),
            @Mapping(expression = "java(adopterAddress)", target = "adopterAddress"),
            @Mapping(expression = "java(adopterPhoneNumber)", target = "adopterPhoneNumber"),
            @Mapping(source = "adopterRequestModel.contactMethodPreference", target = "contactMethodPreference")

    })
    Adopter requestToEntity(AdopterRequestModel adopterRequestModel, AdopterIdentifier adopterIdentifier, AdopterAddress adopterAddress, AdopterPhoneNumber adopterPhoneNumber);
}
