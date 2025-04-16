package com.analistas.amesistema.amesistema.model.repository;

import org.springframework.data.repository.CrudRepository;

import com.analistas.amesistema.amesistema.model.domain.Compra;
import com.analistas.amesistema.amesistema.model.domain.Movimiento;
import com.analistas.amesistema.amesistema.model.domain.Venta;

public interface IMovimientoRepository extends CrudRepository<Movimiento, Long>{
    
    Movimiento findByCompra (Compra compra);

    Movimiento findByVenta (Venta venta);

}
