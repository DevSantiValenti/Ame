package com.analistas.amesistema.amesistema.model.repository;

import org.springframework.data.repository.CrudRepository;

import com.analistas.amesistema.amesistema.model.domain.Permiso;

public interface IPermisoRepository extends CrudRepository<Permiso, Long>{
    
}
