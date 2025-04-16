package com.analistas.amesistema.amesistema.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analistas.amesistema.amesistema.model.domain.Permiso;
import com.analistas.amesistema.amesistema.model.domain.Usuario;
import com.analistas.amesistema.amesistema.model.repository.IPermisoRepository;
import com.analistas.amesistema.amesistema.model.repository.IUsuarioRepository;


@Service
public class UsuarioServiceImpl implements IUsuarioService{

    @Autowired
    IPermisoRepository permisoRepository;

    @Autowired
    IUsuarioRepository usuarioRepository;

    @Override
    public List<Usuario> buscarTodos() {
        return (List<Usuario>) usuarioRepository.findAll();
    }

    @Override
    public List<Permiso> getPermisos() {
        return (List<Permiso>) permisoRepository.findAll();
    }

    @Override
    public void guardar(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    public void borrarPorId(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public Usuario findByNombre(String nombre) {
        return usuarioRepository.findByNombre(nombre);
    }
    
}
