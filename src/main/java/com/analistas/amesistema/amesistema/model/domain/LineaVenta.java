package com.analistas.amesistema.amesistema.model.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "linea_ventas")
public class LineaVenta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1)
    private int cantidad;

    @NotNull(message = "El precio no puede ser nulo")
    private int precioActual;

    @ManyToOne(fetch =  FetchType.LAZY)  //many a clase actual, one a la referenciada
    @JoinColumn(name = "id_producto", referencedColumnName = "id") 
    private Producto producto;

    public Double calcularSubTotal() {
        return (double) (cantidad * precioActual);
    }
}
