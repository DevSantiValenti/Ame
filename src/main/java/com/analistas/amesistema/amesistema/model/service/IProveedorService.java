package com.analistas.amesistema.amesistema.model.service;

import java.util.List;

import com.analistas.amesistema.amesistema.model.domain.Proveedor;

public interface IProveedorService {
    public List<Proveedor> buscarTodos();

    public Proveedor buscarPorId(Long id);

    public void guardar(Proveedor proveedor);

    public void borrarPorId(Long id);
}
