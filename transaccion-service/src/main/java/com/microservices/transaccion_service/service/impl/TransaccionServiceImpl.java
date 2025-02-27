package com.microservices.transaccion_service.service.impl;

import com.microservices.transaccion_service.TipoTransaccion;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransaccionServiceImpl implements TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final CuentaClient cuentaClient;

    @Autowired
    public TransaccionServiceImpl(TransaccionRepository transaccionRepository, CuentaClient cuentaClient) {
        this.transaccionRepository = transaccionRepository;
        this.cuentaClient = cuentaClient;
    }

    @Override
    public ResponseEntity<?> crearTransaccion(Long cuentaId, TransaccionDTO transaccionDTO, String token) {
        System.out.println("🔹 Estado de SecurityContext en crearTransaccion: " + SecurityContextHolder.getContext().getAuthentication());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getCredentials() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse("No se encontró autenticación en el contexto de seguridad.", false, null));
        }

        String extractedToken = authentication.getCredentials().toString();
        System.out.println("🔹 Token extraído desde SecurityContextHolder: " + extractedToken);

        try {
            // 🔹 Obtener la cuenta actual desde cuenta-service
            CuentaDTO cuenta = cuentaClient.obtenerCuenta(cuentaId, "Bearer " + extractedToken);
            System.out.println("Cuenta obtenida: " + cuenta);

            if (cuenta == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("La cuenta con ID " + cuentaId + " no existe.", false, null));
            }

            // 🔹 Calcular el nuevo saldo
            BigDecimal saldoDisponible = Optional.ofNullable(cuenta.getSaldoDisponible()).orElse(BigDecimal.ZERO);
            BigDecimal cantidad = Optional.ofNullable(transaccionDTO.getCantidad()).orElse(BigDecimal.ZERO);
            BigDecimal nuevoSaldo = transaccionDTO.getTipo() == TipoTransaccion.EGRESO ?
                    saldoDisponible.subtract(cantidad) :
                    saldoDisponible.add(cantidad);

            // 🔹 Crear y guardar la transacción
            Transaccion nuevaTransaccion = new Transaccion();
            nuevaTransaccion.setCuentaId(cuentaId);
            nuevaTransaccion.setCantidad(transaccionDTO.getCantidad());
            nuevaTransaccion.setFecha(LocalDateTime.now());
            nuevaTransaccion.setDescripcion(transaccionDTO.getDescripcion());
            nuevaTransaccion.setTipo(transaccionDTO.getTipo() != null ? transaccionDTO.getTipo() : TipoTransaccion.INGRESO);

            transaccionRepository.save(nuevaTransaccion);

            // 🔹 Crear un nuevo objeto con los datos existentes + saldo actualizado
            CuentaDTO cuentaActualizada = new CuentaDTO();
            cuentaActualizada.setId(cuenta.getId());
            cuentaActualizada.setNombreYApellido(cuenta.getNombreYApellido());
            cuentaActualizada.setNumeroCuenta(cuenta.getNumeroCuenta());
            cuentaActualizada.setAlias(cuenta.getAlias());
            cuentaActualizada.setCvu(cuenta.getCvu());
            cuentaActualizada.setTelefono(cuenta.getTelefono());
            cuentaActualizada.setSaldoDisponible(nuevoSaldo);

            // 🔹 Actualizar la cuenta en cuenta-service
            System.out.println("🔹 Intentando actualizar la cuenta con nuevo saldo: " + nuevoSaldo);
            cuentaClient.actualizarCuenta(cuentaId, cuentaActualizada, "Bearer " + extractedToken);
            System.out.println("✅ Cuenta actualizada exitosamente.");

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Transacción creada exitosamente", true, nuevaTransaccion));

        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("La cuenta con ID " + cuentaId + " no existe.", false, null));
        } catch (FeignException e) {
            System.out.println("❌ Error al conectar con cuenta-service: " + e.getMessage());
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
    }

    @Override
    public Transaccion registrarTransaccion(Transaccion transaccion) {
        return transaccionRepository.save(transaccion);
    }

}
