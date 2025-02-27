package com.microservices.cuenta_service.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.cuenta_service.client.TarjetaClient;
import com.microservices.cuenta_service.client.TransaccionClient;
import com.microservices.cuenta_service.client.UsuarioClient;
import com.microservices.cuenta_service.dto.*;
import com.microservices.cuenta_service.entity.Cuenta;
import com.microservices.cuenta_service.repository.CuentaRepository;
import com.microservices.cuenta_service.service.CuentaService;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service
public class CuentaServiceImpl implements CuentaService {
    private final CuentaRepository cuentaRepository;
    private final UsuarioClient usuarioClient;
    private final TarjetaClient tarjetaClient;
    private final TransaccionClient transaccionClient;
    private static final String JSON_FILE_PATH = "cuentas.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CuentaServiceImpl(CuentaRepository cuentaRepository, UsuarioClient usuarioClient,
                             TarjetaClient tarjetaClient, TransaccionClient transaccionClient) {
        this.cuentaRepository = cuentaRepository;
        this.usuarioClient = usuarioClient;
        this.tarjetaClient = tarjetaClient;
        this.transaccionClient = transaccionClient;
    }
    @Override
    public ResponseEntity<?> crearCuentaYVincular(Long usuarioId, CuentaDTO cuentaDTO) {
        // Verificar si el usuario ya tiene una cuenta vinculada
        if (cuentaRepository.findByUsuarioId(usuarioId).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El usuario ya tiene una cuenta vinculada.");
        }
        // Intentar obtener el usuario desde el user-service
        UsuarioDTO usuarioResponse;
        try {
            usuarioResponse = usuarioClient.obtenerUsuarioPorId(usuarioId);
        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró un usuario con ID: " + usuarioId);
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Error al conectar con el servicio de usuarios.");
        }
        // Crear la cuenta
        Cuenta nuevaCuenta = new Cuenta();
        nuevaCuenta.setUsuarioId(usuarioId);
        nuevaCuenta.setAlias(usuarioResponse.getAlias());
        nuevaCuenta.setCvu(usuarioResponse.getCvu());
        nuevaCuenta.setNombreYApellido(usuarioResponse.getNombreYApellido());
        nuevaCuenta.setTelefono(usuarioResponse.getTelefono());

        nuevaCuenta.setSaldoDisponible(
                cuentaDTO.getSaldoDisponible() != null ? cuentaDTO.getSaldoDisponible() : BigDecimal.ZERO
        );

        String numeroCuenta = generarNumeroCuenta();
        nuevaCuenta.setNumeroCuenta(numeroCuenta);

        cuentaRepository.save(nuevaCuenta);
        cuentaRepository.flush();
        guardarCuentasJson();

        Cuenta cuentaGuardada = cuentaRepository.findById(nuevaCuenta.getId()).orElse(null);

        return ResponseEntity.status(HttpStatus.CREATED).body(new CuentaDTO(nuevaCuenta, usuarioResponse));
    }

    private void cargarCuentasJson() {
        File file = new File(JSON_FILE_PATH);
        if (file.exists()) {
            try {
                List<Cuenta> tarjetasList = objectMapper.readValue(file, new TypeReference<List<Cuenta>>() {});
                cuentaRepository.saveAll(tarjetasList); // Guardar en la base de datos si es necesario
            } catch (IOException e) {
                throw new RuntimeException("Error al leer el archivo JSON: " + e.getMessage());
            }
        }
    }

    private void guardarCuentasJson() {
        try {
            List<Cuenta> tarjetasList = cuentaRepository.findAll();
            objectMapper.writeValue(new File(JSON_FILE_PATH), tarjetasList);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo JSON: " + e.getMessage());
        }
    }

    private String generarNumeroCuenta() {
        // Generar un número de cuenta de 10 dígitos
        Random random = new Random();
        StringBuilder numeroCuenta = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            numeroCuenta.append(random.nextInt(10));
        }
        return numeroCuenta.toString();
    }
    @Override
    public Optional<Cuenta> obtenerCuenta(Long id) {
        return cuentaRepository.findById(id);
    }
    @Override
    public List<Cuenta> obtenerTodasLasCuentas() {
        return cuentaRepository.findAll();
    }

    public Cuenta actualizarCuenta(Long id, CuentaDTO cuentaDTO) {
        Optional<Cuenta> cuentaOpt = cuentaRepository.findById(id);

        if (cuentaOpt.isEmpty()) {
            throw new NoSuchElementException("No se encontró la cuenta con ID " + id);
        }

        Cuenta cuenta = cuentaOpt.get();
        cuenta.setNombreYApellido(cuentaDTO.getNombreYApellido());
        cuenta.setNumeroCuenta(cuentaDTO.getNumeroCuenta());
        cuenta.setAlias(cuentaDTO.getAlias());
        cuenta.setCvu(cuentaDTO.getCvu());
        cuenta.setTelefono(cuentaDTO.getTelefono());
        cuenta.setSaldoDisponible(cuentaDTO.getSaldoDisponible());

        return cuentaRepository.save(cuenta); // Asegura que se devuelve una cuenta actualizada
    }



    @Override
    public BigDecimal obtenerSaldoPorId(Long cuentaId) {
        return cuentaRepository.findById(cuentaId)
                .map(Cuenta::getSaldoDisponible)
                .orElseThrow(() -> new NoSuchElementException("Cuenta no encontrada"));
    }
    @Override
    public Cuenta crearCuenta(Long usuarioId, Cuenta cuenta) {
        cuenta.setUsuarioId(usuarioId);
        return cuentaRepository.save(cuenta);
    }
    @Override
    public TarjetaResponse agregarTarjetaALaCuenta(Long cuentaId, TarjetaDTO tarjetaDTO) {
        return tarjetaClient.agregarTarjetaALaCuenta(cuentaId, tarjetaDTO);
    }
    @Override
    public void vincularCuenta(Cuenta cuenta) {
        cuentaRepository.save(cuenta);
    }
    @Override
    public List<TarjetaResponse> obtenerTarjetasPorCuenta(Long cuentaId) {
        return tarjetaClient.obtenerTarjetasPorCuenta(cuentaId);
    }
    @Override
    public void agregarSaldo(Long cuentaId, BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }
        cuentaRepository.findById(cuentaId).ifPresentOrElse(cuenta -> {
            BigDecimal saldoActual = Optional.ofNullable(cuenta.getSaldoDisponible()).orElse(BigDecimal.ZERO);
            cuenta.setSaldoDisponible(saldoActual.add(monto));
            cuentaRepository.save(cuenta);
        }, () -> {
            throw new NoSuchElementException("Cuenta no encontrada");
        });
    }
    @Override
    public void eliminarCuenta(Long id) {
        cuentaRepository.deleteById(id);
    }

    public CuentaDTO buscarPorAliasOCVU(String identificador) {
        return cuentaRepository.findByAliasOrCvu(identificador, identificador)
                .map(cuenta -> new CuentaDTO(
                        cuenta.getId(), cuenta.getNombreYApellido(), cuenta.getNumeroCuenta(),
                        cuenta.getAlias(), cuenta.getCvu(), cuenta.getTelefono(), cuenta.getSaldoDisponible()))
                .orElse(null);
    }
}