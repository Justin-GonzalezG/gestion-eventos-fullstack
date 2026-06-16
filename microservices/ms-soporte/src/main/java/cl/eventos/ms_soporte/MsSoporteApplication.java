package cl.eventos.ms_soporte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsSoporteApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsSoporteApplication.class, args);
	}
// Fuerza recompilacion

}
