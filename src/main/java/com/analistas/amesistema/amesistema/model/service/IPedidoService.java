package com.analistas.amesistema.amesistema.model.service;

import java.util.List;


import com.analistas.amesistema.amesistema.model.domain.Pedido;

public interface IPedidoService {

    

    public void guardar(Pedido pedido);

    public List<Pedido> buscarTodos();

    public Pedido buscarPorVentaId(Long id);

    public void borrar(Pedido pedido);


}
