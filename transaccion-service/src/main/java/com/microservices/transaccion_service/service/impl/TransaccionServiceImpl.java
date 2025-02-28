package com.microservices.transaccion_service.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.transaccion_service.enums.TipoTransaccion;
import com.microservices.transaccion_service.client.CuentaClient;
import com.microservices.transaccion_service.dto.ApiResponse;
import com.microservices.transaccion_service.dto.CuentaDTO;
import com.microservices.transaccion_service.dto.TransaccionDTO;
import com.microservices.transaccion_service.entity.Transaccion;
import com.microservices.transaccion_service.repository.TransaccionRepository;
import com.microservices.transaccion_service.service.TransaccionService;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransaccionServiceImpl implements TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final CuentaClient cuentaClient;

    private static final String JSON_FILE_PATH = "transacciones.json";
    private final Map<Long, Transaccion> transacciones = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    public TransaccionServiceImpl(TransaccionRepository transaccionRepository, CuentaClient cuentaClient) {
        this.transaccionRepository = transaccionRepository;
        this.cuentaClient = cuentaClient;
        cargarTransaccionesJson();
    }

    @Override
    public ResponseEntity<?> crearTransaccion(Long cuentaId, TransaccionDTO transaccionDTO, String token) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getCredentials() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse("No se encontró autenticación en el contexto de seguridad.", false, null));
        }

        String extractedToken = authentication.getCredentials().toString();

        try {
            CuentaDTO cuenta = cuentaClient.obtenerCuenta(cuentaId, "Bearer " + extractedToken);

            if (cuenta == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("La cuenta con ID " + cuentaId + " no existe.", false, null));
            }

            BigDecimal saldoDisponible = Optional.ofNullable(cuenta.getSaldoDisponible()).orElse(BigDecimal.ZERO);
            BigDecimal cantidad = Optional.ofNullable(transaccionDTO.getCantidad()).orElse(BigDecimal.ZERO);
            BigDecimal nuevoSaldo = transaccionDTO.getTipo() == TipoTransaccion.EGRESO ?
                    saldoDisponible.subtract(cantidad) :
                    saldoDisponible.add(cantidad);

            Transaccion nuevaTransaccion = new Transaccion();
            nuevaTransaccion.setCuentaId(cuentaId);
            nuevaTransaccion.setCantidad(transaccionDTO.getCantidad());
            nuevaTransaccion.setFecha(LocalDateTime.now());
            nuevaTransaccion.setDescripcion(transaccionDTO.getDescripcion());
            nuevaTransaccion.setTipo(transaccionDTO.getTipo() != null ? transaccionDTO.getTipo() : TipoTransaccion.INGRESO);

            transaccionRepository.save(nuevaTransaccion);

            CuentaDTO cuentaActualizada = new CuentaDTO();
            cuentaActualizada.setId(cuenta.getId());
            cuentaActualizada.setNombreYApellido(cuenta.getNombreYApellido());
            cuentaActualizada.setNumeroCuenta(cuenta.getNumeroCuenta());
            cuentaActualizada.setAlias(cuenta.getAlias());
            cuentaActualizada.setCvu(cuenta.getCvu());
            cuentaActualizada.setTelefono(cuenta.getTelefono());
            cuentaActualizada.setSaldoDisponible(nuevoSaldo);

            guardarTransaccionesJson();

            System.out.println("Intentando actualizar la cuenta con nuevo saldo: " + nuevoSaldo);
            cuentaClient.actualizarCuenta(cuentaId, cuentaActualizada, "Bearer " + extractedToken);
            System.out.println("Cuenta actualizada exitosamente.");

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Transacción creada exitosamente", true, nuevaTransaccion));

        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("La cuenta con ID " + cuentaId + " no existe.", false, null));
        } catch (FeignException e) {
            System.out.println("Error al conectar con cuenta-service: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ApiResponse("Error al conectar con el servicio de cuentas.", false, null));
        }
    }

    @Override
    public List<Transaccion> getAllTransacciones() {
        return transaccionRepository.findAll();
    }

    @Override
    public List<Transaccion> findByCuentaId(Long cuentaId) {
        return transaccionRepository.findByCuentaId(cuentaId);
    }

    @Override
    public Optional<Transaccion> getTransaccionById(Long id) {
        return transaccionRepository.findById(id);
    }

    @Override
    public List<TransaccionDTO> obtenerTransaccionesPorCuenta(Long cuentaId) {
        List<Transaccion> transacciones = transaccionRepository.findByCuentaId(cuentaId);

        return transacciones.stream()
                .map(transaccion -> new TransaccionDTO(
                        transaccion.getTransaccionId(),
                        transaccion.getCuentaId(),
                        transaccion.getCantidad(),
                        transaccion.getDescripcion(),
                        transaccion.getTipo(),
                        transaccion.getFecha()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarTransaccion(Long id) {
        transaccionRepository.deleteById(id);
        guardarTransaccionesJson();
    }

    @Override
    public Transaccion registrarTransaccion(Transaccion transaccion) {
        Transaccion nuevaTransaccion = transaccionRepository.save(transaccion);
        guardarTransaccionesJson();
        return nuevaTransaccion;
    }

    private void cargarTransaccionesJson() {
        File file = new File(JSON_FILE_PATH);
        if (file.exists() && file.length() > 0) {
            try {
                List<Transaccion> transaccionList = objectMapper.readValue(file, new TypeReference<List<Transaccion>>() {});
                transacciones.clear();
                for (Transaccion transaccion : transaccionList) {
                    transacciones.put(transaccion.getTransaccionId(), transaccion);
                }
            } catch (IOException e) {
                System.err.println("Error al leer el archivo JSON. Se ignorará el archivo corrupto.");
            }
        }
    }


    private void guardarTransaccionesJson() {
        try {
            List<Transaccion> transaccionList = transaccionRepository.findAll();
            objectMapper.writeValue(new File(JSON_FILE_PATH), transaccionList);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo JSON: " + e.getMessage());
        }
    }
}
