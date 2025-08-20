package com.coelho.designation.gen.repository;

import com.coelho.designation.gen.entity.Municipalities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MunicipalitiesRepository extends JpaRepository<Municipalities, Long> {
    public List<Municipalities> findByName(String name);
    public Optional<Municipalities> findByAcronym(String acronym);
    public Boolean existsByAcronym(String acronym);
}
