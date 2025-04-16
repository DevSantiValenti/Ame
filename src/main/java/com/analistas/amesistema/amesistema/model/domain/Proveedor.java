package com.analistas.amesistema.amesistema.model.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "proveedores")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 45)
    private String nombre; // email, tel, dire

    @Size(max = 45)
    private String email;

    @Size(max = 20)
    private String telefono;

    @Size(max = 45)
    private String direccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_postal", referencedColumnName = "codPostal")
    private Localidades localidad;

    @Override
    public String toString() {
        return nombre;
    }
}
