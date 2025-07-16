package com.coelho.designation.gen.controller;

import com.coelho.designation.gen.dto.SearchMunicipalitiesDTO;
import com.coelho.designation.gen.model.Municipalities;
import com.coelho.designation.gen.service.MunicipalitiesService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/municipalities")
@AllArgsConstructor
public class MunicipalitiesController {

    MunicipalitiesService municipalitiesService;

    @PostMapping("/list")
    public ResponseEntity<?> registerMunicipalitiesList(@RequestBody List<Municipalities> municipalitiesList){
        return municipalitiesService.registerMunicipalitiesList(municipalitiesList);
    }

    @PostMapping("/findbyname")
    public ResponseEntity<?> findMunicipalitiesByName(@RequestBody SearchMunicipalitiesDTO municipalitiesSearch){
        return municipalitiesService.findMunicipalitiesByName(municipalitiesSearch);
    }
}
