package com.analistas.amesistema.amesistema.model.service;

import java.util.List;

import com.analistas.amesistema.amesistema.model.domain.Permiso;
import com.analistas.amesistema.amesistema.model.domain.Usuario;

public interface IUsuarioService{

    public List<Usuario> buscarTodos();

    public List<Permiso> getPermisos();

    public Usuario buscarPorId(Long id);

    public void borrarPorId(Long id);

    public void guardar(Usuario usuario);

    public Usuario findByNombre(String nombre);
}
