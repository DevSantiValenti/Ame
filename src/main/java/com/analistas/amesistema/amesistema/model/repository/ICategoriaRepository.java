package com.analistas.amesistema.amesistema.model.repository;

import org.springframework.data.repository.CrudRepository;

import com.analistas.amesistema.amesistema.model.domain.Categoria;

public interface ICategoriaRepository extends CrudRepository<Categoria, Long>{
    
}
