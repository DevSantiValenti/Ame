package com.analistas.amesistema.amesistema.model.repository;

import org.springframework.data.repository.CrudRepository;

import com.analistas.amesistema.amesistema.model.domain.MetodoPago;

public interface IMetodoPagoRepository  extends CrudRepository<MetodoPago, Long>{
    
}
