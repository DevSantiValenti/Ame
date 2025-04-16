package com.analistas.amesistema.amesistema.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.analistas.amesistema.amesistema.model.domain.Movimiento;
import com.analistas.amesistema.amesistema.model.domain.Venta;
import com.analistas.amesistema.amesistema.model.repository.IVentaRepository;

@Service
public class VentaServiceImpl implements IVentaService{

    @Autowired
    IVentaRepository ventaRepository;

    @Autowired
    IMovimientoService movimientoService;

    @Override
    public List<Venta> buscarTodo() {
        return (List<Venta>) ventaRepository.findAll();
    }

    @Override
    public Venta buscarPorId(Long id) {
        return ventaRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void guardar(Venta venta) {
        ventaRepository.save(venta);
    }

    @Override
    public void borrarPorId(Long id) {
        Venta venta = ventaRepository.findById(id).orElse(null);

        //Eliminamos los movimientos asociados a la venta
        Movimiento movimiento = movimientoService.buscarPorVenta(venta);
        if (movimiento != null) {
            movimientoService.borrar(movimiento);
        }

        //Borrar la venta
        ventaRepository.deleteById(id);
    }
    
}
