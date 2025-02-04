package com.adolfo.test.gs.bussineslogic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Webservices {
    @Value("${webservices.incomes}")
    public String webserviceIncomes;
    @Value("${webservices.outgoings}")
    public String webserviceOutgoings;
}
