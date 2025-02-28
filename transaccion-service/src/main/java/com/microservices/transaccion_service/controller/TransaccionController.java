package com.microservices.transaccion_service.controller;

import com.microservices.transaccion_service.enums.TipoTransaccion;
import com.microservices.transaccion_service.client.CuentaClient;
import com.microservices.transaccion_service.client.TarjetaClient;
import com.microservices.transaccion_service.dto.*;
import com.microservices.transaccion_service.entity.Transaccion;
import com.microservices.transaccion_service.repository.TransaccionRepository;
import com.microservices.transaccion_service.service.TransaccionService;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    @Autowired
    private final TransaccionService transaccionService;
    private final TransaccionRepository transaccionRepository;
    private final CuentaClient cuentaClient;
    private final TarjetaClient tarjetaClient;
    @Autowired
    private RestTemplate restTemplate;


    @Autowired
    public TransaccionController(TransaccionService transaccionService, TransaccionRepository transaccionRepository, CuentaClient cuentaClient, TarjetaClient tarjetaClient, RestTemplate restTemplate) {
        this.transaccionService = transaccionService;
        this.transaccionRepository = transaccionRepository;
        this.cuentaClient = cuentaClient;
        this.tarjetaClient = tarjetaClient;
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public List<Transaccion> obtenerTodas() {
        return transaccionService.getAllTransacciones();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaccion> obtenerPorId(@PathVariable Long id) {
        return transaccionService.getTransaccionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cuentas/{cuentaId}")
    public ResponseEntity<?> obtenerTransacciones(@PathVariable Long cuentaId) {
        return ResponseEntity.ok(transaccionService.findByCuentaId(cuentaId));
    }

    @PostMapping("/{cuentaId}")
    public ResponseEntity<?> crearTransaccion(
            @PathVariable Long cuentaId,
            @RequestBody TransaccionDTO transaccionDTO,
            @RequestHeader(name = "Authorization", required = true) String token) {

        System.out.println("Token recibido en api-transacciones: " + token);
        return transaccionService.crearTransaccion(cuentaId, transaccionDTO, token);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTransaccion(@PathVariable Long id) {
        transaccionService.eliminarTransaccion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cuenta/{id}/transacciones")
    public ResponseEntity<List<TransaccionDTO>> obtenerTransaccionesPorCuenta(@PathVariable Long id) {
        List<TransaccionDTO> transacciones = transaccionService.obtenerTransaccionesPorCuenta(id);
        return ResponseEntity.ok(transacciones);
    }

    @GetMapping("/cuenta/{id}/ultimos")
    public ResponseEntity<?> obtenerUltimosMovimientos(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            CuentaDTO cuenta = cuentaClient.obtenerCuenta(id, token);
            if (cuenta == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("Cuenta no encontrada", false, null));
            }

            List<Transaccion> ultimasTransacciones = transaccionRepository
                    .findTop5ByCuentaIdOrderByFechaDesc(id);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("saldoDisponible", cuenta.getSaldoDisponible());
            respuesta.put("ultimasTransacciones", ultimasTransacciones);

            return ResponseEntity.ok(respuesta);

        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("La cuenta no existe", false, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error interno del servidor", false, null));
        }
    }
    @GetMapping("/cuenta/{cuentaId}/actividad/{transferenciaId}")
    public ResponseEntity<?> obtenerDetalleActividad(
            @PathVariable Long cuentaId,
            @PathVariable Long transferenciaId,
            @RequestHeader("Authorization") String token) {

        try {
            CuentaDTO cuenta = cuentaClient.obtenerCuenta(cuentaId, token);
            if (cuenta == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("Cuenta no encontrada", false, null));
            }

            Optional<Transaccion> transaccionOpt = transaccionRepository.findById(transferenciaId);
            if (transaccionOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("La transferencia con ID " + transferenciaId + " no existe.", false, null));
            }

            Transaccion transaccion = transaccionOpt.get();

            if (!transaccion.getCuentaId().equals(cuentaId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse("No tienes permisos para ver esta actividad.", false, null));
            }

            return ResponseEntity.ok(transaccion);

        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("La cuenta no existe.", false, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error interno del servidor.", false, null));
        }
    }
    @PostMapping("/cuenta/{id}/ingreso")
    public ResponseEntity<?> ingresarDinero(@PathVariable Long id,
                                            @RequestBody IngresoDTO ingresoDTO,
                                            @RequestHeader("Authorization") String token) {
        try {
            List<TarjetaDTO> tarjetas = tarjetaClient.obtenerTarjetas(token);
            boolean tarjetaValida = tarjetas.stream()
                    .anyMatch(t -> t.getId().equals(ingresoDTO.getTarjetaId()));

            if (!tarjetaValida) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse("Tarjeta no válida o no encontrada.", false, null));
            }

            CuentaDTO cuenta = cuentaClient.obtenerCuenta(id, token);
            if (cuenta == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("Cuenta no encontrada", false, null));
            }

            Transaccion transaccion = new Transaccion();
            transaccion.setCuentaId(id);
            transaccion.setCantidad(ingresoDTO.getCantidad());
            transaccion.setDescripcion("Ingreso desde tarjeta");
            transaccion.setTipo(TipoTransaccion.INGRESO);
            transaccion.setFecha(LocalDateTime.now());

            transaccionRepository.save(transaccion);

            BigDecimal nuevoSaldo = cuenta.getSaldoDisponible().add(ingresoDTO.getCantidad());
            cuenta.setSaldoDisponible(nuevoSaldo);
            cuentaClient.actualizarCuenta(id, cuenta, token);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Ingreso registrado con éxito.", true, transaccion));

        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ApiResponse("Error en la comunicación con otros servicios.", false, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al procesar la solicitud.", false, null));
        }
    }

    //http://localhost:8082/api/transacciones/cuentas/51/transferir?identificadorDestino=luz.luz.carne&cantidad=100
    @PostMapping("/cuentas/{id}/transferir")
    public ResponseEntity<?> transferirDinero(
            @PathVariable Long id, // ID de la cuenta origen
            @RequestParam String identificadorDestino, // Identificador de la cuenta destino (por ejemplo, alias o cvu)
            @RequestParam BigDecimal cantidad,
            @RequestHeader("Authorization") String token) {
        try {
            System.out.println("Token recibido: " + token);

            // 1. Obtener la cuenta destino usando su identificador
            CuentaDTO cuentaDestino = cuentaClient.obtenerCuentaPorIdentificador(identificadorDestino, token);
            if (cuentaDestino == null) {
                System.out.println("Cuenta de destino no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("Cuenta de destino no encontrada", false, null));
            }

            // 2. Obtener la cuenta origen
            CuentaDTO cuentaOrigen = cuentaClient.obtenerCuenta(id, token);
            if (cuentaOrigen == null) {
                System.out.println("Cuenta de origen no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("Cuenta de origen no encontrada", false, null));
            }

            // 3. Verificar fondos suficientes en la cuenta origen
            if (cuentaOrigen.getSaldoDisponible().compareTo(cantidad) < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse("Fondos insuficientes", false, null));
            }

            // 4. Actualizar saldos:
            // Restar el monto de la cuenta origen
            BigDecimal nuevoSaldoOrigen = cuentaOrigen.getSaldoDisponible().subtract(cantidad);
            cuentaOrigen.setSaldoDisponible(nuevoSaldoOrigen);
            cuentaClient.actualizarCuenta(id, cuentaOrigen, token);

            // Sumar el monto a la cuenta destino
            BigDecimal nuevoSaldoDestino = cuentaDestino.getSaldoDisponible().add(cantidad);
            cuentaDestino.setSaldoDisponible(nuevoSaldoDestino);
            cuentaClient.actualizarCuenta(cuentaDestino.getId(), cuentaDestino, token);

            // 5. Registrar la transacción de egreso en la cuenta origen
            Transaccion egreso = new Transaccion();
            egreso.setCuentaId(id);
            egreso.setCantidad(cantidad);
            egreso.setDescripcion("Transferencia enviada a " + identificadorDestino);
            egreso.setTipo(TipoTransaccion.EGRESO);
            egreso.setFecha(LocalDateTime.now());
            transaccionRepository.save(egreso);

            // 6. Registrar la transacción de ingreso en la cuenta destino
            Transaccion ingreso = new Transaccion();
            ingreso.setCuentaId(cuentaDestino.getId());
            ingreso.setCantidad(cantidad);
            ingreso.setDescripcion("Transferencia recibida de la cuenta " + id);
            ingreso.setTipo(TipoTransaccion.INGRESO);
            ingreso.setFecha(LocalDateTime.now());
            transaccionRepository.save(ingreso);

            // 7. Preparar respuesta con ambas transacciones
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Transferencia realizada con éxito");
            respuesta.put("transaccionEgreso", egreso);
            respuesta.put("transaccionIngreso", ingreso);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Transferencia realizada con éxito", true, respuesta));

        } catch (FeignException e) {
            System.out.println("Error en comunicación con otros servicios: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ApiResponse("Error en la comunicación con otros servicios", false, null));
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al procesar la solicitud: " + e.getMessage(), false, null));
        }
    }


    @PostMapping("/cuentas/{id}/ingresar-dinero")
    public ResponseEntity<?> ingresarDineroDesdeTarjeta(
            @PathVariable Long id,
            @RequestBody IngresoDTO ingresoDTO,
            @RequestHeader("Authorization") String token) {
        try {
            List<TarjetaDTO> tarjetas = tarjetaClient.obtenerTarjetas(token);
            boolean tarjetaValida = tarjetas.stream()
                    .anyMatch(t -> t.getId().equals(ingresoDTO.getTarjetaId()) && t.getCuentaId().equals(id));

            if (!tarjetaValida) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse("Tarjeta no válida o no encontrada.", false, null));
            }

            CuentaDTO cuenta = cuentaClient.obtenerCuenta(id, token);
            if (cuenta == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("Cuenta no encontrada", false, null));
            }

            Transaccion transaccion = new Transaccion();
            transaccion.setCuentaId(id);
            transaccion.setCantidad(ingresoDTO.getCantidad());
            transaccion.setDescripcion("Ingreso desde tarjeta");
            transaccion.setTipo(TipoTransaccion.INGRESO);
            transaccion.setFecha(LocalDateTime.now());

            transaccionRepository.save(transaccion);

            BigDecimal nuevoSaldo = cuenta.getSaldoDisponible().add(ingresoDTO.getCantidad());
            cuenta.setSaldoDisponible(nuevoSaldo);
            cuentaClient.actualizarCuenta(id, cuenta, token);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Ingreso registrado con éxito.", true, transaccion));

        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ApiResponse("Error en la comunicación con otros servicios.", false, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al procesar la solicitud.", false, null));
        }
    }

}