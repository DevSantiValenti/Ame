package com.analistas.amesistema.amesistema.model.service;

import java.util.List;

import com.analistas.amesistema.amesistema.model.domain.Categoria;
import com.analistas.amesistema.amesistema.model.domain.Producto;

public interface IProductoService {
    
    public List<Producto> buscarTodo();

    public List<Producto> buscarPorVenta(String criterio);

    public List<Producto> buscarPorCompra(String criterio);

    public Producto buscarPorId(Long id);

    public void guardar(Producto producto);

    public void borrarPorId(Long id);

    public List<Categoria> getCategorias();

    public List<Producto> buscarPorColor(String color);

    public List<Producto> buscarPorCategoria(Long id);

    // public List<Producto> buscarPorCategoria();
}
