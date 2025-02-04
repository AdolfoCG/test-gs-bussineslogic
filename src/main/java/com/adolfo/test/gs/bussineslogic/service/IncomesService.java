package com.adolfo.test.gs.bussineslogic.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.adolfo.test.gs.bussineslogic.dto.IncomesDto;

@Service
public interface IncomesService {
    public ResponseEntity<?> newIncome(IncomesDto income);
}
