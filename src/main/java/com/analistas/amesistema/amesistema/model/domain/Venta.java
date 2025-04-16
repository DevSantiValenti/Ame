package com.analistas.amesistema.amesistema.model.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ventas")
public class Venta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nro_factura")
    private Long numero;

    private LocalDateTime fechaHora;

    private String descripcionGral;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", referencedColumnName = "id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metodo_pago", referencedColumnName = "id")
    private MetodoPago metodoPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", referencedColumnName = "id")
    private Cliente cliente;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_venta")
    private List<LineaVenta> lineas;

    @Column(name = "activo", columnDefinition = "boolean default 1")
    private boolean activo;

    @Column(name = "total")
    private Double total;

    public Venta() {
        lineas = new ArrayList<>();
        fechaHora = LocalDateTime.now();
        descripcionGral = "ninguna";
        activo = true;
    }

    public Double calcularTotal() {
        Double total = 0.0;

        for (LineaVenta linea : lineas) {
            //Obtener el subtotal y acumularlos
            total += linea.calcularSubTotal();
        }
        this.total = total;

        return total;
    }

    public void addLinea(LineaVenta linea) {
        lineas.add(linea);
    }
}
