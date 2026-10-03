package com.nequi.franquicias.service;

import com.nequi.franquicias.dto.SolicitudNombre;
import com.nequi.franquicias.dto.SolicitudProducto;
import com.nequi.franquicias.dto.SolicitudStock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class FranquiciaServiceIntegrationTest {

    @Autowired
    private FranquiciaService service;

    @Test
    void debeObtenerElProductoConMayorStockDeCadaSucursal() {
        var franquicia = service.crearFranquicia(new SolicitudNombre("Mercado Central"));
        var medellin = service.agregarSucursal(franquicia.id(), new SolicitudNombre("Medellín"));
        var bogota = service.agregarSucursal(franquicia.id(), new SolicitudNombre("Bogotá"));

        service.agregarProducto(medellin.id(), new SolicitudProducto("Café", 20));
        service.agregarProducto(medellin.id(), new SolicitudProducto("Chocolate", 60));
        service.agregarProducto(bogota.id(), new SolicitudProducto("Panela", 15));
        service.agregarProducto(bogota.id(), new SolicitudProducto("Galletas", 40));

        var resultado = service.obtenerMayorStockPorSucursal(franquicia.id());

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(item -> item.producto())
                .containsExactly("Chocolate", "Galletas");
    }

    @Test
    void debePermitirActualizarElStock() {
        var franquicia = service.crearFranquicia(new SolicitudNombre("Tienda Uno"));
        var sucursal = service.agregarSucursal(franquicia.id(), new SolicitudNombre("Centro"));
        var producto = service.agregarProducto(sucursal.id(), new SolicitudProducto("Radio", 3));

        var actualizado = service.actualizarStock(producto.id(), new SolicitudStock(12));

        assertThat(actualizado.stock()).isEqualTo(12);
    }
}
