package com.microservices.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@FeignClient(name = "config-server", url = "http://localhost:8864")
public interface ConfigServerClient {
    @GetMapping("config/user-service/default")
    Map<String, Object> getConfigurations();
}
