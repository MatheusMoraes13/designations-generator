package com.coelho.designation.gen.model.types;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ClientReportType {
    ISP("Cliente ISP"),
    DEDICATED("Cliente Link Dedicado");

    private final String typeName;
}
