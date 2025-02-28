package api_tarjeta.client;

import api_tarjeta.dto.CuentaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "cuenta-service")
public interface CuentaClient {
 @GetMapping("/api/cuentas/{cuentaId}")
 CuentaDTO obtenerCuenta(
         @PathVariable("cuentaId") Long cuentaId,
         @RequestHeader("Authorization") String token
 );

}