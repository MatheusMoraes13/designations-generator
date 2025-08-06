package com.coelho.designation.gen.service;

import com.coelho.designation.gen.model.netbox.api.models.NetboxApiResponse;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CircuitsService {

    private final NetboxCircuitsService netboxCircuitsService;

    public ResponseEntity<?> refreshCircuits() {
        log.info("Executando a api de atualização de Circuitos.");
        String response = "";
        Gson gson = new Gson();

        try {
            response = netboxCircuitsService.getCircuits();
            NetboxApiResponse netboxApiResponse = gson.fromJson(response, NetboxApiResponse.class);
            System.out.printf("cliente: "+ netboxApiResponse.toString());
        } catch (RuntimeException e) {
            log.error("Erro ao realizar a atualização dos circuitos retornados pelo Netbox: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao realizar a atualização dos circuitos retornados pelo Netbox: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}
