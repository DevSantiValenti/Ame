package com.analistas.amesistema.amesistema.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.analistas.amesistema.amesistema.model.domain.Categoria;
import com.analistas.amesistema.amesistema.model.domain.Producto;
import com.analistas.amesistema.amesistema.model.repository.ICategoriaRepository;
import com.analistas.amesistema.amesistema.model.repository.IProductoRepository;

@Service
public class ProductoServiceImpl implements IProductoService{

    @Autowired
    IProductoRepository productoRepository;

    @Autowired
    ICategoriaRepository categoriaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarTodo() {
        return (List<Producto>) productoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarPorVenta(String criterio) {
        return productoRepository.buscarPorVenta(criterio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarPorCompra(String criterio) {
        return productoRepository.buscarPorCompra(criterio);
    }

    @Override
    @Transactional(readOnly = true)
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void guardar(Producto producto) {
        productoRepository.save(producto);
    }

    @Override
    @Transactional
    public void borrarPorId(Long id) {
        productoRepository.deleteById(id);
    }

    @Override
    public List<Categoria> getCategorias() {
        return (List<Categoria>) categoriaRepository.findAll();
    }

    @Override
    public List<Producto> buscarPorCategoria(Long id) {
        return productoRepository.findByCategoriaId(id);
    }

    @Override
    public List<Producto> buscarPorColor(String color) {
        return productoRepository.findByColorContainingIgnoreCase(color);
    }
    
}
