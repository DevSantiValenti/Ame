package com.analistas.amesistema.amesistema.model.service;

import java.util.List;

import com.analistas.amesistema.amesistema.model.domain.Categoria;

public interface ICategoriaService {

    public List<Categoria> buscarTodo();

    public void guardar(Categoria categoria);

    public Categoria buscarPorId(Long id);

    public void borrarPorId(Long id);
}
