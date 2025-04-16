package com.analistas.amesistema.amesistema.model.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.analistas.amesistema.amesistema.model.domain.Cliente;


public interface IClienteRepository extends CrudRepository<Cliente, Long>{
    
    public boolean existsByDni(String dni);

    public List<Cliente> findByDniStartingWith(String dni);

    Cliente findByDni(String dni);

}
