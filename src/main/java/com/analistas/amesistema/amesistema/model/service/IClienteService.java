package com.analistas.amesistema.amesistema.model.service;

import java.util.List;

import com.analistas.amesistema.amesistema.model.domain.Cliente;
import com.analistas.amesistema.amesistema.model.domain.Permiso;

public interface IClienteService {
    
    public List<Cliente> buscarTodos();

    public Cliente buscarPorId(Long id);

    public void guardar(Cliente cliente);

    public Permiso getPermiso(Long id); 

    public boolean existeDni(String dni);

    public Cliente findByDni(String dni);
}
