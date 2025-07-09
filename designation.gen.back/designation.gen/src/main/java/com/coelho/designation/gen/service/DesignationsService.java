package com.coelho.designation.gen.service;

import com.coelho.designation.gen.model.Designation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DesignationsService {

    public ResponseEntity<?> generateDesignation(Designation designation){
        String generatedDesignation = "";

        try {
            generatedDesignation = designation.getCNL() + "000" + designation.getContractId() + designation.getCircuitType().getTypeAcronym();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(generatedDesignation);
    }
}
