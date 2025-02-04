package com.adolfo.test.gs.bussineslogic.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adolfo.test.gs.bussineslogic.service.IncomesService;
import com.adolfo.test.gs.bussineslogic.dto.IncomesDto;
import com.adolfo.test.gs.bussineslogic.entities.Empleado;
import com.adolfo.test.gs.bussineslogic.entities.LimiteCaja;
import com.adolfo.test.gs.bussineslogic.entities.Movimiento;
import com.adolfo.test.gs.bussineslogic.exceptions.ForbiddenException;
import com.adolfo.test.gs.bussineslogic.exceptions.NotFoundException;
import com.adolfo.test.gs.bussineslogic.repositories.EmpleadoRepository;
import com.adolfo.test.gs.bussineslogic.repositories.LimiteCajaRepository;
import com.adolfo.test.gs.bussineslogic.repositories.MovimientoRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class IncomesServiceImpl implements IncomesService {
    @Autowired
    private MovimientoRepository movimientoRepository;
    @Autowired
    private EmpleadoRepository empleadoRepository;
    @Autowired
    private LimiteCajaRepository limiteCajaRepository;


    @Override
    @Transactional
    public ResponseEntity<?> newIncome(IncomesDto income) {
        Optional<Empleado> optionalEmpleado = empleadoRepository.findByNombre(income.getEmpleado());
        Movimiento ingreso = new Movimiento();

        if (optionalEmpleado.isPresent()) {
            Empleado empleado = optionalEmpleado.orElseThrow();
            float limiteEmpleado = limiteEmpleadoFunc(empleado);

            if (limiteEmpleado != 0) {
                float saldoActual = empleado.getSaldoActual();
                float cuenta = saldoActual + income.getMonto();

                if (cuenta <= limiteEmpleado) {
                    ingreso.setTipoMovimiento(1);
                    ingreso.setMonto(income.getMonto()); 
                    ingreso.setFecha(new Date());
                    ingreso.setEmpleado(empleado);

                    empleado.setSaldoActual(cuenta);
                    empleadoRepository.save(empleado);

                    return ResponseEntity.status(HttpStatus.CREATED).body(movimientoRepository.save(ingreso));
                } else {
                    throw new ForbiddenException("¡El empleado no puede recibir más ingresos sin antes realizar un corte!");
                }
            }
        } else {
            throw new NotFoundException("¡El empleado no existe!");
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private float limiteEmpleadoFunc(Empleado empleado) {
        Optional<LimiteCaja> limiteCaja = limiteCajaRepository.findByEmpleado(empleado);
        float limiteEmpleado = 0;
            
            if (limiteCaja.isPresent()) {
                limiteEmpleado = limiteCaja.get().getMontoMaximo();
            } else {
                throw new NotFoundException("¡El limite de caja del empleado no existe!");
            }

        return limiteEmpleado;
    }
}
