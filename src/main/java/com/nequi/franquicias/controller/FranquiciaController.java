package com.nequi.franquicias.controller;

import com.nequi.franquicias.dto.*;
import com.nequi.franquicias.service.FranquiciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FranquiciaController {

    private final FranquiciaService franquiciaService;

    public FranquiciaController(FranquiciaService franquiciaService) {
        this.franquiciaService = franquiciaService;
    }

    @PostMapping("/franquicias")
    public ResponseEntity<RespuestaFranquicia> crearFranquicia(
            @Valid @RequestBody SolicitudNombre solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(franquiciaService.crearFranquicia(solicitud));
    }

    @PostMapping("/franquicias/{franquiciaId}/sucursales")
    public ResponseEntity<RespuestaSucursal> agregarSucursal(
            @PathVariable Long franquiciaId,
            @Valid @RequestBody SolicitudNombre solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(franquiciaService.agregarSucursal(franquiciaId, solicitud));
    }

    @PostMapping("/sucursales/{sucursalId}/productos")
    public ResponseEntity<RespuestaProducto> agregarProducto(
            @PathVariable Long sucursalId,
            @Valid @RequestBody SolicitudProducto solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(franquiciaService.agregarProducto(sucursalId, solicitud));
    }

    @DeleteMapping("/sucursales/{sucursalId}/productos/{productoId}")
    public ResponseEntity<Void> eliminarProducto(
            @PathVariable Long sucursalId,
            @PathVariable Long productoId) {
        franquiciaService.eliminarProducto(sucursalId, productoId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/productos/{productoId}/stock")
    public RespuestaProducto actualizarStock(
            @PathVariable Long productoId,
            @Valid @RequestBody SolicitudStock solicitud) {
        return franquiciaService.actualizarStock(productoId, solicitud);
    }

    @GetMapping("/franquicias/{franquiciaId}/productos-mayor-stock")
    public List<ProductoMayorStock> obtenerMayorStockPorSucursal(
            @PathVariable Long franquiciaId) {
        return franquiciaService.obtenerMayorStockPorSucursal(franquiciaId);
    }

    @PatchMapping("/franquicias/{franquiciaId}/nombre")
    public RespuestaFranquicia actualizarNombreFranquicia(
            @PathVariable Long franquiciaId,
            @Valid @RequestBody SolicitudNombre solicitud) {
        return franquiciaService.actualizarNombreFranquicia(franquiciaId, solicitud);
    }

    @PatchMapping("/productos/{productoId}/nombre")
    public RespuestaProducto actualizarNombreProducto(
            @PathVariable Long productoId,
            @Valid @RequestBody SolicitudNombre solicitud) {
        return franquiciaService.actualizarNombreProducto(productoId, solicitud);
    }
}
