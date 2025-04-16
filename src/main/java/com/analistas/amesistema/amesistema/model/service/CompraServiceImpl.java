package com.analistas.amesistema.amesistema.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.analistas.amesistema.amesistema.model.domain.Compra;
import com.analistas.amesistema.amesistema.model.domain.Movimiento;
import com.analistas.amesistema.amesistema.model.repository.ICompraRepository;
import com.analistas.amesistema.amesistema.model.repository.IProductoRepository;

@Service
public class CompraServiceImpl implements ICompraService{

    @Autowired
    ICompraRepository compraRepository;

    @Autowired
    IProductoRepository productoRepository;

    @Autowired
    IMovimientoService movimientoService; 

    @Override
    public List<Compra> buscarTodos() {
        return (List<Compra>) compraRepository.findAll();
    }

    @Override
    @Transactional
    public Compra buscarPorId(Long id) {
        return compraRepository.findById(id).orElse(null);
    }

    @Override
    public void guardar(Compra compra) {
        compraRepository.save(compra);
    }

    @Override
    public void borrarPorId(Long id) {

        Compra compra = compraRepository.findById(id).orElse(null);

        //Eliminamos los movimientos asociados a la compra
        Movimiento movimiento = movimientoService.buscarPorCompra(compra);
        if (movimiento != null) {
            movimientoService.borrar(movimiento);
        }

        //Borrar la compra
        compraRepository.deleteById(id);
    }

}
