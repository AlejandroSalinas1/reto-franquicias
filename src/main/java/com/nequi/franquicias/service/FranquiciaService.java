package com.nequi.franquicias.service;

import com.nequi.franquicias.dto.*;
import com.nequi.franquicias.entity.Franquicia;
import com.nequi.franquicias.entity.Producto;
import com.nequi.franquicias.entity.Sucursal;
import com.nequi.franquicias.exception.RecursoNoEncontradoException;
import com.nequi.franquicias.exception.ReglaNegocioException;
import com.nequi.franquicias.repository.FranquiciaRepository;
import com.nequi.franquicias.repository.ProductoRepository;
import com.nequi.franquicias.repository.SucursalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FranquiciaService {

    private final FranquiciaRepository franquiciaRepository;
    private final SucursalRepository sucursalRepository;
    private final ProductoRepository productoRepository;

    public FranquiciaService(FranquiciaRepository franquiciaRepository,
                             SucursalRepository sucursalRepository,
                             ProductoRepository productoRepository) {
        this.franquiciaRepository = franquiciaRepository;
        this.sucursalRepository = sucursalRepository;
        this.productoRepository = productoRepository;
    }

    public RespuestaFranquicia crearFranquicia(SolicitudNombre solicitud) {
        String nombre = solicitud.nombre().trim();
        if (franquiciaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ReglaNegocioException("Ya existe una franquicia con ese nombre");
        }

        Franquicia franquicia = new Franquicia();
        franquicia.setNombre(nombre);
        franquicia = franquiciaRepository.save(franquicia);
        return new RespuestaFranquicia(franquicia.getId(), franquicia.getNombre());
    }

    public RespuestaSucursal agregarSucursal(Long franquiciaId, SolicitudNombre solicitud) {
        Franquicia franquicia = buscarFranquicia(franquiciaId);
        String nombre = solicitud.nombre().trim();

        if (sucursalRepository.existsByFranquiciaIdAndNombreIgnoreCase(franquiciaId, nombre)) {
            throw new ReglaNegocioException("Ya existe una sucursal con ese nombre en la franquicia");
        }

        Sucursal sucursal = new Sucursal();
        sucursal.setNombre(nombre);
        sucursal.setFranquicia(franquicia);
        sucursal = sucursalRepository.save(sucursal);
        return new RespuestaSucursal(sucursal.getId(), sucursal.getNombre(), franquicia.getId());
    }

    public RespuestaProducto agregarProducto(Long sucursalId, SolicitudProducto solicitud) {
        Sucursal sucursal = buscarSucursal(sucursalId);
        String nombre = solicitud.nombre().trim();

        if (productoRepository.existsBySucursalIdAndNombreIgnoreCase(sucursalId, nombre)) {
            throw new ReglaNegocioException("Ya existe un producto con ese nombre en la sucursal");
        }

        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setStock(solicitud.stock());
        producto.setSucursal(sucursal);
        producto = productoRepository.save(producto);
        return convertirProducto(producto);
    }

    public void eliminarProducto(Long sucursalId, Long productoId) {
        Producto producto = buscarProducto(productoId);
        if (!producto.getSucursal().getId().equals(sucursalId)) {
            throw new RecursoNoEncontradoException("El producto no pertenece a la sucursal indicada");
        }
        productoRepository.delete(producto);
    }

    public RespuestaProducto actualizarStock(Long productoId, SolicitudStock solicitud) {
        Producto producto = buscarProducto(productoId);
        producto.setStock(solicitud.stock());
        return convertirProducto(producto);
    }

    @Transactional(readOnly = true)
    public List<ProductoMayorStock> obtenerMayorStockPorSucursal(Long franquiciaId) {
        buscarFranquicia(franquiciaId);
        return productoRepository.buscarMayorStockPorSucursal(franquiciaId).stream()
                .map(producto -> new ProductoMayorStock(
                        producto.getSucursal().getId(),
                        producto.getSucursal().getNombre(),
                        producto.getId(),
                        producto.getNombre(),
                        producto.getStock()))
                .toList();
    }

    public RespuestaFranquicia actualizarNombreFranquicia(Long franquiciaId, SolicitudNombre solicitud) {
        Franquicia franquicia = buscarFranquicia(franquiciaId);
        String nombre = solicitud.nombre().trim();

        if (franquiciaRepository.existsByNombreIgnoreCase(nombre)
                && !franquicia.getNombre().equalsIgnoreCase(nombre)) {
            throw new ReglaNegocioException("Ya existe una franquicia con ese nombre");
        }

        franquicia.setNombre(nombre);
        return new RespuestaFranquicia(franquicia.getId(), franquicia.getNombre());
    }

    public RespuestaProducto actualizarNombreProducto(Long productoId, SolicitudNombre solicitud) {
        Producto producto = buscarProducto(productoId);
        String nombre = solicitud.nombre().trim();
        Long sucursalId = producto.getSucursal().getId();

        if (productoRepository.existsBySucursalIdAndNombreIgnoreCase(sucursalId, nombre)
                && !producto.getNombre().equalsIgnoreCase(nombre)) {
            throw new ReglaNegocioException("Ya existe un producto con ese nombre en la sucursal");
        }

        producto.setNombre(nombre);
        return convertirProducto(producto);
    }

    private Franquicia buscarFranquicia(Long id) {
        return franquiciaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la franquicia con id " + id));
    }

    private Sucursal buscarSucursal(Long id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la sucursal con id " + id));
    }

    private Producto buscarProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto con id " + id));
    }

    private RespuestaProducto convertirProducto(Producto producto) {
        return new RespuestaProducto(
                producto.getId(), producto.getNombre(), producto.getStock(), producto.getSucursal().getId());
    }
}
