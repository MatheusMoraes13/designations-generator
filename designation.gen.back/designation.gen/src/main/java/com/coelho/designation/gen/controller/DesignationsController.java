package com.coelho.designation.gen.controller;

import com.coelho.designation.gen.model.Designation;
import com.coelho.designation.gen.service.DesignationsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/designations-generator")
@AllArgsConstructor
public class DesignationsController {

    DesignationsService designationsService;

    @PostMapping
    public ResponseEntity<?> generateDesignation(@RequestBody Designation designation){
        return designationsService.generateDesignation(designation);
    }
}
