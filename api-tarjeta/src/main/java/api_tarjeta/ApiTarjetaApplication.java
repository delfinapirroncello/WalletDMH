package api_tarjeta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ApiTarjetaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiTarjetaApplication.class, args);
	}

}
