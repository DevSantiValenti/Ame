package com.analistas.amesistema.amesistema.model.service;

import java.util.List;

import com.analistas.amesistema.amesistema.model.domain.Venta;

public interface IVentaService {
    
    public List<Venta> buscarTodo();

    public Venta buscarPorId(Long id);

    public void guardar(Venta venta);

    public void borrarPorId(Long id);
}
