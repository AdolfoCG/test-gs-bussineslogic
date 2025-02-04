package com.adolfo.test.gs.bussineslogic.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adolfo.test.gs.bussineslogic.entities.Empleado;
import com.adolfo.test.gs.bussineslogic.entities.Movimiento;
import com.adolfo.test.gs.bussineslogic.exceptions.NotFoundException;
import com.adolfo.test.gs.bussineslogic.exceptions.RepositoryException;
import com.adolfo.test.gs.bussineslogic.repositories.EmpleadoRepository;
import com.adolfo.test.gs.bussineslogic.repositories.MovimientoRepository;
import com.adolfo.test.gs.bussineslogic.service.EmpleadosService;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class EmpleadosServiceImpl implements EmpleadosService {
    @Autowired
    private EmpleadoRepository empleadoRepository;
    @Autowired
    private MovimientoRepository movimientoRepository;

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<?> saldo(Long id) {
        try {
            Optional<Empleado> empleado = empleadoRepository.findById(id);
    
            if (empleado.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(Collections.singletonMap("saldoActual", empleado.get().getSaldoActual()));
            } else {
                throw new NotFoundException("¡El empleado no existe!");
            }
        } catch (RepositoryException e) {
            throw new RepositoryException("Error en base de datos: " + e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<?> history(Long id) {
        try {
            Optional<Empleado> empleado = empleadoRepository.findById(id);
    
            if (!empleado.isPresent()) {
                throw new NotFoundException("¡El empleado no existe!");
            }
    
            List<Movimiento> movimientos = movimientoRepository.findAllByIdEmpleado(empleado.get());
    
            if (movimientos.isEmpty()) {
                throw new NotFoundException("No se encontraron movimientos");
            }
    
            return ResponseEntity.status(HttpStatus.OK).body(movimientos);
        } catch (RepositoryException e) {
            throw new RepositoryException("Error en base de datos: " + e);
        }
    }

}