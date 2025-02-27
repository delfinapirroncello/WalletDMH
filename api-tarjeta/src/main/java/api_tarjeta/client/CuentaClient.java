package api_tarjeta.client;

import api_tarjeta.dto.CuentaDTO;
import api_tarjeta.dto.CuentaResponse;
import api_tarjeta.dto.TarjetaRequest;  // Clase DTO para la solicitud de tarjeta
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "cuenta-service")
public interface CuentaClient {

 @GetMapping("/api/cuentas/{cuentaId}")
 CuentaDTO obtenerCuenta(
         @PathVariable("cuentaId") Long cuentaId,
         @RequestHeader("Authorization") String token
 );

}