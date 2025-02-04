package com.adolfo.test.gs.bussineslogic.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.adolfo.test.gs.bussineslogic.dto.OutgoingsDto;

@Service
public interface OutgoingsService {
    public ResponseEntity<?> newOutgoings(OutgoingsDto outgoing);
}