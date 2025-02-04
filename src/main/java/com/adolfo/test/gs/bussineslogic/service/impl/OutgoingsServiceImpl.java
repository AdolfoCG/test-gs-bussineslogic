package com.adolfo.test.gs.bussineslogic.service.impl;

import java.util.Date;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adolfo.test.gs.bussineslogic.config.Headers;
import com.adolfo.test.gs.bussineslogic.config.RestClient;
import com.adolfo.test.gs.bussineslogic.config.Webservices;
import com.adolfo.test.gs.bussineslogic.dto.OutgoingsDto;
import com.adolfo.test.gs.bussineslogic.entities.Empleado;
import com.adolfo.test.gs.bussineslogic.entities.Movimiento;
import com.adolfo.test.gs.bussineslogic.exceptions.ForbiddenException;
import com.adolfo.test.gs.bussineslogic.exceptions.NotFoundException;
import com.adolfo.test.gs.bussineslogic.exceptions.RepositoryException;
import com.adolfo.test.gs.bussineslogic.repositories.EmpleadoRepository;
import com.adolfo.test.gs.bussineslogic.service.OutgoingsService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class OutgoingsServiceImpl implements OutgoingsService {
    @Autowired
    private EmpleadoRepository empleadoRepository;
    @Autowired
    private RestClient restClient;
    @Autowired
    private Webservices webservices;

    Headers[] headers = new Headers[] { new Headers(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) };

    @Override
    @Transactional
    public ResponseEntity<?> newOutgoings(OutgoingsDto outgoing) {
        try {
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
    
                        // consumo outgoing
                        JSONObject empleadoJson = new JSONObject();
                        empleadoJson.put("id", empleado.getId());
                        empleadoJson.put("saldoActual", empleado.getSaldoActual());

                        JSONObject metodoPost = new JSONObject();
                        metodoPost.put("monto", outgoing.getMonto());
                        metodoPost.put("empleado", empleadoJson);

                        log.info("datosPost: " + metodoPost.toString());

                        restClient.callPost(headers, webservices.webserviceOutgoings, "/new-egreso", metodoPost.toString(), 10000);
                        
                        if (restClient.getHttpStatus().value() == 200) {
                            JsonObject data = this.restClient.getJsonBody().getAsJsonObject();
                            Gson gson = new Gson();
                            String jsonResponse = gson.toJson(data);
                            return ResponseEntity.status(HttpStatus.CREATED).body(jsonResponse);
                        } else {
                            new RepositoryException("Error al consumir el servicio outgoings");
                        }
                    } else {
                        throw new ForbiddenException("¡El empleado no puede entregar la cantidad de dinero indicada!");
                    }
                } else {
                    throw new ForbiddenException("¡El empleado no cuenta con saldo en caja!");
                }
            } else {
                throw new NotFoundException("¡El empleado no existe!");
            }  
        } catch (RepositoryException e) {
            throw new RepositoryException("Error en base de datos: " + e);
        }
        
        return null;
    }
}
