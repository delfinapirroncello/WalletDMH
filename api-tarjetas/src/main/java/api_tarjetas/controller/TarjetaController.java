package api_tarjetas.controller;

import api_tarjetas.dto.ApiResponse;
import api_tarjetas.dto.TarjetaDTO;
import api_tarjetas.entity.Tarjetas;
import api_tarjetas.repository.TarjetaRepository;
import api_tarjetas.service.TarjetaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tarjetas")
public class TarjetaController {

    private final TarjetaService tarjetaService;
    private final TarjetaRepository tarjetaRepository;

    public TarjetaController(TarjetaService tarjetaService, TarjetaRepository tarjetaRepository) {
        this.tarjetaService = tarjetaService;
        this.tarjetaRepository = tarjetaRepository;
    }

    @PostMapping("/{cuentaId}")
    public ResponseEntity<ApiResponse> crearTarjeta(
            @PathVariable Long cuentaId,
            @RequestBody TarjetaDTO tarjetaDTO,
            @RequestHeader("Authorization") String token) {
        System.out.println("Token recibido en api-tarjetas: " + token);
        return (ResponseEntity<ApiResponse>) tarjetaService.crearTarjeta(cuentaId, tarjetaDTO, token);
    }


    @GetMapping("/cuentas/{cuentaId}/tarjetas")
    public ResponseEntity<?> obtenerTarjetasPorCuenta(@PathVariable Long cuentaId) {
        try {
            List<Tarjetas> tarjetas = tarjetaRepository.findByCuentaId(cuentaId);
            if (tarjetas.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse("La cuenta no tiene tarjetas asociadas.", true, tarjetas)); // ✅ 200 con mensaje
            }
            return ResponseEntity.ok(tarjetas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error interno del servidor: " + e.getMessage(), false, null));
        }
    }

    @GetMapping("/tarjetas")
    public ResponseEntity<?> obtenerTarjetas() {
        try {
            List<Tarjetas> tarjetas = tarjetaRepository.findAll();
            if (tarjetas.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse("No hay tarjetas registradas.", true, tarjetas));
            }
            return ResponseEntity.ok(tarjetas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error interno del servidor: " + e.getMessage(), false, null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarTarjeta(@PathVariable Long id, @RequestBody TarjetaDTO tarjetaDTO) {
        try {
            Optional<Tarjetas> tarjetaExistente = tarjetaRepository.findById(id);

            if (tarjetaExistente.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse("No se encontró la tarjeta para actualizar.", true, null));
            }

            Tarjetas tarjeta = tarjetaExistente.get();
            System.out.println("Antes de actualizar: " + tarjeta);

            boolean actualizado = false;
            List<String> cambiosNoPermitidos = new ArrayList<>();

            if (tarjetaDTO.getFechaExpiracion() != null) {
                tarjeta.setFechaExpiracion(tarjetaDTO.getFechaExpiracion());
                actualizado = true;
            }
            if (tarjetaDTO.getTipoTarjeta() != null) {
                tarjeta.setTipoTarjeta(tarjetaDTO.getTipoTarjeta());
                actualizado = true;
            }
            if (tarjetaDTO.getNombreYApellido() != null) {
                cambiosNoPermitidos.add("nombre");
            }
            if (tarjetaDTO.getNumeroTarjeta() != null) {
                cambiosNoPermitidos.add("número de tarjeta");
            }
            if (tarjetaDTO.getCvv() != null) {
                cambiosNoPermitidos.add("CVV");
            }
            if (!cambiosNoPermitidos.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse("No puedes actualizar los siguientes campos desde aquí: " + String.join(", ", cambiosNoPermitidos), false, null)
                );
            }
            if (!actualizado) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse("No se enviaron datos válidos para actualizar.", false, null)
                );
            }
            tarjetaRepository.save(tarjeta);
            tarjetaService.guardarTarjetasJson();

            return ResponseEntity.ok(new ApiResponse("Tarjeta actualizada correctamente", true, tarjeta));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al actualizar la tarjeta: " + e.getMessage(), false, null));
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> eliminarTarjeta(@PathVariable Long id) {
        try {
            Optional<Tarjetas> tarjetaExistente = tarjetaRepository.findById(id);

            if (tarjetaExistente.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("No se encontró la tarjeta con ID " + id + ", no se puede eliminar.", false, null));
            }

            tarjetaService.eliminarTarjeta(id);
            return ResponseEntity.ok(new ApiResponse("Tarjeta eliminada exitosamente.", true, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al eliminar la tarjeta: " + e.getMessage(), false, null));
        }
    }
}
