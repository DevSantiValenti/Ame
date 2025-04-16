package com.analistas.amesistema.amesistema.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.analistas.amesistema.amesistema.model.domain.Producto;

public interface IProductoRepository extends CrudRepository<Producto, Long> {

    // JPQL: Java Persistence Query Language
    // Muy parecido a SQL, pero es OO
    // Equivalente en SQL a:
    // select * from productos where activo = true;
    /*
     * @Query("select p from Producto p where p.activo = true")
     * List<Producto> buscarSoloActivos();
     */

    // Con consultas SQL nativas
    /*
     * @Query(value = "select * from productos where activo = 1", nativeQuery =
     * true)
     * List<Producto> buscarSoloActivos();
     */

    // Con Query Methods
    List<Producto> findByActivoTrue();

    @Query("select p from Producto p where p.codigoBarras like %:criterio% or p.descripcion like %:criterio% and (p.stock > 0 and p.activo = true)")
    List<Producto> buscarPorVenta(@Param("criterio") String criterio);

    @Query("select p from Producto p where (p.codigoBarras like %:criterio% or p.descripcion like %:criterio%) and p.activo = true") //Se saco el parametro stock ya que no importaba si tiene o no
    List<Producto> buscarPorCompra(@Param("criterio") String criterio);

    @Query("select p from Producto p")
    List<Producto> buscarTodo(); // Esto hizo que realize la consulta a la base de datos y me traiga los objetos
                                 // en el LIST

    List<Producto> findByCategoriaId(Long categoriaId);

    List<Producto> findByColorContainingIgnoreCase(String color);
}
