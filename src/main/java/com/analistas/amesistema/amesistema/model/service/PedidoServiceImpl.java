package com.analistas.amesistema.amesistema.model.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analistas.amesistema.amesistema.model.domain.Pedido;
import com.analistas.amesistema.amesistema.model.repository.IPedidoRepository;

@Service
public class PedidoServiceImpl implements IPedidoService{

    @Autowired
    IPedidoRepository pedidoRepository;

    public void guardar(Pedido pedido) {
        pedidoRepository.save(pedido);
    }

    @Override
    public List<Pedido> buscarTodos() {
        return (List<Pedido>) pedidoRepository.findAll();
    }

    public void borrar(Pedido pedido) {
        pedidoRepository.delete(pedido);
    }

    @Override
    public Pedido buscarPorVentaId(Long id) {
        return pedidoRepository.findByVentaId(id);
    }

}
