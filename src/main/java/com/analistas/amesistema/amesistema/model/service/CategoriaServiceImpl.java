package com.analistas.amesistema.amesistema.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analistas.amesistema.amesistema.model.domain.Categoria;
import com.analistas.amesistema.amesistema.model.repository.ICategoriaRepository;

@Service
public class CategoriaServiceImpl implements ICategoriaService{

    @Autowired
    ICategoriaRepository categoriaRepository;

    @Override
    public List<Categoria> buscarTodo() {
         return (List<Categoria>) categoriaRepository.findAll();
    }

    @Override
    public void guardar(Categoria categoria) {
        categoriaRepository.save(categoria);
    }

    @Override
    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id).orElse(null);
    }

    @Override
    public void borrarPorId(Long id) {
        categoriaRepository.deleteById(id);
    }

}
