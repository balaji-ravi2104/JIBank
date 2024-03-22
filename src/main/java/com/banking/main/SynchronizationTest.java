package com.banking.main;

import com.banking.controller.UserController;
import com.banking.model.Customer;
import com.banking.utils.CustomException;

public class SynchronizationTest {
	public static void main(String[] args) {
		// Create an instance of userController
		// UserController userController = new UserController();

		// Define the userId to be used for testing
		int userId = 1008;

		// Create multiple threads that concurrently access the getCustomerDetailsById
		// method
		Thread thread1 = new Thread(() -> {
			try {
				UserController userController = new UserController();
				Customer customer = userController.getCustomerDetailsById(userId, 3007); // Employee purpose
				System.out.println("Thread 1: Customer Details - " + customer);
			} catch (CustomException e) {
				e.printStackTrace();
			}
		});

		Thread thread2 = new Thread(() -> {
			try {
				UserController userController = new UserController();
				Customer customer = userController.getCustomerDetailsById(userId); // Admin purpose
				System.out.println("Thread 2: Customer Details - " + customer);
			} catch (CustomException e) {
				e.printStackTrace();
			}
		});

		Thread thread3 = new Thread(() -> {
			try {
				UserController userController = new UserController();
				Customer customer = userController.getCustomerDetailsById(userId, 3007); // Employee purpose
				System.out.println("Thread 3: Customer Details - " + customer);
			} catch (CustomException e) {
				e.printStackTrace();
			}
		});

		Thread thread4 = new Thread(() -> {
			try {
				UserController userController = new UserController();
				Customer customer = userController.getCustomerDetailsById(userId); // Admin purpose
				System.out.println("Thread 4: Customer Details - " + customer);
			} catch (CustomException e) {
				e.printStackTrace();
			}
		});
		// Start the threads
		thread1.start();
		thread2.start();
		thread3.start();
		thread4.start();
	}
}
