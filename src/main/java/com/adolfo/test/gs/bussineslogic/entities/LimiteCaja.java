package com.adolfo.test.gs.bussineslogic.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "limite_caja")
@Getter
@Setter
public class LimiteCaja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "monto_maximo")
    private float montoMaximo;

    @ManyToOne
    @JoinColumn(name = "id_empleado")
    private Empleado empleado;
}