package com.demo.debubing;

public class DebuggingWithObject {

	public static void main(String[] args) {
		
		
		User user1 = new User("Balaji");
		user1.setDept("I-AM");
		user1.setAge(21);
		//user1.setName("Baby"); crtl+shift+i for inspect and execute this line any time of the debugging
		
		User user2 = new User("Ravi");
		user2.setDept("Zoho one");
		
		user2.getDept();
		System.out.println("User 2 Age : "+user2.getAge());
		
		User[] users = new User[2];
		
		users[0] = user1;
		users[1] = user2;
		
//		for(int i=0;i<10;i++) {
//			System.out.println("Current Value : "+i); // this code is for conditional break point
//		}
	}

}

// ctrl+alt+b -> for disable all break points