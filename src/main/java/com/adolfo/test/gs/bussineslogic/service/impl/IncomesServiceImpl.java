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

import com.adolfo.test.gs.bussineslogic.service.IncomesService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.adolfo.test.gs.bussineslogic.config.Headers;
import com.adolfo.test.gs.bussineslogic.config.RestClient;
import com.adolfo.test.gs.bussineslogic.config.Webservices;
import com.adolfo.test.gs.bussineslogic.dto.IncomesDto;
import com.adolfo.test.gs.bussineslogic.entities.Empleado;
import com.adolfo.test.gs.bussineslogic.entities.LimiteCaja;
import com.adolfo.test.gs.bussineslogic.entities.Movimiento;
import com.adolfo.test.gs.bussineslogic.exceptions.ForbiddenException;
import com.adolfo.test.gs.bussineslogic.exceptions.NotFoundException;
import com.adolfo.test.gs.bussineslogic.exceptions.RepositoryException;
import com.adolfo.test.gs.bussineslogic.repositories.EmpleadoRepository;
import com.adolfo.test.gs.bussineslogic.repositories.LimiteCajaRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class IncomesServiceImpl implements IncomesService {
    @Autowired
    private EmpleadoRepository empleadoRepository;
    @Autowired
    private LimiteCajaRepository limiteCajaRepository;
    @Autowired
    private RestClient restClient;
    @Autowired
    private Webservices webservices;

    Headers[] headers = new Headers[] { new Headers(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) };

    @Override
    @Transactional
    public ResponseEntity<?> newIncome(IncomesDto income) {
        try {
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
    
                        // consumo incomes
                        JSONObject empleadoJson = new JSONObject();
                        empleadoJson.put("id", empleado.getId());
                        empleadoJson.put("saldoActual", empleado.getSaldoActual());

                        JSONObject metodoPost = new JSONObject();
                        metodoPost.put("monto", income.getMonto());
                        metodoPost.put("empleado", empleadoJson);

                        log.info("datosPost: " + metodoPost.toString());

                        restClient.callPost(headers, webservices.webserviceIncomes, "/new-ingreso", metodoPost.toString(), 10000);
                        
                        if (restClient.getHttpStatus().value() == 200) {
                            JsonObject data = this.restClient.getJsonBody().getAsJsonObject();
                            Gson gson = new Gson();
                            String jsonResponse = gson.toJson(data);
                            return ResponseEntity.status(HttpStatus.CREATED).body(jsonResponse);
                        } else {
                            new RepositoryException("Error al consumir el servicio incomes");
                        }
                    } else {
                        throw new ForbiddenException("¡El empleado no puede recibir más ingresos sin antes realizar un corte!");
                    }
                }
            } else {
                throw new NotFoundException("¡El empleado no existe!");
            }
    
            return ResponseEntity.status(HttpStatus.OK).build();
            
        } catch (RepositoryException e) {
            throw new RepositoryException("Error en base de datos: " + e);
        }
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
