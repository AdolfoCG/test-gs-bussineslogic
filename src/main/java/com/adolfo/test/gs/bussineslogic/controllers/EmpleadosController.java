package com.adolfo.test.gs.bussineslogic.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adolfo.test.gs.bussineslogic.service.EmpleadosService;

@RestController
@RequestMapping("/empleados")
public class EmpleadosController {
    @Autowired
    private EmpleadosService empleadosService;

    @GetMapping("/{id}/saldo")
    public ResponseEntity<?> saldo(@PathVariable(name = "id") Long id) {
        return empleadosService.saldo(id);
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<?> history(@PathVariable(name = "id") Long id) {
        return empleadosService.history(id);
    }
}