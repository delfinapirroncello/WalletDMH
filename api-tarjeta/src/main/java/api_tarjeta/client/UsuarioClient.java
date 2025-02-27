package api_tarjeta.client;

import api_tarjeta.dto.AuthResponse;
import api_tarjeta.service.AuthRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@Deprecated
@FeignClient(name = "user-service")
public interface UsuarioClient {
    @PostMapping("/api/auth/login")
    AuthResponse login(@RequestBody AuthRequest authRequest);
}
