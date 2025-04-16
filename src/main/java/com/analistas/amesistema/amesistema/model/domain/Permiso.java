package com.analistas.amesistema.amesistema.model.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "permisos")
public class Permiso {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   
    @NotEmpty(message = "El nombre es necesario...")
    @Size(max = 20)
    private String nombre;

    @Size(max = 45)
    private String descripcion;

    @Override
    public String toString() {
        return id + " - " + nombre;
    }
}
