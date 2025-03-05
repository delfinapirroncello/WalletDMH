package api_tarjetas.service;

import api_tarjetas.dto.TarjetaDTO;
import api_tarjetas.entity.Tarjetas;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TarjetaService {
    ResponseEntity<?> crearTarjeta(Long cuentaId, TarjetaDTO tarjetaDTO, String token);
    List<Tarjetas> obtenerTarjetasDeCuenta(Long cuentaId);

    ResponseEntity<?> actualizarTarjeta(Long id, TarjetaDTO tarjetaDTO);
    void eliminarTarjeta(Long id);
    void guardarTarjetasJson();
}