package com.analistas.amesistema.amesistema.model.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "localidades")
public class Localidades {
    
    @Id
    // @Column(name = "cod_postal")
    private Long codPostal;

    @Column(name = "provincia", length = 45)
    private String provincia;

    @Column(name = "localidad", length = 45)
    private String localidad;

    @Override
    public String toString() {
        return "CP: " + codPostal + ", " + localidad + ", " + provincia;
    }
}
