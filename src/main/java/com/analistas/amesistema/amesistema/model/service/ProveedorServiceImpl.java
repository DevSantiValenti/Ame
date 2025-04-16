package com.analistas.amesistema.amesistema.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analistas.amesistema.amesistema.model.domain.Proveedor;
import com.analistas.amesistema.amesistema.model.repository.IProveedorRepository;

@Service
public class ProveedorServiceImpl implements IProveedorService{

    @Autowired
    IProveedorRepository proveedorRepository;

    @Override
    public List<Proveedor> buscarTodos() {
        return (List<Proveedor>) proveedorRepository.findAll();
    }

    @Override
    public Proveedor buscarPorId(Long id) {
        return proveedorRepository.findById(id).orElse(null);
    }

    @Override
    public void guardar(Proveedor proveedor) {
        proveedorRepository.save(proveedor);
    }

    @Override
    public void borrarPorId(Long id) {
        proveedorRepository.deleteById(id);
    }

}
