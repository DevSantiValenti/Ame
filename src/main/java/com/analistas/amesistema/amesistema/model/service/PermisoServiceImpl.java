package com.analistas.amesistema.amesistema.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analistas.amesistema.amesistema.model.domain.Permiso;
import com.analistas.amesistema.amesistema.model.repository.IPermisoRepository;

@Service
public class PermisoServiceImpl implements IPermisoService{


    @Autowired
    IPermisoRepository permisoRepository;

    @Override
    public Permiso buscarPorId(Long id) {
        return permisoRepository.findById(id).orElse(null);
    }

}
