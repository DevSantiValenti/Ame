package com.analistas.amesistema.amesistema.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analistas.amesistema.amesistema.model.domain.Compra;
import com.analistas.amesistema.amesistema.model.domain.Movimiento;
import com.analistas.amesistema.amesistema.model.domain.Venta;
import com.analistas.amesistema.amesistema.model.repository.IMovimientoRepository;

@Service
public class MovimientoServiceImpl implements IMovimientoService{

    @Autowired
    IMovimientoRepository movimientoRepository;

    @Override
    public List<Movimiento> buscarTodos() {
        return (List<Movimiento>) movimientoRepository.findAll();
    }

    @Override
    public void guardar(Movimiento movimiento) {
        movimientoRepository.save(movimiento);
    }

    @Override
    public Movimiento buscarPorCompra(Compra compra) {
        return movimientoRepository.findByCompra(compra);
    }

    @Override
    public Movimiento buscarPorVenta(Venta venta) {
        return movimientoRepository.findByVenta(venta);
    }

    @Override
    public void borrar(Movimiento movimiento){
        movimientoRepository.delete(movimiento);
    }

    

}
