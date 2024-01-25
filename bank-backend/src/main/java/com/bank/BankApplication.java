package com.bank;

import com.bank.api.account.interfaces.AccountService;
import com.bank.api.auth.interfaces.AuthService;
import com.bank.api.transfer.data.TransferRequest;
import com.bank.api.transfer.interfaces.TransferService;
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
	public CommandLineRunner exampleData(AuthService authService,
										 AccountService accountService,
										 TransferService transferService) {
		return args -> createExampleUsersAndAccountsAndTransfers(authService, accountService, transferService);

	}

	private static void createExampleUsersAndAccountsAndTransfers(AuthService authService,
																  AccountService accountService,
																  TransferService transferService) {
		String username = "user123";
		String password = "abcdefghijklmnoprsuwxyz";

		User firstUser = authService.register(username, password);

		username = "user234";
		password = "1234567890abcdeghijkl";

		User secondUser = authService.register(username, password);

		String firstAccountNumber = "1111111111111111";
		String idNumber = "AAA12345678";
		String cardNumber = "1234 1234 1234 1234";
		double balance = 100.0;

		accountService.registerAccount(firstAccountNumber, idNumber, cardNumber, balance, firstUser);

		String secondAccountNumber = "9999888877776666";
		idNumber = "ABC98765432";
		cardNumber = "1337 2008 5543 9893";
		balance = 200.0;

		accountService.registerAccount(secondAccountNumber, idNumber, cardNumber, balance, secondUser);

		sendTransfers(18, 1.5, firstUser, secondAccountNumber, transferService);
		sendTransfers(9, 2.5, secondUser, firstAccountNumber, transferService);
	}

	private static void sendTransfers(int count, double amount, User user,
									  String accountNum, TransferService transferService) {

		for(int i = 1; i <= count; i++) {
			TransferRequest transferRequest = TransferRequest.builder()
					.title(String.format("Transfer no. %d", i))
					.receiverAccountNumber(accountNum)
					.receiverName("Mr. Doe")
					.amount(amount)
					.build();

			transferService.makeATransfer(user, transferRequest);
		}
	}
}
