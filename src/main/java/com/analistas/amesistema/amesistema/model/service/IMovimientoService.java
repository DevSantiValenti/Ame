package com.analistas.amesistema.amesistema.model.service;

import java.util.List;

import com.analistas.amesistema.amesistema.model.domain.Compra;
import com.analistas.amesistema.amesistema.model.domain.Movimiento;
import com.analistas.amesistema.amesistema.model.domain.Venta;

public interface IMovimientoService {

    public List<Movimiento> buscarTodos();

    public void guardar(Movimiento movimiento);

    public Movimiento buscarPorCompra(Compra compra);

    public Movimiento buscarPorVenta(Venta venta);

    public void borrar(Movimiento movimiento); 
}
