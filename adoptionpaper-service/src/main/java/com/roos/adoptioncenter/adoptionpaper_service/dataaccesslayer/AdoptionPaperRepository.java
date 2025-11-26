package com.roos.adoptioncenter.adoptionpaper_service.dataaccesslayer;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AdoptionPaperRepository extends MongoRepository<AdoptionPaper, String> {
    AdoptionPaper findAdoptionPaperByAdoptionPaperIdentifier_AdoptionPaperId(String id);
    List<AdoptionPaper> findAllByAdopterModel_AdopterId(String adopterId);
    AdoptionPaper findByAdopterModel_AdopterIdAndAdoptionPaperIdentifier_AdoptionPaperId(String adopterId, String dogId);
}
