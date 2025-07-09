package com.coelho.designation.gen.repository;

import com.coelho.designation.gen.model.Designation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesignationsRepository extends JpaRepository<Designation, Long> {
}
