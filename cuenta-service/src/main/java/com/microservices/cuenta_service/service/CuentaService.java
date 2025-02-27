package com.microservices.cuenta_service.service;

import com.microservices.cuenta_service.dto.CuentaDTO;
import com.microservices.cuenta_service.dto.TarjetaDTO;
import com.microservices.cuenta_service.dto.TarjetaResponse;
import com.microservices.cuenta_service.entity.Cuenta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CuentaService {
    List<Cuenta> obtenerTodasLasCuentas();

    Optional<Cuenta> obtenerCuenta(Long id);

    Cuenta crearCuenta(Long usuarioId, Cuenta cuenta);

    Cuenta actualizarCuenta(Long id, CuentaDTO cuentaDTO);


    void eliminarCuenta(Long id);

    public void vincularCuenta(Cuenta cuenta);

    ResponseEntity<?> crearCuentaYVincular(Long usuarioId, CuentaDTO cuentaDTO);

    BigDecimal obtenerSaldoPorId(Long cuentaId);

    List<TarjetaResponse> obtenerTarjetasPorCuenta(Long cuentaId);

    TarjetaResponse agregarTarjetaALaCuenta(Long cuentaId, TarjetaDTO tarjetaDTO);
    void agregarSaldo(Long cuentaId, BigDecimal monto);
    public CuentaDTO buscarPorAliasOCVU(String identificador);

}
