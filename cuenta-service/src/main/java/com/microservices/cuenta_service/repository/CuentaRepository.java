package com.microservices.cuenta_service.repository;

import com.microservices.cuenta_service.dto.TarjetaResponse;
import com.microservices.cuenta_service.entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    Optional<Cuenta> findById(Long id);
    Optional<Cuenta> findByUsuarioId(Long usuarioId);
    Optional<Cuenta> findByAliasOrCvu(String alias, String cvu);
}

