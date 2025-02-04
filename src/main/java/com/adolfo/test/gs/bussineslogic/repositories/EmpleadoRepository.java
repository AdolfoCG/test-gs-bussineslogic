package com.adolfo.test.gs.bussineslogic.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.adolfo.test.gs.bussineslogic.entities.Empleado;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long>{
    @Query("select e from Empleado e where e.nombre=?1")
    Optional<Empleado> findByNombre(String nombre);
}