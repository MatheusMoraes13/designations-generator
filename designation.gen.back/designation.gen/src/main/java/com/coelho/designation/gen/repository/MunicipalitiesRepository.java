package com.coelho.designation.gen.repository;

import com.coelho.designation.gen.model.Municipalities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MunicipalitiesRepository extends JpaRepository<Municipalities, Long> {
    public Optional<Municipalities> findByName(String name);
    public Optional<Municipalities> findByAcronym(String acronym);
    public Boolean existsByAcronym(String acronym);
}
