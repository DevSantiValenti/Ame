package com.analistas.amesistema.amesistema.model.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "linea_compras")
public class LineaCompra {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private int cantidad;

    // private int precioActual;

    @ManyToOne(fetch = FetchType.LAZY) //many a clase actual, one a la referenciada
    @JoinColumn(name = "id_producto")
    private Producto producto;

    //Métodos:
    // public Double calcularSubtotal() {
    //     return (double) (cantidad * precioActual);
    // }

}
