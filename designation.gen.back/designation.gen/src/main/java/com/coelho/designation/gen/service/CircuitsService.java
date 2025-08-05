package com.coelho.designation.gen.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class CircuitsService {

    private final NetboxCircuitsService netboxCircuitsService;

    public ResponseEntity<?> refreshCircuits() {
        log.info("Executando a api de atualização de Circuitos.");
        String response = "";
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>(){}.getType();

        try {
            response = netboxCircuitsService.getCircuits();
            Map<String, Object> jsonMap = gson.fromJson(response, type);
            System.out.println(jsonMap.toString());
        } catch (RuntimeException e) {
            log.error("Erro ao realizar a atualização dos circuitos retornados pelo Netbox: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao realizar a atualização dos circuitos retornados pelo Netbox: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}
