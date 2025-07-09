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

@Service
@AllArgsConstructor
public class MunicipalitiesService {

    private MunicipalitiesRepository municipalitiesRepository;

    @Transactional
    public ResponseEntity<?> registerMunicipalities(Municipalities municipalities){
        municipalities.setName(removeAccents(municipalities.getName()));
        municipalities.setAcronym(municipalities.getAcronym().toUpperCase().replaceAll("\\s+", ""));

        try {
            Optional<Municipalities> foundMunicipalities = municipalitiesRepository.findByName(municipalities.getName());
            if (foundMunicipalities.isPresent() && municipalitiesRepository.existsByAcronym(municipalities.getAcronym())){
                    Municipalities municipalitiesToUpdate = foundMunicipalities.get();

                    municipalitiesToUpdate.setName(municipalities.getName());
                    municipalitiesToUpdate.setAcronym(municipalities.getAcronym());

                    municipalitiesRepository.save(municipalitiesToUpdate);

            }
            municipalitiesRepository.save(municipalities);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.ok().body("Município " + municipalities.getName() + " cadastrado com sucesso!");
    }

    @Transactional
    public ResponseEntity<?> registerMunicipalitiesList(List<Municipalities> municipalitiesList){
        for (Municipalities m : municipalitiesList){
            m.setName(removeAccents(m.getName()));
        }

        try {
            for (Municipalities m : municipalitiesList){
                Optional<Municipalities> foundMunicipalities = municipalitiesRepository.findByName(m.getName());
                if (foundMunicipalities.isPresent() && municipalitiesRepository.existsByAcronym(m.getAcronym())){
                    Municipalities municipalitiesToUpdate = foundMunicipalities.get();

                    municipalitiesToUpdate.setName(m.getName());
                    municipalitiesToUpdate.setAcronym(m.getAcronym());

                    municipalitiesRepository.save(municipalitiesToUpdate);

                }
                municipalitiesRepository.save(m);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok().body("Lista de municípios cadastrada com sucesso!");
    }

    public ResponseEntity<?> findMunicipalitiesByName(SearchMunicipalitiesDTO searchMunicipalities){
        String searchName = removeAccents(searchMunicipalities.name());

        try {
            Optional<Municipalities> foundMunicipalities = municipalitiesRepository.findByName(searchName);
            List<Municipalities> foundMunicipalitiesList = foundMunicipalities.stream().toList();

            if(foundMunicipalities.isEmpty()){
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Nenhum município com o nome \"" + searchMunicipalities.name() + "\" encontrado.");
            } else {
                return ResponseEntity.ok(foundMunicipalities);
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
