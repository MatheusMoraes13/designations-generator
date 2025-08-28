package com.coelho.designation.gen.model;

import com.coelho.designation.gen.types.CircuitType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Designation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "CLN circuito", nullable = false)
    private String CNL;

    @Column(name = "Id Contrato", nullable = false)
    private String ContractId;

    @Column(name = "Tipo do circuito", nullable = false)
    private CircuitType circuitType;
}
