package api_tarjetas.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class FeignClientInterceptor {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            String token = getToken();
            if (token != null) {
                requestTemplate.header("Authorization", "Bearer " + token);
            }
        };
    }

    private String getToken() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
            return jwt.getTokenValue();
        }
        return null;
    }
}
