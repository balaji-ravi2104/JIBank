package com.banking.controller;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.banking.model.Account;
import com.banking.model.AccountStatus;
import com.banking.model.Customer;
import com.banking.model.Employee;
import com.banking.model.Transaction;
import com.banking.model.User;
import com.banking.model.UserType;
import com.banking.utils.CommonUtils;
import com.banking.utils.CommonUtils.Field;
import com.banking.utils.CustomException;
import com.banking.utils.DateUtils;
import com.banking.utils.PasswordGenerator;
import com.banking.utils.ThreadLocalStroage;
import com.banking.view.AccountView;
import com.banking.view.MainView;
import com.banking.view.TransactionView;
import com.banking.view.UserView;

public class MainController {
	private static final Logger log = Logger.getLogger(MainController.class.getName());

	private MainView mainView;
	private UserView userView;
	private AccountView accountView;
	private TransactionView transactionView;
	public UserController userController;
	public AccountController accountController;
	public TransactionController transactionController;
	public BranchController branchController;
	private boolean isAppliactionAlive;
	private boolean isLoggedIn;

	public MainController() {
		this.mainView = new MainView();
		this.userView = new UserView();
		this.accountView = new AccountView();
		this.transactionView = new TransactionView();
		this.userController = new UserController(new AccountController());
		this.accountController = new AccountController(new UserController());
		this.branchController = new BranchController();
		this.transactionController = new TransactionController();
		this.isLoggedIn = false;
	}

	public void startApplication() {
		this.isAppliactionAlive = true;
		while (isAppliactionAlive) {
			try {
				mainView.displayWelcomeMessage();
				log.info("1. Login");
				log.info("2. Logout");
				log.info("3. Exit");
				log.info("Enter your choice: ");
				int choice = mainView.promptForMainMenuChoice();
				switch (choice) {
				case 1:
					if (!isLoggedIn) {
						login();
					} else {
						log.info("You are already logged in!");
					}
					break;
				case 2:
					if (isLoggedIn) {
						logout();
					} else {
						log.info("You are not Logged In !!");
					}
					break;
				case 3:
					isAppliactionAlive = false;
					log.log(Level.WARNING, "Exiting");
					break;
				default:
					log.info("Invalid choice. Please try again.");
					break;
				}
			} catch (InputMismatchException e) {
				mainView.displayInputMissMatchMessage();
				mainView.promptNewLine();
				continue;
			} catch (Exception e) {
				mainView.displayExceptionMessage(e);
				mainView.promptNewLine();
				continue;
			}
		}
	}

	private void login() {
		while (!isLoggedIn) {
			try {
				log.info("Please login to continue:");
				int userId = mainView.promptForUserID();
				if (userId <= 0) {
					throw new IllegalArgumentException("UserId must be greater the ZERO!!!");
				}
				mainView.promptNewLine();
				String password = mainView.promptForPassword();

				User user = userController.login(userId, password);
				if (user != null && user.getStatus() == AccountStatus.INACTIVE) {
					log.warning("Your Login Account Has Been Blocked!! Please Contact Bank!!");
					break;
				}
				if (user != null) {
					log.info("Logged in Successfully!!");
					ThreadLocalStroage.setUser(user);
					isLoggedIn = true;
					UserType userType = user.getTypeOfUser();
					if (userType == UserType.CUSTOMER) {
						performCustomerOperations(user);
					} else if (userType == UserType.EMPLOYEE) {
						performEmployeeOperations(user);
					} else {
						preformAdminOperation(user);
					}
				} else {
					log.info("Invalid username or password!");
				}
			} catch (IllegalArgumentException e) {
				log.log(Level.WARNING, e.getMessage());
				mainView.promptNewLine();
				continue;
			} catch (InputMismatchException e) {
				mainView.displayInputMissMatchMessage();
				mainView.promptNewLine();
				continue;
			} catch (Exception e) {
				mainView.displayExceptionMessage(e);
				mainView.promptNewLine();
				continue;
			}
		}
	}

	private void performCustomerOperations(User user) {
		Account selectedAccount = accountSelectionOperation(user, true);
		boolean isActiveAccount = false;
		if (selectedAccount == null) {
			log.info("Error While Choosing Primary Account!!!");
			return;
		}
		if (selectedAccount.getAccountStatus() == AccountStatus.ACTIVE) {
			isActiveAccount = true;
		}
		boolean isCustomerAlive = true;
		while (isCustomerAlive) {
			try {
				log.info("Customer Operation");
				log.info("1.View My Profile");
				log.info("2.Account Details");
				log.info("3.View Balance");
				log.info("4.Deposite");
				log.info("5.Withdraw");
				log.info("6.Transfer Within Bank");
				log.info("7.Transfer with other Bank");
				log.info("8.Take Statement");
				log.info("9.Change Password");
				log.info("10.Switch Account");
				log.info("11.Exit");
				log.info("Enter Your Choice");
				int customerChoice = mainView.promptForMainMenuChoice();
				mainView.promptNewLine();

				switch (customerChoice) {
				case 1:
					log.info("1.View My Profile");
					userView.displayUserProfile(user);
					break;
				case 2:
					log.info("2.Account Details");
					accountView.displayAccountDetails(selectedAccount);
					break;
				case 3:
					log.info("3.View Balance");
					accountView.displayBalance(selectedAccount);
					break;
				case 4:
					log.info("4.Deposite Money");
					if (!isActiveAccount) {
						accountView.displayAccountsInActiveMessage();
						break;
					}
					log.info("Enter the Amount to Deposite");
					double amountToDeposite = mainView.promptDoubleInput();
					boolean isAmountDeposited = transactionController.depositAmount(selectedAccount, amountToDeposite);
					if (isAmountDeposited) {
						transactionView.transactionMessages("Amount Deposited Successfully!!");
					} else {
						transactionView.transactionMessages("Amount Deposit Failed!! Try Again!!");
					}
					break;
				case 5:
					log.info("5.Withdraw Money!!");
					if (!isActiveAccount) {
						accountView.displayAccountsInActiveMessage();
						break;
					}
					log.info("Enter the Amount to Withdraw");
					double amountToWithdraw = mainView.promptDoubleInput();
					boolean isAmountWithdrawed = transactionController.withdrawAmount(selectedAccount,
							amountToWithdraw);
					if (isAmountWithdrawed) {
						transactionView.transactionMessages("Amount Withdrawed Successfully!!");
					} else {
						transactionView.transactionMessages("Amount Withdraw Failed!! Try Again!!");
					}
					break;
				case 6:
					log.info("6.Transfer Within Bank");
					if (!isActiveAccount) {
						accountView.displayAccountsInActiveMessage();
						break;
					}
					log.info("Enter the Account Number to Transfer the Amount");
					String accountNumber = mainView.promptStringInput();
					log.info("Enter the Branch Id");
					int branchId = mainView.promtForIntegerInput();
					log.info("Enter the Amount to Transfer");
					double amountToTransfer = mainView.promptDoubleInput();
					mainView.promptNewLine();
					log.info("Enter the Small Description");
					String remark = mainView.promptStringInput();
					Account accountToTransfer = accountController.getAccountDetails(accountNumber, branchId);
					// System.out.println(accountToTransfer);
					if (accountToTransfer == null) {
						transactionView.transactionMessages("Transaction Failed!!! Try Again!!");
						break;
					}
					boolean isTransactionSuccess = transactionController.transferWithinBank(selectedAccount,
							accountToTransfer, amountToTransfer, remark);
					if (isTransactionSuccess) {
						transactionView.transactionMessages("Transaction Successfull!!!");
					} else {
						transactionView.transactionMessages("Transaction Failed!!! Try Again!!");
					}
					break;
				case 7:
					log.info("7.Transfer With Other Bank!!");
					if (!isActiveAccount) {
						accountView.displayAccountsInActiveMessage();
						break;
					}
					log.info("Enter the Account Number to Transfer the Amount");
					String accountNumberToTransfer = mainView.promptStringInput();
					log.info("Enter the Amount to Transfer");
					double amountToTransferWithOtherBank = mainView.promptDoubleInput();
					mainView.promptNewLine();
					log.info("Enter the Small Description");
					remark = mainView.promptStringInput();
					boolean isTransferSuccess = transactionController.transferWithOtherBank(selectedAccount,
							accountNumberToTransfer, amountToTransferWithOtherBank, remark);
					if (isTransferSuccess) {
						transactionView.transactionMessages("Transaction Successfull!!!");
					} else {
						transactionView.transactionMessages("Transaction Failed!!! Try Again!!");
					}
					break;
				case 8:
					log.info("8.Get Statement");
					log.info("Enter the Number of Months to get the Statement(1 to 6)");
					int numberOfMonths = mainView.promtForIntegerInput();
					List<Transaction> statement = transactionController.getStatement(selectedAccount, numberOfMonths);
					if (statement == null) {
						transactionView.transactionMessages("Statement Taken Failed !! Try Again!");
						break;
					}
					if (statement.isEmpty()) {
						transactionView.transactionMessages("No Statement Avaliable For your Account!!!");
						break;
					}
					transactionView.displayStatements(statement);
					break;
				case 9:
					log.info("9.Update Password");
					log.info("Enter the Password to Change");
					String password = mainView.promptStringInput();
					boolean isPasswordUpdated = userController.updatePassword(user.getUserId(), password);
					if (isPasswordUpdated) {
						userView.userViewMessages("Password Updated Successfully!!");
					} else {
						userView.userViewMessages("Password Updated Failed!!");
					}
					break;
				case 10:
					log.info("10.Switch Account");
					selectedAccount = accountSelectionOperation(user, false);
					isActiveAccount = false;
					if (selectedAccount.getAccountStatus() == AccountStatus.ACTIVE) {
						isActiveAccount = true;
					}
					break;
				case 11:
					isCustomerAlive = false;
					log.info("Exiting!!!");
					break;
				default:
					log.info("Invalid option! Please choose again.");
					break;
				}
			} catch (InputMismatchException e) {
				mainView.displayInputMissMatchMessage();
				mainView.promptNewLine();
				continue;
			} catch (Exception e) {
				mainView.displayExceptionMessage(e);
				mainView.promptNewLine();
				continue;
			}
		}
	}

	public Account accountSelectionOperation(User user, boolean flag) {
		Account selectedAccount = null;
		boolean isAccountSelected = false;
		try {
			List<Account> accounts = accountController.getAccountsOfCustomer(user.getUserId());
			if (accounts.isEmpty()) {
				log.info("You don't have any accounts.");
				return selectedAccount;
			}
			if (flag) {
				for (Account account : accounts) {
					if (account.isPrimaryAccount()) {
						selectedAccount = account;
						break;
					}
				}
				return selectedAccount;
			}
			Map<Integer, Account> accountMap = new HashMap<>();
			int accountNumber = 1;
			log.info("Your accounts:");
			for (Account account : accounts) {
				log.info("((" + accountNumber + "))");
				accountView.displayAccountDetails(account);
				accountMap.put(accountNumber, account);
				accountNumber++;
			}
			while (!isAccountSelected) {
				log.info("Please choose an account to continue:");
				int selectedAccountNumber = mainView.promptForAccountNumber();
				selectedAccount = accountMap.get(selectedAccountNumber);
				if (selectedAccount == null) {
					log.info("Invalid account selected! Please enter a valid choice Or INACTIVE ACCOUNT SELECTED!!!.");
				} else {
					log.info("Account Selected Successfully");
					isAccountSelected = true;
				}
			}
		} catch (CustomException e) {
			mainView.displayExceptionMessage(e);
		}
		return selectedAccount;
	}

	private void performEmployeeOperations(User user) {
		int employeeBranchId = 0;
		try {
			employeeBranchId = userController.getEmployeeBranch(user.getUserId());
		} catch (CustomException e) {
			mainView.displayExceptionMessage(e);
		}
		boolean isEmployeeAlive = true;
		while (isEmployeeAlive) {
			try {
				log.info("Employee Operations");
				log.info("1.Create Customer");
				log.info("2.Create Account");
				log.info("3.Update Customer Details");
				log.info("4.View Particular Customer Details");
				log.info("5.View Particular Customer All Details Within Branch");	
				log.info("6.View Transaction History For a Particular Customer(Account)");
				log.info("7.View All Transaction of A Customer In Branch");
				log.info("8.Update Password");
				log.info("9.Deposie Amount For the Customer");
				log.info("10.ACTIVATE or DE-ACTIVATE Customer Bank Account");
				log.info("11.Exit");
				log.info("Enter the choice");
				int employeeChoice = mainView.promptForMainMenuChoice();
				mainView.promptNewLine();
				switch (employeeChoice) {
				case 1:
					log.info("1.Create Customer");
					String password = PasswordGenerator.generatePassword();
					log.info("Enter the First Name");
					String firstName = mainView.promptStringInput();
					log.info("Enter the Last Name");
					String lastName = mainView.promptStringInput();
					log.info("Enter the Gender");
					String gender = mainView.promptStringInput();
					log.info("Enter the Email");
					String email = mainView.promptStringInput();
					log.info("Enter the Contact Number");
					String number = mainView.promptStringInput();
					log.info("Enter the Address");
					String address = mainView.promptStringInput();
					log.info("Enter the date of birth(YYYY-MM-DD)");
					String dob = mainView.promptStringInput().trim();
					long dateOfBirth = DateUtils.formatDate(DateUtils.formatDateString(dob));
					log.info("Enter the PAN Number");
					String panNumber = mainView.promptStringInput();
					log.info("Enter the Aadhar Number");
					String aadharNumber = mainView.promptStringInput();
					Customer newCustomer = new Customer();
					newCustomer.setPassword(password);
					newCustomer.setFirstName(firstName);
					newCustomer.setLastName(lastName);
					newCustomer.setGender(gender);
					newCustomer.setEmail(email);
					newCustomer.setContactNumber(number);
					newCustomer.setAddress(address);
					newCustomer.setDateOfBirth(dateOfBirth);
					newCustomer.setTypeOfUser(UserType.CUSTOMER.getValue());
					newCustomer.setPanNumber(panNumber);
					newCustomer.setAadharNumber(aadharNumber);
					boolean isUserCreated = userController.registerNewCustomer(newCustomer);
					if (isUserCreated) {
						userView.userViewMessages("User Created Successfully!!");
					} else {
						userView.userViewMessages("User Creation Failed!! Try Again!!");
					}
					break;
				case 2:
					log.info("2.Create Account");
					log.info("Enter the userID");
					int userId = mainView.promptForUserID();
					mainView.promptNewLine();
					log.info("Enter the Balance");
					double balance = mainView.promptDoubleInput();
					accountView.displayAccountTypes();
					log.info("Enter the Account Type");
					int type = mainView.promtForIntegerInput();
					Account account = new Account();
					account.setUserId(userId);
					account.setBranchId(employeeBranchId);
					account.setAccountType(type);
					account.setBalance(balance);
					boolean isAccountCreated = accountController.createAccount(account);
					if (isAccountCreated) {
						accountView.accountViewMessages("Account Created Successfully!!!");
					} else {
						accountView.accountViewMessages("Account Creation Failed!! Try Again!!");
					}
					break;
				case 3:
					log.info("3.Update User Details");
					Map<Integer, Field> fieldMap = CommonUtils.generateFieldMap();
					mainView.displayFieldName(fieldMap);
					log.info("Enter the UserId to Update");
					int userIdToUpdate = mainView.promptForUserID();
					Map<Field, Object> fieldsToUpdate = new HashMap<>();
					log.info("Enter the Number Of Field To be Updated");
					int count = mainView.promtForIntegerInput(); 
					log.info("Please Enter the Field Number to Update");
					for (int i = 1; i <= count; i++) {
						log.info("Enter the choice(Number) from the list");
						int choice = mainView.promtForIntegerInput();
						if (choice < 0 || choice > 10) {
							throw new CustomException(
									"The Field Selection Choice Should be greater than Zero or Less than Ten!!");
						}
						mainView.promptNewLine();
						if (choice == 10) {
							log.info("1.ACTIVE");
							log.info("2.INACTIVE");
							log.info("Enter the Value to Update");
							int subChoice = mainView.promtForIntegerInput();
							if (subChoice == 1) {
								fieldsToUpdate.put(fieldMap.get(choice), AccountStatus.ACTIVE.name());
							} else if (subChoice == 2) {
								fieldsToUpdate.put(fieldMap.get(choice), AccountStatus.INACTIVE.name());
							} else {
								throw new CustomException(
										"The Field Selection Choice Should be greater than Zero or Less than Two!!");
							}
							continue;
						}
						log.info("Enter the Value to Update");
						String value = mainView.promptStringInput();
						if (choice == 7) {
							fieldsToUpdate.put(fieldMap.get(choice),
									DateUtils.formatDate(DateUtils.formatDateString(value)));
							continue;
						}
						fieldsToUpdate.put(fieldMap.get(choice), value);
					}
					if (fieldsToUpdate.size() == count) {
						boolean isUserUpdated = userController.updateCustomer(userIdToUpdate, fieldsToUpdate);
						if (isUserUpdated) {
							userView.userViewMessages("Customer Details Updated Successfully!!");
						} else {
							userView.userViewMessages("Customer Updation Failed!! Try Again!!");
						}
					}
					break;
				case 4:
					log.info("4.View Particual Customer Details");
					log.info("Enter the Account Number");
					String accountNumber = mainView.promptStringInput();
					Customer customerDetail = userController.getCustomerDetails(accountNumber, employeeBranchId);
					if (customerDetail == null) {
						userView.userViewMessages("Customer Details Reterving Failed!! Please Try Again!!");
						break;
					}
					Account customerAccount = accountController.getAccountDetails(accountNumber, employeeBranchId);
					if (customerAccount == null) {
						userView.userViewMessages("Customer Account Details Reterving Failed!! Please Try Again!!");
						break;
					}
					userView.displayCustomerDetails(customerDetail);
					accountView.displayAccountDetails(customerAccount);
					break;
				case 5:
					log.info("5.View Details of One Customer in Branch");
					log.info("Enter the userID");
					userId = mainView.promptForUserID();
					Customer customerDetails = userController.getCustomerDetailsById(userId, employeeBranchId);
					if (customerDetails == null) {
						userView.userViewMessages("Customer Details Reterving Failed!! Please Try Again!!");
						break;
					}
					Map<String, Account> accountDetails = accountController.getCustomerAccountsInBranch(userId,
							employeeBranchId);
					if (accountDetails == null) {
						userView.userViewMessages("Customer Account Details Reterving Failed!! Please Try Again!!");
						break;
					}
					userView.displayCustomerDetails(customerDetails);
					accountView.displayAllAccounts(accountDetails);
					break;
				case 6:
					log.info("6.View Transaction History of a Particular Account");
					log.info("Enter the Account Number to Get Transaction History");
					String accountNumberToGetTransaction = mainView.promptStringInput();
					log.info("Enter the number of months to view the customer's transaction history:");
					int month = mainView.promtForIntegerInput();
					List<Transaction> transactionsHistory = transactionController
							.getCustomerTransaction(accountNumberToGetTransaction, employeeBranchId, month);
					if (transactionsHistory == null) {
						transactionView.transactionMessages("Transaction History Taken Failed!!!");
						break;
					}
					transactionView.displayTransactionHistory(transactionsHistory);
					break;
				case 7:
					log.info("7.View All Transaction of A Customer In Branch");
					log.info("Enter the Customer Id");
					userId = mainView.promtForIntegerInput();
					log.info("Enter the number of months to view the customer's transaction history:");
					month = mainView.promtForIntegerInput();
					Map<String, List<Transaction>> allTransactionsOfCustomer = transactionController
							.getAllTransactionsOfCustomer(userId, employeeBranchId, month);
					if (allTransactionsOfCustomer == null) {
						transactionView.transactionMessages("Transaction History Taken Failed!!!");
						break;
					}
					transactionView.displayAllTransActionHistory(allTransactionsOfCustomer);
					break;
				case 8:
					log.info("8.Update Password");
					log.info("Enter the Password to Change");
					password = mainView.promptStringInput();
					boolean isPasswordUpdated = userController.updatePassword(user.getUserId(), password);
					if (isPasswordUpdated) {
						userView.userViewMessages("Password Updated Successfully!!");
					} else {
						userView.userViewMessages("Password Updated Failed!!");
					}
					break;
				case 9:
					log.info("9.Deposie Amount For the Customer");
					log.info("Enter the Account Number");
					accountNumber = mainView.promptStringInput();
					log.info("Enter the Amount to Deposite");
					double amountToDeposite = mainView.promptDoubleInput();
					Account accountToDeposite = accountController.getAccountDetails(accountNumber, employeeBranchId);
					// System.out.println(accountToDeposite);
					if (accountToDeposite.getAccountStatus() == AccountStatus.INACTIVE) {
						transactionView
								.transactionMessages("The Account is INACTIVE!! Please Try With Different Account!!");
						break;
					}
					boolean isAmountDeposited = transactionController.depositAmount(accountToDeposite,
							amountToDeposite);
					if (isAmountDeposited) {
						transactionView.transactionMessages("Amount Deposited Successfully!!");
					} else {
						transactionView.transactionMessages("Amount Deposit Failed!! Try Again!!");
					}
					break;
				case 10:
					log.info("10.ACTIVATE or DE-ACTIVATE Customer Bank Account");
					log.info("Enter the Account number");
					accountNumber = mainView.promptStringInput();
					accountView.displayAccountStatus();
					log.info("Choose the Status to Update");
					log.info("Enter the Value to Update");
					int statusChoice = mainView.promtForIntegerInput();
					boolean isAccountStatusChanged = accountController.activateDeactivateCustomerAccount(accountNumber,
							employeeBranchId, statusChoice);
					if (isAccountStatusChanged) {
						accountView.accountViewMessages("Bank Account Status Updated SuccessFully!!!");
					} else {
						accountView.accountViewMessages("Bank Account Status Updation Failed!!! Tyr Again!!");
					}
					break;
				case 11:
					isEmployeeAlive = false;
					log.info("Exiting!");
					break;
				default:
					log.info("Invalid option! Please choose again.");
					break;
				}
			} catch (InputMismatchException e) {
				mainView.displayInputMissMatchMessage();
				mainView.promptNewLine();
				continue;
			} catch (Exception e) {
				mainView.displayExceptionMessage(e);
				mainView.promptNewLine();
				continue;
			}
		}
	}

	private void preformAdminOperation(User user) {
		boolean isAdminAlive = true;
		while (isAdminAlive) {
			try {
				log.info("Admin Operations");
				log.info("1. Add new employee");
				log.info("2. View Particular Employee details");
				log.info("3. View All Employees in One Branch");
				log.info("4. View All Employees From Accross All Branch");
				log.info("5. Create Customer");
				log.info("6. Create Account");
				log.info("7. ACTIVATE or DE-ACTIVATE Customer Bank Account");
				log.info("8. View Particular Customer(Account) Details");
				log.info("9. View Details of Customer in Branch");
				log.info("10. View Details of Customer in All Branch");
				log.info("11. View Particular Customer Transaction by Account");
				log.info("12. View Particular Customers One Branch Transaction");
				log.info("13. Update Password");
				log.info("14. Update Customer Details");
				log.info("15. Exit");
				log.info("Enter the choice");
				int adminChoice = mainView.promptForMainMenuChoice();
				mainView.promptNewLine();
				switch (adminChoice) {
				case 1:
					log.info("1. Add new employee");
					String password = PasswordGenerator.generatePassword();
					log.info("Enter the First Name");
					String firstName = mainView.promptStringInput();
					log.info("Enter the Last Name");
					String lastName = mainView.promptStringInput();
					log.info("Enter the Gender");
					String gender = mainView.promptStringInput();
					log.info("Enter the Email");
					String email = mainView.promptStringInput();
					log.info("Enter the Contact Number");
					String number = mainView.promptStringInput();
					log.info("Enter the Address");
					String address = mainView.promptStringInput();
					log.info("Enter the date of birth(YYYY-MM-DD)");
					String dob = mainView.promptStringInput();
					long dateOfBirth = DateUtils.formatDate(DateUtils.formatDateString(dob));
					log.info("Enter the Branch Id:");
					int branchId = mainView.promtForIntegerInput();
					Employee newEmployee = new Employee();
					newEmployee.setPassword(password);
					newEmployee.setFirstName(firstName);
					newEmployee.setLastName(lastName);
					newEmployee.setGender(gender);
					newEmployee.setEmail(email);
					newEmployee.setContactNumber(number);
					newEmployee.setAddress(address);
					newEmployee.setDateOfBirth(dateOfBirth);
					newEmployee.setTypeOfUser(UserType.EMPLOYEE.getValue());
					newEmployee.setBranchId(branchId);
					boolean isEmployeeCreated = userController.registerNewEmployee(newEmployee);
					if (isEmployeeCreated) {
						userView.userViewMessages("User Created Successfully!!");
					} else {
						userView.userViewMessages("User Creation Failed!! Try Again!!");
					}
					break;
				case 2:
					log.info("2. View Particular Employee details");
					log.info("Enter the Employee Id");
					int employeeId = mainView.promtForIntegerInput();
					Employee employeeDetails = userController.getEmployeeDetails(employeeId);
					if (employeeDetails == null) {
						userView.userViewMessages("Employee Detail Reterving Failed!! Please Try Again!!");
						break;
					}
					userView.displayEmployeeProfile(employeeDetails);
					break;
				case 3:
					log.info("3. View All Employees in One Branch");
					log.info("Enter the Branch Id");
					int branchIdToGetEmployees = mainView.promtForIntegerInput();
					Map<Integer, Employee> employeesList = userController
							.getEmployeeFromOneBranch(branchIdToGetEmployees);
					if (employeesList == null) {
						userView.userViewMessages("Employees Details Reterving Failed!! Please Try Again!!");
						break;
					}
					userView.displayListOfEmployees(employeesList);
					break;
				case 4:
					log.info("4. View All Employees From Accross All Branch");
					Map<Integer, Map<Integer, Employee>> allEmployeesList = userController.getEmployeeFromAllBranch();
					if (allEmployeesList == null) {
						userView.userViewMessages("Employees Details Reterving Failed!! Please Try Again!!");
						break;
					}
					userView.displayEmployeesByBranch(allEmployeesList);
					break;
				case 5:
					log.info("5.Create Customer");
					password = PasswordGenerator.generatePassword();
					log.info("Enter the First Name");
					firstName = mainView.promptStringInput();
					log.info("Enter the Last Name");
					lastName = mainView.promptStringInput();
					log.info("Enter the Gender");
					gender = mainView.promptStringInput();
					log.info("Enter the Email");
					email = mainView.promptStringInput();
					log.info("Enter the Contact Number");
					number = mainView.promptStringInput();
					log.info("Enter the Address");
					address = mainView.promptStringInput();
					log.info("Enter the date of birth(YYYY-MM-DD)");
					dob = mainView.promptStringInput();
					long dateofBirth = DateUtils.formatDate(DateUtils.formatDateString(dob));
					log.info("Enter the PAN Number");
					String panNumber = mainView.promptStringInput();
					log.info("Enter the Aadhar Number");
					String aadharNumber = mainView.promptStringInput();
					Customer newCustomer = new Customer();
					newCustomer.setPassword(password);
					newCustomer.setFirstName(firstName);
					newCustomer.setLastName(lastName);
					newCustomer.setGender(gender);
					newCustomer.setEmail(email);
					newCustomer.setContactNumber(number);
					newCustomer.setAddress(address);
					newCustomer.setDateOfBirth(dateofBirth);
					newCustomer.setTypeOfUser(UserType.CUSTOMER.getValue());
					newCustomer.setPanNumber(panNumber);
					newCustomer.setAadharNumber(aadharNumber);
					boolean isUserCreated = userController.registerNewCustomer(newCustomer);
					if (isUserCreated) {
						userView.userViewMessages("User Created Successfully!!");
					} else {
						userView.userViewMessages("User Creation Failed!! Try Again!!");
					}
					break;
				case 6:
					log.info("6. Create Account");
					log.info("Enter the userID");
					int userId = mainView.promptForUserID();
					mainView.promptNewLine();
					log.info("Enter the Branch Id");
					branchId = mainView.promtForIntegerInput();
					log.info("Enter the Balance");
					double balance = mainView.promptDoubleInput();
					accountView.displayAccountTypes();
					log.info("Enter the Account Type");
					int type = mainView.promtForIntegerInput();
					Account account = new Account();
					account.setUserId(userId);
					account.setBranchId(branchId);
					account.setAccountType(type);
					account.setBalance(balance);
					boolean isAccountCreated = accountController.createAccount(account);
					if (isAccountCreated) {
						accountView.accountViewMessages("Account Created Successfully!!!");
					} else {
						accountView.accountViewMessages("Account Creation Failed!! Try Again!!");
					}
					break;
				case 7:
					log.info("7. ACTIVATE or DE-ACTIVATE Customer Bank Account");
					log.info("Enter the Account number");
					String accountNumber = mainView.promptStringInput();
					log.info("Enter the Branch Id");
					branchId = mainView.promtForIntegerInput();
					accountView.displayAccountStatus();
					log.info("Choose the Status to Update");
					log.info("Enter the Value to Update");
					int statusChoice = mainView.promtForIntegerInput();
					boolean isAccountStatusChanged = accountController.activateDeactivateCustomerAccount(accountNumber,
							branchId, statusChoice);
					if (isAccountStatusChanged) {
						accountView.accountViewMessages("Bank Account Status Updated SuccessFully!!!");
					} else {
						accountView.accountViewMessages("Bank Account Status Updation Failed!!! Tyr Again!!");
					}
					break;
				case 8:
					log.info("8. View Particular Customer(Account) Details");
					log.info("Enter the Account Number");
					accountNumber = mainView.promptStringInput();
					log.info("Enter the Branch Id");
					branchId = mainView.promtForIntegerInput();
					Customer customerDetail = userController.getCustomerDetails(accountNumber, branchId);
					if (customerDetail == null) {
						userView.userViewMessages("Customer Details Reterving Failed!! Please Try Again!!");
						break;
					}
					Account customerAccount = accountController.getAccountDetails(accountNumber, branchId);
					if (customerAccount == null) {
						userView.userViewMessages("Customer Account Details Reterving Failed!! Please Try Again!!");
						break;
					}
					userView.displayCustomerDetails(customerDetail);
					accountView.displayAccountDetails(customerAccount);
					break;
				case 9:
					log.info("9.View Details of One Customer in Branch");
					log.info("Enter the userID");
					userId = mainView.promptForUserID();
					log.info("Enter the branch Id");
					branchId = mainView.promtForIntegerInput();
					Customer customerDetails = userController.getCustomerDetailsById(userId, branchId);
					if (customerDetails == null) {
						userView.userViewMessages("Customer Details Reterving Failed!! Please Try Again!!");
						break;
					}
					Map<String, Account> accountDetails = accountController.getCustomerAccountsInBranch(userId,
							branchId);
					if (accountDetails == null) {
						userView.userViewMessages("Customer Account Details Reterving Failed!! Please Try Again!!");
						break;
					}
					userView.displayCustomerDetails(customerDetails);
					accountView.displayAllAccounts(accountDetails);
					break;
				case 10:
					log.info("10.View Details of Customer in All Branch");
					log.info("Enter the userID");
					userId = mainView.promptForUserID();
					Customer customer = userController.getCustomerDetailsById(userId);
					if (customer == null) {
						userView.userViewMessages("Customer Details Reterving Failed!! Please Try Again!!");
						break;
					}
					Map<Integer, Map<String, Account>> allAccountDetails = accountController
							.getCustomerAccountsInAllBranch(userId);
					if (allAccountDetails == null) {
						userView.userViewMessages("Customer Account Details Reterving Failed!! Please Try Again!!");
						break;
					}
					userView.displayCustomerDetails(customer);
					accountView.displayCustomersAllBranchAccount(allAccountDetails);
					break;
				case 11:
					log.info("11. View Particular Customer Transaction by Account");
					log.info("Enter the Account Number to Get Transaction History");
					String accountNumberToGetTransaction = mainView.promptStringInput();
					log.info("Enter the Branch Id");
					branchId = mainView.promtForIntegerInput();
					log.info("Enter the number of months to view the customer's transaction history:");
					int month = mainView.promtForIntegerInput();
					List<Transaction> transactionsHistory = transactionController
							.getCustomerTransaction(accountNumberToGetTransaction, branchId, month);
					if (transactionsHistory == null) {
						log.warning("Transaction History Taken Failed!!!");
						break;
					}
					transactionView.displayTransactionHistory(transactionsHistory);
					break;
				case 12:
					log.info("12. View Particular Customers One Branch Transaction");
					log.info("Enter the Customer Id");
					userId = mainView.promtForIntegerInput();
					log.info("Enter the Branch Id");
					branchId = mainView.promtForIntegerInput();
					log.info("Enter the number of months to view the customer's transaction history:");
					month = mainView.promtForIntegerInput();
					Map<String, List<Transaction>> allTransactionsOfCustomer = transactionController
							.getAllTransactionsOfCustomer(userId, branchId, month);
					if (allTransactionsOfCustomer == null) {
						log.warning("Transaction History Taken Failed!!!");
						break;
					}
					transactionView.displayAllTransActionHistory(allTransactionsOfCustomer);
					break;
				case 13:
					log.info("13. Update Password");
					log.info("Enter the Password to Change");
					password = mainView.promptStringInput();
					boolean isPasswordUpdated = userController.updatePassword(user.getUserId(), password);
					if (isPasswordUpdated) {
						userView.userViewMessages("Password Updated Successfully!!");
					} else {
						userView.userViewMessages("Password Updated Failed!!");
					}
					break;
				case 14:
					log.info("14. Update Customer Details");
					Map<Integer, Field> fieldMap = CommonUtils.generateFieldMap();
					mainView.displayFieldName(fieldMap);
					log.info("Enter the UserId to Update");
					int userIdToUpdate = mainView.promptForUserID();
					Map<Field, Object> fieldsToUpdate = new HashMap<>();
					log.info("Enter the Number Of Field To be Updated");
					int count = mainView.promtForIntegerInput();
					log.info("Please Enter the Field Number to Update");
					for (int i = 1; i <= count; i++) {
						log.info("Enter the choice(Number) from the list");
						int choice = mainView.promtForIntegerInput();
						if (choice < 0 || choice > 10) {
							throw new CustomException(
									"The Field Selection Choice Should be greater than Zero or Less than Ten!!");
						}
						mainView.promptNewLine();
						if (choice == 10) {
							log.info("1.ACTIVE");
							log.info("2.INACTIVE");
							log.info("Enter the Value to Update");
							int subChoice = mainView.promtForIntegerInput();
							if (subChoice == 1) {
								fieldsToUpdate.put(fieldMap.get(choice), AccountStatus.ACTIVE.name());
							} else if (subChoice == 2) {
								fieldsToUpdate.put(fieldMap.get(choice), AccountStatus.INACTIVE.name());
							} else {
								throw new CustomException(
										"The Field Selection Choice Should be greater than Zero or Less than Two!!");
							}
							continue;
						}
						log.info("Enter the Value to Update");
						String value = mainView.promptStringInput();
						if (choice == 7) {
							fieldsToUpdate.put(fieldMap.get(choice),
									DateUtils.formatDate(DateUtils.formatDateString(value)));
							continue;
						}
						fieldsToUpdate.put(fieldMap.get(choice), value);
					}
					if (fieldsToUpdate.size() == count) {
						boolean isUserUpdated = userController.updateCustomer(userIdToUpdate, fieldsToUpdate);
						if (isUserUpdated) {
							userView.userViewMessages("Customer Details Updated Successfully!!");
						} else {
							userView.userViewMessages("Customer Updation Failed!! Try Again!!");
						}
					}
					break;
				case 15:
					isAdminAlive = false;
					break;
				default:
					log.info("Invalid option! Please choose again.");
					break;
				}
			} catch (IllegalArgumentException e) {
				log.warning(e.getMessage());
				mainView.promptNewLine();
				continue;
			} catch (InputMismatchException e) {
				mainView.displayInputMissMatchMessage();
				mainView.promptNewLine();
				continue;
			} catch (Exception e) {
				mainView.displayExceptionMessage(e);
				mainView.promptNewLine();
				continue;
			}
		}

	}

	private void logout() {
		isLoggedIn = false;
		log.info("Logged out successfully!");
	}
}