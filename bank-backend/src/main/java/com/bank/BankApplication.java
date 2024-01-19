package com.bank;

import com.bank.api.auth.interfaces.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class BankApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankApplication.class, args);
	}

	@Bean
	public CommandLineRunner exampleData(AuthService authService) {
		return args -> registerExampleUser(authService);
	}

	private static void registerExampleUser(AuthService authService) {
		log.info("Registering user");
		String username = "user123";
		String password = "abcdefghijklmnoprsuwxyz";
		authService.register(username, password);
		log.info("User registered");
	}

}
