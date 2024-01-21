package com.bank;

import com.bank.api.account.interfaces.AccountService;
import com.bank.api.auth.interfaces.AuthService;
import com.bank.entities.user.User;
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
	public CommandLineRunner exampleData(AuthService authService, AccountService accountService) {
		return args -> {
			User user = registerExampleUser(authService);
			createExampleAccount(accountService, user);
		};
	}

	private static User registerExampleUser(AuthService authService) {
		String username = "user123";
		String password = "abcdefghijklmnoprsuwxyz";
		return authService.register(username, password);
	}

	private static void createExampleAccount(AccountService accountService, User user) {
		String accountNumber = "1111111111111111";
		String idNumber = "PLABC123";
		String cardNumber = "1234 1234 1234 1234";
		double balance = 100.0;

		accountService.registerAccount(accountNumber, idNumber, cardNumber, balance, user);
	}

}
