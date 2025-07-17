package com.coelho.designation.gen.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "tab_municipios")
@Getter @Setter
@NoArgsConstructor
public class Municipalities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "nome município", nullable = false)
    private String name;

    @Column(name = "sigla CNL", nullable = false)
    private String acronym;
}
