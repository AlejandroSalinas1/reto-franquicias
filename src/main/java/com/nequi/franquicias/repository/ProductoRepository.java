package com.nequi.franquicias.repository;

import com.nequi.franquicias.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    boolean existsBySucursalIdAndNombreIgnoreCase(Long sucursalId, String nombre);

    @Query(value = """
            SELECT p.*
            FROM productos p
            INNER JOIN sucursales s ON s.id = p.sucursal_id
            WHERE s.franquicia_id = :franquiciaId
              AND p.id = (
                  SELECT p2.id
                  FROM productos p2
                  WHERE p2.sucursal_id = s.id
                  ORDER BY p2.stock DESC, p2.id ASC
                  LIMIT 1
              )
            ORDER BY s.id
            """, nativeQuery = true)
    List<Producto> buscarMayorStockPorSucursal(@Param("franquiciaId") Long franquiciaId);
}
