package com.coelho.designation.gen.service;

import com.coelho.designation.gen.model.Designation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DesignationsService {

    public ResponseEntity<?> generateDesignation(Designation designation){
        String generatedDesignation = "";

        if (designation.getCNL() == null || designation.getContractId() == null || designation.getCircuitType() == null){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Erro, há campos inválidos ou não preenchidos, na entrada informada!");
        }

        try {
            generatedDesignation = designation.getCNL() + "000" + designation.getContractId() + designation.getCircuitType().getTypeAcronym();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(generatedDesignation);
    }
}
