package com.coelho.designation.gen.service;

import com.coelho.designation.gen.dto.SearchMunicipalitiesDTO;
import com.coelho.designation.gen.entity.Municipalities;
import com.coelho.designation.gen.repository.MunicipalitiesRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class MunicipalitiesService {

    private MunicipalitiesRepository municipalitiesRepository;

    public ResponseEntity<?> gettAllMunicipalities(){
        List<Municipalities> foundMunicipalities = municipalitiesRepository.findAll();

        if(foundMunicipalities.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Aviso: Nenhum município encontrado.");
        }

        return ResponseEntity.ok(foundMunicipalities);
    }

    /*
    * Função responsável pela realização do cadastro dos municípios e seus respectivos cnl.
    */
    @Transactional
    public ResponseEntity<?> registerMunicipalitiesList(List<Municipalities> municipalitiesList){
        List<Municipalities> allMunicipalitiesDb = municipalitiesRepository.findAll();

        for (Municipalities m : municipalitiesList){
            m.setName(removeAccents(m.getName()));
            if (m.getAcronym().length() > 5) {
                System.out.println("O CNL muito long");
                continue;
            }
            m.setAcronym(m.getAcronym().toUpperCase().replaceAll("\\s+", ""));
        }

        Set<Municipalities> uniqueMunicipalitiesList = new HashSet<>(allMunicipalitiesDb);
        uniqueMunicipalitiesList.addAll(municipalitiesList);

        for (Municipalities m : uniqueMunicipalitiesList){
            List<Municipalities> foundMunicipalities = municipalitiesRepository.findByName(m.getName());
            if (foundMunicipalities.isEmpty()) {
                try {
                    municipalitiesRepository.save(m);
                } catch (JpaSystemException e) {
                    System.out.println("Erro ao salvar o municipio: " + m.getName());
                }
            }
        }

        return ResponseEntity.ok().body("Lista de municípios cadastrada com sucesso!");
    }


    /*
    Funcão responsável pela remoção de municípios pelo ID
     */
    public ResponseEntity<?> deleteById(String municipaliteId){
        try {
            municipalitiesRepository.deleteById(Long.valueOf(municipaliteId));
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }


    /*
     * Função para realizar a busca de um cnl com base no nome do município
     */
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

    /*
    Função para realizar a normalização das strings, retirando os acentos e transformando em lowerCase
    */
    public static String removeAccents(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("").toLowerCase();
    }
}
