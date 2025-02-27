package api_tarjeta.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@Deprecated
@FeignClient(name = "api-config")
public interface ConfigClient {

    @GetMapping("/tarjeta.limite")
    String getLimiteMaximo();

    @GetMapping("/tarjeta.cantidad.maxima")
    String getCantidadMaxima();
}
