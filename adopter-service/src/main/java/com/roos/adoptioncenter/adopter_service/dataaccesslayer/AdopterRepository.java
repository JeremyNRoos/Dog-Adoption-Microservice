package com.roos.adoptioncenter.adopter_service.dataaccesslayer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AdopterRepository extends JpaRepository<Adopter, Integer> {
    Adopter findAdopterByAdopterIdentifier_AdopterId(String AdopterId);
}
