package com.demo.debubing;

public class DebuggigExample {

	public static void main(String[] args) {
		int value1 = 10;
		int value2 = 20;
		
		int sum = addToNum(value1,value2);
		
		System.out.println("Sum is "+sum);
	}

	private static int addToNum(int int1, int int2) {
		int sum = int1+int2;
		return sum;
	}

}


// crtl+shift+b -> to create a break point 
// double tab on the the left side shaded line of the line where the break point has to be created