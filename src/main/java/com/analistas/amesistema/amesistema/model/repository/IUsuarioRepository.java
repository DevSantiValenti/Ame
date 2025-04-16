package com.analistas.amesistema.amesistema.model.repository;

import org.springframework.data.repository.CrudRepository;

import com.analistas.amesistema.amesistema.model.domain.Usuario;

public interface IUsuarioRepository extends CrudRepository<Usuario, Long>{
    Usuario findByNombre(String nombre);
}
