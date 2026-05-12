package cl.eventos.ms_checkin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients

public class MsCheckinApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsCheckinApplication.class, args);
	}

}
