package com.nequi.franquicias.dto;

public record ProductoMayorStock(
        Long sucursalId,
        String sucursal,
        Long productoId,
        String producto,
        Integer stock) {
}
