package com.analistas.amesistema.amesistema.model.service;

import java.util.List;

import com.analistas.amesistema.amesistema.model.domain.Compra;

public interface ICompraService {

    public List<Compra> buscarTodos();

    public Compra buscarPorId(Long id);

    public void guardar(Compra compra);

    public void borrarPorId(Long id);
}
