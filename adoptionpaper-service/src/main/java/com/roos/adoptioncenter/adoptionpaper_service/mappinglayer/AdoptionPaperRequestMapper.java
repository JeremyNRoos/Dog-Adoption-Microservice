package com.roos.adoptioncenter.adoptionpaper_service.mappinglayer;

import com.roos.adoptioncenter.adoptionpaper_service.dataaccesslayer.AdoptionPaper;
import com.roos.adoptioncenter.adoptionpaper_service.dataaccesslayer.AdoptionPaperIdentifier;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.adopter.AdopterModel;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.dog.DogModel;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.dog.LocationModel;
import com.roos.adoptioncenter.adoptionpaper_service.domainclientlayer.volunteer.VolunteerModel;
import com.roos.adoptioncenter.adoptionpaper_service.presentationlayer.AdoptionPaperRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


@Mapper(componentModel = "spring")
public interface AdoptionPaperRequestMapper {


    @Mappings(
            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(expression = "java(adoptionPaperIdentifier)", target = "adoptionPaperIdentifier"),
                    @Mapping(expression = "java(volunteerModel)", target = "volunteerModel"),
                    @Mapping(expression = "java(locationModel)", target = "locationModel"),
                    @Mapping(expression = "java(dogModel)", target = "dogModel"),
                    @Mapping(expression = "java(adopterModel)", target = "adopterModel")
            }
    )
    AdoptionPaper requestToEntity(AdoptionPaperRequestModel adoptionPaperRequestModel,
                                  AdoptionPaperIdentifier adoptionPaperIdentifier,
                                  VolunteerModel volunteerModel,
                                  LocationModel locationModel,
                                  DogModel dogModel,
                                  AdopterModel adopterModel);
    }
