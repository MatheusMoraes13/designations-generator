package com.coelho.designation.gen.service;

import com.coelho.designation.gen.dto.SearchMunicipalitiesDTO;
import com.coelho.designation.gen.model.Municipalities;
import com.coelho.designation.gen.repository.MunicipalitiesRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MunicipalitiesService {

    private MunicipalitiesRepository municipalitiesRepository;

    public ResponseEntity<?> gettAllMunicipalities(){
        List<Municipalities> foundMunicipalities = municipalitiesRepository.findAll();

        if(foundMunicipalities.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(foundMunicipalities);
    }

    @Transactional
    public ResponseEntity<?> registerMunicipalitiesList(List<Municipalities> municipalitiesList){
        for (Municipalities m : municipalitiesList){
            m.setName(removeAccents(m.getName()));
            m.setAcronym(m.getAcronym().toUpperCase().replaceAll("\\s+", ""));
        }

        List<Municipalities> uniqueMunicipalitiesList = municipalitiesList.stream()
                .distinct()
                .toList();

        for (Municipalities m : uniqueMunicipalitiesList){
            List<Municipalities> foundMunicipalities = municipalitiesRepository.findByName(m.getName());
            municipalitiesRepository.save(m);
        }

        return ResponseEntity.ok().body("Lista de municípios cadastrada com sucesso!");
    }


    public ResponseEntity<?> findMunicipalitiesByName(SearchMunicipalitiesDTO searchMunicipalities){
        String searchName = removeAccents(searchMunicipalities.name());

        try {
            List<Municipalities> foundMunicipalitiesList = municipalitiesRepository.findByName(searchName);

            if(foundMunicipalitiesList.isEmpty()){
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Nenhum município com o nome \"" + searchMunicipalities.name() + "\" encontrado.");
            } else {
                return ResponseEntity.ok(foundMunicipalitiesList);
            }

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }

    public static String removeAccents(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("").toLowerCase();
    }
}
