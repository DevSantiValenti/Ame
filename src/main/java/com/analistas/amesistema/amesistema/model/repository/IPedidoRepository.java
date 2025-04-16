package com.analistas.amesistema.amesistema.model.repository;


import org.springframework.data.repository.CrudRepository;

import com.analistas.amesistema.amesistema.model.domain.Pedido;

public interface IPedidoRepository extends CrudRepository<Pedido, Long>{
    Pedido findByVentaId(Long ventaId);
}
