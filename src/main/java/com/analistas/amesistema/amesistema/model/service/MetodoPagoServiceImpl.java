package com.analistas.amesistema.amesistema.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analistas.amesistema.amesistema.model.domain.MetodoPago;
import com.analistas.amesistema.amesistema.model.repository.IMetodoPagoRepository;

@Service
public class MetodoPagoServiceImpl implements IMetodoPagoService{

    @Autowired
    IMetodoPagoRepository metodoPagoRepository;

    @Override
    public List<MetodoPago> buscarTodos() {
        return (List<MetodoPago>) metodoPagoRepository.findAll();
    }

    @Override
    public MetodoPago buscarPorId(Long id) {
        return metodoPagoRepository.findById(id).orElse(null);
    }
    
}
