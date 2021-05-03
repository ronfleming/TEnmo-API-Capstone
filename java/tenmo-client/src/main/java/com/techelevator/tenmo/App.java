package com.techelevator.tenmo;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.view.ConsoleService;
import io.cucumber.java.bs.A;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class App {

	private static final String API_BASE_URL = "http://localhost:8080/";

	//private static final String MENU_OPTION_EXIT = "Exit";
	private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = {LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN};
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_VIEW_TRANSFER = "View a transfer by ID";
	private static final String MAIN_MENU_OPTION_PROCESS_REQUEST = "Process a transfer request";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = {MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_VIEW_TRANSFER, MAIN_MENU_OPTION_PROCESS_REQUEST, MAIN_MENU_OPTION_LOGIN};


	private AuthenticatedUser currentUser;
	private ConsoleService console;
	private AuthenticationService authenticationService;
	private AccountService accountService;

	public static void main(String[] args) {
		App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL), new AccountService(API_BASE_URL));
		app.run();
	}

	public App(ConsoleService console, AuthenticationService authenticationService, AccountService accountService) {
		this.console = console;
		this.authenticationService = authenticationService;
		this.accountService = accountService;
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");

		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while (true) {
			String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if (MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				System.out.println("-------------------------------------------");
				System.out.println("             Select recipient");
				System.out.println("-------------------------------------------");
				User recipient = chooseRecipientMenu();
				if (recipient.getId() != null) {
					System.out.println(sendBucks(recipient.getUsername()));
				} else {
					System.out.println("Transaction cancelled.");
				}
			} else if (MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				System.out.println("-------------------------------------------");
				System.out.println("        Select user to request from");
				System.out.println("-------------------------------------------");
				User requestee = chooseRecipientMenu();
				if (requestee.getId() != null) {
					System.out.println(requestBucks(requestee.getUsername()));
				} else {
					System.out.println("Transaction cancelled.");
				}

			} else if (MAIN_MENU_OPTION_VIEW_TRANSFER.equals(choice)){
				viewTransferById();
			} else if (MAIN_MENU_OPTION_PROCESS_REQUEST.equals(choice)){
				Transfer transfer = requestsMenu();
				if (transfer.getId() != null) {
					processRequest(transfer);
				}
			} else if (MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {
		//Remove below
		linkUserToAccountNumber();
		//Remove above
		BigDecimal balance = accountService.viewCurrentBalance(currentUser.getToken());
		System.out.println("Account balance: " + NumberFormat.getCurrencyInstance().format(balance));
	}

	private void viewTransferHistory() {
		Transfer[] transfers = accountService.viewTransferHistory(currentUser.getToken());
		System.out.println("----------------------------------------------------------");
		System.out.println("Transfers");
		System.out.printf("%-13s %-22s %-13s %-12s\n", "ID", "From/To", "Amount", "Status");
		printTransferArray(transfers);
	}

	private void viewTransferById() {
		Integer transferId = console.getUserInputInteger("Enter transfer ID");
		Transfer transfer = accountService.viewTransferById(currentUser.getToken(), transferId);

		if(transfer.getId() != null) {
			System.out.println("-------------------------------------------");
			System.out.println("Transfer Details");
			System.out.println("-------------------------------------------");
			String type = transfer.getTypeId() == 2 ? "Send" : "Request";
			String status = translateStatusCode(transfer.getStatusId());

			String outputTransfer = String.format( "Id: %d \nFrom: %s \nTo: %s \nType: %s \nStatus: %s \nAmount: %s",
					transfer.getId(), transfer.getUserName(), transfer.getRecipientName(), type, status,
					NumberFormat.getCurrencyInstance().format(transfer.getAmount()));
			System.out.println(outputTransfer);

		} else {
			System.out.println("\nTransfer not found.");
		}

	}

	private void viewPendingRequests() {
		Transfer[] transfers = accountService.viewPendingRequests(currentUser.getToken());
		System.out.println("----------------------------------------------------------");
		System.out.println("Pending Transfers");
		System.out.printf("%-13s %-22s %-13s %-12s\n", "ID", "From/To", "Amount", "Status");
        printTransferArray(transfers);
	}

	private void printTransferArray(Transfer[] transfers) {
        System.out.println("----------------------------------------------------------");
        for (Transfer transfer: transfers) {
            String toFrom = "";
            String username = "";
            if (currentUser.getUser().getUsername().equals(transfer.getUserName())) {
                toFrom = "To: ";
                username = transfer.getRecipientName();
            } else {
                toFrom = "From: ";
                username = transfer.getUserName();
            }
			String status = translateStatusCode(transfer.getStatusId());

            System.out.printf("%-12d %7s %-15s $%-12.2f %s\n", transfer.getId(), toFrom, username, transfer.getAmount(), status);
        }
    }

    private String translateStatusCode(int statusCode) {
		String status = "";
		switch (statusCode) {
			case 1:
				status = "Pending";
				break;
			case 2:
				status = "Approved";
				break;
			case 3:
				status = "Rejected";
				break;
		}
		return status;
	}

	private void linkUserToAccountNumber() {
		Integer accountId = accountService.getAccountIdByUsername(currentUser.getToken());
		System.out.println("User " + currentUser.getUser().getUsername() + " has account number " + accountId);
	}

	private String sendBucks(String recipientName) {
		BigDecimal amount = console.getUserInputBigDecimal("Enter amount");
		Transfer transfer = new Transfer();
		transfer.setRecipientName(recipientName);
		transfer.setAmount(amount);
		transfer.setTypeId(2);      // transfer type Send
		return accountService.sendBucks(currentUser.getToken(), transfer);
	}

	private String requestBucks(String requesteeName) {
		BigDecimal amount = console.getUserInputBigDecimal("Enter amount");
		Transfer transfer = new Transfer();
		transfer.setUserName(requesteeName);
		transfer.setAmount(amount);
		transfer.setTypeId(1);      // transfer type Request
		return accountService.requestBucks(currentUser.getToken(), transfer);
	}

	private void processRequest(Transfer transfer) {
		String[] options = {"Approve", "Reject"};
		String choice = (String) console.getChoiceFromOptions(options);
		if (choice != null) {
			if (options[0].equals(choice)) {
				transfer.setStatusId(2);
			} else if (options[1].equals(choice)) {
				transfer.setStatusId(3);
			}
			String msg = accountService.processRequest(currentUser.getToken(), transfer);
			System.out.println(msg);
		}
	}

	private Transfer requestsMenu() {
		Transfer chosenOne = new Transfer();
		Transfer[] transferMenu = accountService.viewPendingRequests(currentUser.getToken());
		if (transferMenu.length > 0) {
			Transfer choice = (Transfer) console.getChoiceFromOptions(transferMenu);
			if (choice == null) {
				System.out.println("Process Cancelled");
				return chosenOne;
			}
			for (Transfer transfer : transferMenu) {
				if (transfer.getId() == choice.getId()) {
					chosenOne = choice;
					break;
				}
			}
		} else {
			System.out.println("No current requests");
		}
		return chosenOne;
	}

	private User chooseRecipientMenu() {
		User recipient = new User();
		User[] usersMenu = accountService.listOtherUsers(currentUser.getToken());

		User choice = (User) console.getChoiceFromOptions(usersMenu);
		if (choice != null) {
			for (User user : usersMenu) {
				if (user.getId() == choice.getId()) {
					recipient = user;
					break;
				}
			}
		}
		return recipient;
	}

	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while (!isAuthenticated()) {
			String choice = (String) console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
		while (!isRegistered) //will keep looping until user is registered
		{
			UserCredentials credentials = collectUserCredentials();
			try {
				authenticationService.register(credentials);
				isRegistered = true;
				System.out.println("Registration successful. You can now login.");
			} catch (AuthenticationServiceException e) {
				System.out.println("REGISTRATION ERROR: " + e.getMessage());
				System.out.println("Please attempt to register again.");
			}
		}
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
			try {
				currentUser = authenticationService.login(credentials);
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: " + e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}

	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}
}
