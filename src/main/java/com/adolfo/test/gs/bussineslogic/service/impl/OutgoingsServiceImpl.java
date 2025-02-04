package com.adolfo.test.gs.bussineslogic.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adolfo.test.gs.bussineslogic.dto.OutgoingsDto;
import com.adolfo.test.gs.bussineslogic.entities.Empleado;
import com.adolfo.test.gs.bussineslogic.entities.Movimiento;
import com.adolfo.test.gs.bussineslogic.exceptions.ForbiddenException;
import com.adolfo.test.gs.bussineslogic.exceptions.NotFoundException;
import com.adolfo.test.gs.bussineslogic.repositories.EmpleadoRepository;
import com.adolfo.test.gs.bussineslogic.repositories.MovimientoRepository;
import com.adolfo.test.gs.bussineslogic.service.OutgoingsService;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class OutgoingsServiceImpl implements OutgoingsService {
    @Autowired
    private MovimientoRepository movimientoRepository;
    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Override
    @Transactional
    public ResponseEntity<?> newOutgoings(OutgoingsDto outgoing) {
        Optional<Empleado> optionalEmpleado = empleadoRepository.findByNombre(outgoing.getEmpleado());
        Movimiento egreso = new Movimiento();

        if (optionalEmpleado.isPresent()) {
            Empleado empleado = optionalEmpleado.orElseThrow();
            float saldoActual = empleado.getSaldoActual();

            if (saldoActual != 0) {
                float cuenta = saldoActual - outgoing.getMonto();

                if (cuenta >= 0) {
                    egreso.setTipoMovimiento(2);
                    egreso.setMonto(outgoing.getMonto()); 
                    egreso.setFecha(new Date());
                    egreso.setEmpleado(empleado);

                    empleado.setSaldoActual(cuenta);
                    empleadoRepository.save(empleado);

                    return ResponseEntity.status(HttpStatus.CREATED).body(movimientoRepository.save(egreso));
                } else {
                    throw new ForbiddenException("¡El empleado no puede entregar la cantidad de dinero indicada!");
                }
            } else {
                throw new ForbiddenException("¡El empleado no puede entregar la cantidad de dinero indicada!");
            }
        } else {
            throw new NotFoundException("¡El empleado no existe!");
        }
    }
}
