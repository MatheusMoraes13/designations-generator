package com.coelho.designation.gen.controller;

import com.coelho.designation.gen.service.CircuitsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/circuits")
@AllArgsConstructor
public class CircuitsController {

    CircuitsService circuitsService;

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshCircuits() {
        return circuitsService.refreshCircuits();
    }

}
