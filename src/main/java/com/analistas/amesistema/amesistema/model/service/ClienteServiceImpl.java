package com.analistas.amesistema.amesistema.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analistas.amesistema.amesistema.model.domain.Cliente;
import com.analistas.amesistema.amesistema.model.domain.Permiso;
import com.analistas.amesistema.amesistema.model.repository.IClienteRepository;
import com.analistas.amesistema.amesistema.model.repository.IPermisoRepository;

@Service
public class ClienteServiceImpl implements IClienteService{

    @Autowired
    IClienteRepository clienteRepository;

    @Autowired
    IPermisoRepository permisoRepository;

    @Override
    public void guardar(Cliente cliente) {

        clienteRepository.save(cliente);

    }

    @Override
    public Permiso getPermiso(Long id) {
        return (Permiso) permisoRepository.findAll();
    }

    @Override
    public boolean existeDni(String dni) {
        return clienteRepository.existsByDni(dni);
    }

    @Override
    public List<Cliente> buscarTodos() {
        return (List<Cliente>) clienteRepository.findAll();
    }

    @Override
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    @Override
    public Cliente findByDni(String dni) {
        return clienteRepository.findByDni(dni);
    }
    
}
