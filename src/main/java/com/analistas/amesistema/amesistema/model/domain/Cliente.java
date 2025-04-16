package com.analistas.amesistema.amesistema.model.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "EL nombre es requerido...")
    @Size(max = 45)
    private String nombre;

    @NotEmpty(message = "EL DNI es requerido...")
    @Size(max = 45)
    private String dni;

    @Email(message = "El email es obligatorio...")
    @Size(max = 45)
    private String email;

    @Column(name = "activo", columnDefinition = "boolean default 1")
    private boolean activo;

    @NotEmpty(message = "La clave es requerida...")
    @Size(max = 70)
    private String clave;

    @NotEmpty(message = "La direccion es requerida...")
    @Size(max = 45)
    private String direccion;

    @NotEmpty(message = "EL telefono es requerido...")
    @Size(max = 45)
    private String telefono;

    private LocalDateTime fechaRegistro;

    @NotNull(message = "La fecha de nacimiento es requerida...")
    @Past(message = "La fecha de nacimiento debe ser en el pasado...")
    private LocalDate fechaNacimiento;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_postal", referencedColumnName = "codPostal")
    private Localidades localidad;

    // @NotNull(message = "El permiso es requerido...")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_permiso", referencedColumnName = "id")
    private Permiso permiso;

    @PrePersist
    public void PrePersist() {
        activo = true;
    }

    @Override
    public String toString() {
        return dni + " - " + nombre;
    }

}
