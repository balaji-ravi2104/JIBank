package com.banking.utils;

import com.banking.model.User;

public class ThreadLocalStroage {

	private static final ThreadLocal<User> threadLocal = new ThreadLocal<User>();

	public static void setUser(User user) {
		threadLocal.set(user);
	}

	public static User getUser() {
		return threadLocal.get();
	}

}
