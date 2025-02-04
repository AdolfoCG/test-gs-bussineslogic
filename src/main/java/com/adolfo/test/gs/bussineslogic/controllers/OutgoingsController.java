package com.adolfo.test.gs.bussineslogic.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adolfo.test.gs.bussineslogic.dto.OutgoingsDto;
import com.adolfo.test.gs.bussineslogic.service.OutgoingsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/movimientos")
public class OutgoingsController {
    @Autowired
    private OutgoingsService outgoingsService;

    @PostMapping("/egreso")
    public ResponseEntity<?> newOutgoing(@Valid @RequestBody OutgoingsDto outgoings) {
        return outgoingsService.newOutgoings(outgoings);
    }
}