package cl.eventos.ms_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient // Esto es fundamental para el descubrimiento
public class MsGatewayApplication {
	public static void main(String[] args) {
		SpringApplication.run(MsGatewayApplication.class, args);
	}
}
