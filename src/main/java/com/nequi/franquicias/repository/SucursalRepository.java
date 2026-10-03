package com.nequi.franquicias.repository;

import com.nequi.franquicias.entity.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SucursalRepository extends JpaRepository<Sucursal, Long> {
    boolean existsByFranquiciaIdAndNombreIgnoreCase(Long franquiciaId, String nombre);
}
