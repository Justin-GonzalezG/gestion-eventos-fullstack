package com.eventos.cl.ms_recomendacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsRecomendacionApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsRecomendacionApplication.class, args);
	}

}
