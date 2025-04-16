package com.analistas.amesistema.amesistema.model.service;

import java.util.List;


import com.analistas.amesistema.amesistema.model.domain.MetodoPago;

public interface IMetodoPagoService{
    
    public List<MetodoPago> buscarTodos();

    public MetodoPago buscarPorId(Long id);

}
