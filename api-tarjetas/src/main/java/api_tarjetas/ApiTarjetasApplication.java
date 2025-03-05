package api_tarjetas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ApiTarjetasApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiTarjetasApplication.class, args);
	}

}
