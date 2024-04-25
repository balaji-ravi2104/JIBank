package com.banking.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.banking.controller.AccountController;
import com.banking.controller.UserController;
import com.banking.dao.UserDao;
import com.banking.dao.implementation.UserDaoImplementation;
import com.banking.model.Account;
import com.banking.model.AccountStatus;
import com.banking.model.Customer;
import com.banking.model.TokenStatus;
import com.banking.model.User;
import com.banking.utils.CustomException;

public class APIServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static UserController userController = new UserController();
	private static AccountController accountController = new AccountController();
	private static UserDao userDao = new UserDaoImplementation();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getPathInfo();
		System.out.println("In Side Get :"+path);
		JSONObject jsonResponse = new JSONObject();
		if (path.startsWith("/user/")) {
			String token = request.getHeader("Authorization");
			System.out.println(token);
			if (!isValidToken(token)) {
				jsonResponse.put("status", "error");
				jsonResponse.put("message", "Invalid Token, Please Provide a Valid Token");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				writeJsonResponse(response, jsonResponse);
				return;
			}

			String[] pathParts = path.split("/");
			//System.out.println(Arrays.toString(pathParts));
			try {
				if (pathParts.length == 3) {
					int userId = Integer.parseInt(pathParts[2]);
					getUserDetails(userId, jsonResponse, response);
				} else if (pathParts.length == 6 && pathParts[5].equals("accounts")) {
					int userId = Integer.parseInt(pathParts[2]);
					int branchId = Integer.parseInt(pathParts[4]);
					getCustomerAccounts(userId, branchId, jsonResponse, response);
				} else {
					jsonResponse.put("status", "error");
					jsonResponse.put("message", "Invalid URL. Please provide a valid URL.");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
			} catch (Exception e) {
				jsonResponse.put("status", "error");
				jsonResponse.put("message", "Invalid URL. Please provide a valid URL."); 
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
		writeJsonResponse(response, jsonResponse);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getPathInfo();
		System.out.println("Inside Post :"+path);
		switch (path) {
		case "/login": 
			JSONObject jsonResponse = new JSONObject();
			JSONObject rootObject = getRootObject(request);
			try {
				//System.out.println("Root Object : " + rootObject);
				int userId = rootObject.optInt("userId");
				String password = rootObject.getString("password");
				User user = userController.login(userId, password);
				if (user == null) {
					jsonResponse.put("status", "error");
					jsonResponse.put("message", "Invalid User Id or Password");
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				} else if (user.getStatus() == AccountStatus.INACTIVE) {
					jsonResponse.put("status", "error");
					jsonResponse.put("message", "Your Account is InActive");
					response.setStatus(HttpServletResponse.SC_OK);
				} else {
					jsonResponse.put("status", "success");
					jsonResponse.put("message", "Welcome to JI Bank " + user.getFirstName());   
					response.setStatus(HttpServletResponse.SC_OK);
				}
			} catch (Exception e) {
				jsonResponse.put("status", "error");
				jsonResponse.put("message", "A problem occurred, Try after sometime");
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			writeJsonResponse(response, jsonResponse);
			break;
		default:
			break;
		}
	}

	private JSONObject getRootObject(HttpServletRequest request) {
		JSONObject rootObject = null;
		try(BufferedReader reader = request.getReader()){
			StringBuilder jsonRequestBuilder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				jsonRequestBuilder.append(line);
			}
			String jsonRequest = jsonRequestBuilder.toString();
			rootObject = new JSONObject(jsonRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rootObject;
	}

	public void writeJsonResponse(HttpServletResponse response, JSONObject jsonResponse) throws IOException {
		response.setContentType("application/json");
		try (PrintWriter out = response.getWriter()) {
			out.print(jsonResponse);
		}
	}

	private boolean isValidToken(String token) {
		try {
			int tokenStatus = userDao.getTokenStatus(token);
			return  tokenStatus!= 0 && TokenStatus.fromValue(tokenStatus) == TokenStatus.ACTIVE;
		} catch (CustomException e) {
			return false;
		}
	}
	
	private void getCustomerAccounts(int userId, int branchId, JSONObject jsonResponse, HttpServletResponse response) {
		try {
			Map<String, Account> customerAccounts = accountController.getCustomerAccountsInBranch(userId, branchId);
			if (customerAccounts.isEmpty()) {
				jsonResponse.put("status", "error");
				jsonResponse.put("message", "No Account Found");
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			} else {
				jsonResponse.put("status", "success");
				jsonResponse.put("data", new JSONObject(customerAccounts));
			}
		} catch (CustomException e) {
			jsonResponse.put("status", "error");
			jsonResponse.put("message", "A problem occurred, Try after sometime");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void getUserDetails(int userId, JSONObject jsonResponse, HttpServletResponse response) {
		try {
			Customer customer = userController.getCustomerDetailsById(userId);
			if (customer == null) {
				jsonResponse.put("status", "error");
				jsonResponse.put("message", "No User Found");
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			} else {
				jsonResponse.put("status", "success");
				jsonResponse.put("data", new JSONObject(customer));
			}
		} catch (Exception e) {
			jsonResponse.put("status", "error");
			jsonResponse.put("message", "A problem occurred, Try after sometime");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}
