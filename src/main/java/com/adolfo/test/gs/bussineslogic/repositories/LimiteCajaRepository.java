package com.adolfo.test.gs.bussineslogic.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.adolfo.test.gs.bussineslogic.entities.Empleado;
import com.adolfo.test.gs.bussineslogic.entities.LimiteCaja;

@Repository
public interface LimiteCajaRepository extends JpaRepository<LimiteCaja, Long> {
    @Query("select l from LimiteCaja l where l.empleado=?1")
    Optional<LimiteCaja> findByEmpleado(Empleado empleado);
}