package com.microservices.cuenta_service.controller;

import com.microservices.cuenta_service.client.TarjetaClient;
import com.microservices.cuenta_service.client.TransaccionClient;
import com.microservices.cuenta_service.client.UsuarioClient;
import com.microservices.cuenta_service.dto.*;
import com.microservices.cuenta_service.entity.Cuenta;
import com.microservices.cuenta_service.service.CuentaService;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("api/cuentas")
@Validated
public class CuentaController {
    private final CuentaService cuentaService;
    private final TarjetaClient tarjetaClient;
    private final TransaccionClient transaccionClient;
    private final UsuarioClient usuarioClient;
    public CuentaController(CuentaService cuentaService,
                            TarjetaClient tarjetaClient,
                            TransaccionClient transaccionClient,
                            UsuarioClient usuarioClient) {
        this.cuentaService = cuentaService;
        this.tarjetaClient = tarjetaClient;
        this.transaccionClient = transaccionClient;
        this.usuarioClient = usuarioClient;
    }
    @PostMapping("/crear/{usuarioId}")
    public ResponseEntity<?> crearCuentaYVincular(
            @PathVariable Long usuarioId,
            @RequestBody CuentaDTO cuentaDTO) {
        return cuentaService.crearCuentaYVincular(usuarioId, cuentaDTO);
    }
    @GetMapping("/{cuentaId}")
    public ResponseEntity<?> obtenerCuenta(@PathVariable Long cuentaId,
                                           @RequestHeader("Authorization") String token) {

        System.out.println("Token recibido en cuenta-service: " + token);
        Optional<Cuenta> cuentaOpt = cuentaService.obtenerCuenta(cuentaId);

        if (cuentaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("La cuenta con ID " + cuentaId + " no existe.", false, null));
        }
        Cuenta cuenta = cuentaOpt.get();
        return ResponseEntity.ok(new CuentaDTO(
                cuenta.getId(),
                cuenta.getNombreYApellido(),
                cuenta.getNumeroCuenta(),
                cuenta.getAlias(),
                cuenta.getCvu(),
                cuenta.getTelefono(),
                cuenta.getSaldoDisponible()
        ));
    }
    @GetMapping
    public List<Cuenta> obtenerTodasLasCuentas() {
        return cuentaService.obtenerTodasLasCuentas();
    }

    @PutMapping("/{cuentaId}")
    public ResponseEntity<?> actualizarCuenta(@PathVariable Long cuentaId, @RequestBody CuentaDTO cuentaDTO) {
        try {
            Cuenta cuentaActualizada = cuentaService.actualizarCuenta(cuentaId, cuentaDTO);
            return ResponseEntity.ok(new ApiResponse("Cuenta actualizada correctamente", true, cuentaActualizada));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), false, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al actualizar la cuenta", false, null));
        }
    }


    @GetMapping("/{id}/saldo")
    public ResponseEntity<ApiResponse> obtenerSaldoPorId(@PathVariable Long id) {
        try {
            BigDecimal saldo = cuentaService.obtenerSaldoPorId(id);
            if (saldo.compareTo(BigDecimal.ZERO) == 0) {
                return ResponseEntity.ok(new ApiResponse("La cuenta existe pero no tiene saldo disponible.", true, saldo));
            }
            return ResponseEntity.ok(new ApiResponse("Saldo obtenido exitosamente", true, saldo));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("La cuenta con ID " + id + " no existe.", false, null));
        }
    }
        @PutMapping("/{id}/saldo")
        public ResponseEntity<?> agregarSaldo(@PathVariable Long id, @RequestBody(required = false) CuentaDTO cuentaDTO) {
            if (cuentaDTO == null || cuentaDTO.getSaldoDisponible() == null || cuentaDTO.getSaldoDisponible().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("El monto debe ser mayor a 0 y no puede estar vacío.", false, null));
            }
            Optional<Cuenta> cuentaOpt = cuentaService.obtenerCuenta(id);
            if (cuentaOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("La cuenta con ID " + id + " no existe.", false, null));
            }
            cuentaService.agregarSaldo(id, cuentaDTO.getSaldoDisponible());
            // Obtener el nuevo saldo después de la operación
            BigDecimal nuevoSaldo = cuentaService.obtenerSaldoPorId(id);
            return ResponseEntity.ok(new ApiResponse("Saldo agregado correctamente.", true, nuevoSaldo));
        }
        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse> eliminarCuenta(@PathVariable Long id) {
            Optional<Cuenta> cuentaOpt = cuentaService.obtenerCuenta(id);
            if (cuentaOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("La cuenta con ID " + id + " no existe.", false, null));
            }
            cuentaService.eliminarCuenta(id);
            return ResponseEntity.ok(new ApiResponse("Cuenta eliminada exitosamente.", true, null));
        }
        @PostMapping("/{cuentaId}/tarjetas")
        public ResponseEntity<TarjetaResponse> agregarTarjeta(@PathVariable Long cuentaId, @RequestBody TarjetaDTO tarjetaDTO) {
            return ResponseEntity.ok(cuentaService.agregarTarjetaALaCuenta(cuentaId, tarjetaDTO));
        }
        @GetMapping("/{cuentaId}/tarjetas")
        public ResponseEntity<List<TarjetaResponse>> obtenerTarjetas(@PathVariable Long cuentaId) {
            return ResponseEntity.ok(cuentaService.obtenerTarjetasPorCuenta(cuentaId));
        }
    @GetMapping("/{id}/actividad")
    public ResponseEntity<?> obtenerActividades(@PathVariable Long id) {
        try {
            List<TransaccionDTO> transacciones = transaccionClient.obtenerTransaccionesPorCuenta(id);

            if (transacciones.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse("No hay actividades registradas para esta cuenta.", true, transacciones));
            }

            transacciones.sort((t1, t2) -> t2.getFecha().compareTo(t1.getFecha()));

            return ResponseEntity.ok(new ApiResponse("Actividades obtenidas correctamente.", true, transacciones));
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ApiResponse("Error al conectar con el servicio de transacciones.", false, null));
        }
    }
    @GetMapping("/identificador/{identificador}")
    public ResponseEntity<CuentaDTO> obtenerCuentaPorIdentificador(
            @PathVariable String identificador,
            @RequestHeader(value = "Authorization", required = false) String token) {

        CuentaDTO cuenta = cuentaService.buscarPorAliasOCVU(identificador);
        return cuenta != null ? ResponseEntity.ok(cuenta) : ResponseEntity.notFound().build();
    }

}