package com.coelho.designation.gen.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CircuitType {

    IP("Link IP", "IP"),
    L2("Transporte de dados", "L2");

    private final String typeName;
    private final String typeAcronym;
}
