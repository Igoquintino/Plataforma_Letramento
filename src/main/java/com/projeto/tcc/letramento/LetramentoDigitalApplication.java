package com.projeto.tcc.letramento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LetramentoDigitalApplication {

	public static void main(String[] args) {
//		System.out.println("Usuário do Banco: " + System.getenv("DB_USERNAME"));
		SpringApplication.run(LetramentoDigitalApplication.class, args);
	}

}
