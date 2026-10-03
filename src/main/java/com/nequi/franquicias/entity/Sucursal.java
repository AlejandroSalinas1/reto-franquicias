package com.nequi.franquicias.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sucursales", uniqueConstraints =
        @UniqueConstraint(columnNames = {"franquicia_id", "nombre"}))
public class Sucursal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "franquicia_id", nullable = false)
    private Franquicia franquicia;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Franquicia getFranquicia() { return franquicia; }
    public void setFranquicia(Franquicia franquicia) { this.franquicia = franquicia; }
}
