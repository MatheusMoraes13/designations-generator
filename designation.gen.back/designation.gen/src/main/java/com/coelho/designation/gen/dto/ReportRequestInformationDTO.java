package com.coelho.designation.gen.dto;

public record ReportRequestInformationDTO(
        String clientName,
        String initialDate,
        String finalDate,
        String clientLink,
        String circuitDesignation,
        String valueMb,
        String circuitVlan
) {
}
