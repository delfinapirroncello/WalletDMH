package api_tarjeta.service.impl;

import api_tarjeta.client.CuentaClient;
import api_tarjeta.dto.ApiResponse;
import api_tarjeta.dto.CuentaDTO;
import api_tarjeta.dto.TarjetaDTO;
import api_tarjeta.entity.Tarjetas;
import api_tarjeta.repository.TarjetaRepository;
import api_tarjeta.service.TarjetaService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class TarjetaServiceImpl implements TarjetaService {

    private final TarjetaRepository tarjetaRepository;
    private final CuentaClient cuentaClient;

    private static final String JSON_FILE_PATH = "tarjetas.json";
    private final ObjectMapper objectMapper = new ObjectMapper();


    public TarjetaServiceImpl(TarjetaRepository tarjetaRepository, CuentaClient cuentaClient) {
        this.tarjetaRepository = tarjetaRepository;
        this.cuentaClient = cuentaClient;
        cargarTarjetasJson(); // Cargar las tarjetas al iniciar
    }

    @Override
    public ResponseEntity<?> crearTarjeta(Long cuentaId, TarjetaDTO tarjetaDTO, String token) {
        try {
            CuentaDTO cuenta = cuentaClient.obtenerCuenta(cuentaId, token);
            System.out.println("Cuenta obtenida: " + cuenta);

            // Verificar si la cuenta ya tiene una tarjeta del mismo tipo (409 Conflict)
            if (tarjetaRepository.existsByCuentaIdAndTipoTarjeta(cuentaId, tarjetaDTO.getTipoTarjeta())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse("La cuenta ya tiene una tarjeta de " + tarjetaDTO.getTipoTarjeta(), false, null));
            }

            // Asignar nombre y apellido si está vacío en el DTO
            if (tarjetaDTO.getNombreYApellido() == null || tarjetaDTO.getNombreYApellido().trim().isEmpty()) {
                if (cuenta.getNombreYApellido() != null && !cuenta.getNombreYApellido().trim().isEmpty()) {
                    tarjetaDTO.setNombreYApellido(cuenta.getNombreYApellido());
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ApiResponse("Error: No se puede crear la tarjeta porque no hay nombre y apellido disponibles.", false, null));
                }
            }

        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse("La cuenta con ID " + cuentaId + " no existe.", false, null));
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ApiResponse("Error al conectar con el servicio de cuentas.", false, null));
        }

        // Generar número de tarjeta único y CVV
        String numeroTarjeta = generarNumeroTarjetaUnico();
        String cvv = generarCVV();

        // Crear nueva tarjeta
        Tarjetas nuevaTarjeta = new Tarjetas();
        nuevaTarjeta.setCuentaId(cuentaId);
        nuevaTarjeta.setNumeroTarjeta(numeroTarjeta);
        nuevaTarjeta.setTipoTarjeta(tarjetaDTO.getTipoTarjeta());
        nuevaTarjeta.setFechaExpiracion(tarjetaDTO.getFechaExpiracion());
        nuevaTarjeta.setNombreYApellido(tarjetaDTO.getNombreYApellido());
        nuevaTarjeta.setCvv(cvv);

        System.out.println("Tarjeta creada: " + nuevaTarjeta);

        // Guardar en la base de datos
        tarjetaRepository.save(nuevaTarjeta);
        guardarTarjetasJson();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Tarjeta creada exitosamente", true, nuevaTarjeta));
    }



    private String generarNumeroTarjetaUnico() {
        String numeroTarjeta;
        do {
            numeroTarjeta = generarNumeroTarjeta(); // Genera un número aleatorio
        } while (tarjetaRepository.existsByNumeroTarjeta(numeroTarjeta)); // Verifica si ya existe
        return numeroTarjeta;
    }

    private String generarNumeroTarjeta() {
        long numero = 4000000000000000L + (long) (Math.random() * 99999999999999L); // Prefijo + aleatorio
        return String.valueOf(numero);
    }

    private String generarCVV() {
        int cvv = (int) (Math.random() * 900) + 100; // Genera un número entre 100 y 999
        return String.valueOf(cvv);
    }

    @Override
    public List<Tarjetas> obtenerTarjetasDeCuenta(Long cuentaId) {
        return tarjetaRepository.findByCuentaId(cuentaId);
    }

    @Override
    public ResponseEntity<?> actualizarTarjeta(Long id, TarjetaDTO tarjetaDTO) {
        return tarjetaRepository.findById(id).map(tarjeta -> {

            if (tarjetaDTO.getFechaExpiracion() != null) {
                tarjeta.setFechaExpiracion(tarjetaDTO.getFechaExpiracion());
            }

            if (tarjetaDTO.getTipoTarjeta() != null) {
                tarjeta.setTipoTarjeta(tarjetaDTO.getTipoTarjeta());
            }

            tarjetaRepository.save(tarjeta);
            guardarTarjetasJson();

            return ResponseEntity.ok(new ApiResponse("Tarjeta actualizada correctamente", true, tarjeta));

        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse("Tarjeta no encontrada con ID: " + id, false, null)));
    }



    @Override
    public void eliminarTarjeta(Long id) {
        if (!tarjetaRepository.existsById(id)) {
            throw new RuntimeException("La tarjeta con ID " + id + " no existe.");
        }
        tarjetaRepository.deleteById(id);
        guardarTarjetasJson(); // Actualizar JSON después de eliminar
    }

    private void cargarTarjetasJson() {
        File file = new File(JSON_FILE_PATH);
        if (file.exists()) {
            try {
                List<Tarjetas> tarjetasList = objectMapper.readValue(file, new TypeReference<List<Tarjetas>>() {});
                tarjetaRepository.saveAll(tarjetasList); // Guardar en la base de datos si es necesario
            } catch (IOException e) {
                throw new RuntimeException("Error al leer el archivo JSON: " + e.getMessage());
            }
        }
    }

    public void guardarTarjetasJson() {
        try {
            List<Tarjetas> tarjetasList = tarjetaRepository.findAll();
            objectMapper.writeValue(new File(JSON_FILE_PATH), tarjetasList);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo JSON: " + e.getMessage());
        }
    }


}
