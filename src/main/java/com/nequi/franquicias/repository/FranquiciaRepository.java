package com.nequi.franquicias.repository;

import com.nequi.franquicias.entity.Franquicia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FranquiciaRepository extends JpaRepository<Franquicia, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}
