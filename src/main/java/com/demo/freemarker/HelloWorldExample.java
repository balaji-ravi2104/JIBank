package com.demo.freemarker;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.banking.model.Account;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
// ClientPortalTemplateLoader
public class HelloWorldExample {
	
	public static void main(String[] args) throws IOException, URISyntaxException, TemplateException {

		// create configuration, only once in application
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
		FileTemplateLoader fileTemplateLoader = new FileTemplateLoader(new File(HelloWorldExample.class.getResource("/templates").toURI()));
		//cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		cfg.setTemplateExceptionHandler(new MyTemplateExceptionHandler());
		cfg.setLogTemplateExceptions(false);
		cfg.setDefaultEncoding("UTF-8");
		cfg.setSharedVariable("company", "Zoho Corp");
		

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("user", "Balaji&Ravi");
		model.put("accountDetails", getAccountDetails());
		model.put("accountList", getListOfAccounts());
		//model.put("temp", true);
		
		StringTemplateLoader templateLoader = new StringTemplateLoader();
		templateLoader.putTemplate("freemarker", "<html> \n"
				+ "	<head> \n"
				+ "		<title>Welcome! to FreeMarker HelloWorld Example</title> \n"
				+ "		<p>${company}</p>\n"
				+ "	</head> \n"
				+ "	<body> \n"
				+ "		<h1>Welcome ${user}<#if user == \"Balaji\">,our beloved leader</#if></h1> \n"
				+ "		<h1>Welcome ${user!\"visitor\"}</h1> <#-- Here if the user data-model is not present the default value visitor will be printed -->\n"
				+ "		<h1>${user?upper_case}</h1>\n"
				+ "		<p>User Id : ${accountDetails.userId?c}</p>\n"
				+ "		<p>Account Id : ${accountDetails.accountId?c}</p>\n"
				+ "		<p>Account Number : ${accountDetails.accountNumber}</p>\n"
				+ "		<p>Account Status : ${accountDetails.accountStatus}</p>\n"
				+ "		<p>Account Type : ${accountDetails.accountType}</p>\n"
				+ "		<p>Balance : ${accountDetails.balance}</p>\n"
				+ "		<p>Branch Id : ${accountDetails.branchId?c}</p>\n"
				+ "		<p>Primary Account : ${accountDetails.primaryAccount?string(\"Yes\", \"No\")}</p>\n"
				+ "		\n"
				+ "		<#list accountList as account>\n"
				+ "			<p>User Id : ${account.userId?c}</p>\n"
				+ "			<p>Account Id : ${account.accountId?c}</p>\n"
				+ "			<p>Account Number : ${account.accountNumber}</p>\n"
				+ "			<p>Account Status : ${account.accountStatus}</p>\n"
				+ "			<p>Account Type : ${account.accountType}</p>\n"
				+ "			<p>Balance : ${account.balance}</p>\n"
				+ "			<p>Branch Id : ${account.branchId?c}</p>\n"
				+ "			<p>Primary Account : ${account.primaryAccount?string(\"Yes\", \"No\")}</p>\n"
				+ "		</#list>\n"
				+ "		<p>${(5 + 8)/2}</p>\n"
				+ "		\n"
				+ "		<#assign s = \"Hello ${user}!\">\n"
				+ "		${s} <#-- Just to see what the value of s is -->\n"
				+ "		\n"
				+ "		<#list [\"Joe\", \"Fred\"] + [\"Julia\", \"Kate\"] as user>\n"
				+ "			${user}\n"
				+ "		</#list>\n"
				+ "		\n"
				+ "		<#assign ages = {\"Joe\":23, \"Fred\":25} + {\"Joe\":30, \"Julia\":18}>\n"
				+ " 		Joe is ${ages.Joe} <#-- If both hashes contain the same key, the hash on the right-hand side of the + takes precedence. -->\n"
				+ " 		Fred is ${ages.Fred}\n"
				+ " 		Julia is ${ages.Julia}\n"
				+ " 		\n"
				+ " 		<#-- Call the function -->\n"
				+ " 		${Repeat(\"Balaji\", 3)}\n"
				+ " 		${Repeat(Repeat(\"Ravi\", 2), 3)}\n"
				+ " 		${Repeat(\"baby\", 4)?upper_case}\n"
				+ " 		\n"
				+ " 		<#assign temp = true>\n"
				+ "		<#if temp>\n"
				+ "   			Balaji Ravi\n"
				+ "		</#if>\n"
				+ "		\n"
				+ "		<#-- User defines directives -->\n"
				+ "		<@greet />\n"
				+ "		\n"
				+ "		<@do_thrice>\n"
				+ "  			Anything.\n"
				+ "		</@do_thrice>\n"
				+ "		\n"
				+ "		<@repeat count=4 start=1 ; c, halfc, last>\n"
				+ "  			${c}. ${halfc}<#if last> Last!</#if>\n"
				+ "		</@repeat>\n"
				+ " 		\n"
				+ "		\n"
				+ "		<#-- Define the function -->\n"
				+ "		<#function Repeat str times>\n"
				+ "   			 <#local result = \"\">\n"
				+ "    		 <#list 1..times as i>\n"
				+ "        			<#local result = result + str>\n"
				+ "    		 </#list>\n"
				+ "    		<#return result>		\n"
				+ "		</#function>\n"
				+ "		\n"
				+ "	</body> \n"
				+ "</html>\n"
				+ "\n"
				+ "<#-- User defines directives -->\n"
				+ "		<#macro greet>\n"
				+ "   			 <font size=\"+2\">Hello ${user}!</font>\n"
				+ "		</#macro>\n"
				+ "		\n"
				+ "		<#macro do_thrice>\n"
				+ "  			<#nested>\n"
				+ "  			<#nested>\n"
				+ "  			<#nested>\n"
				+ "		</#macro>\n"
				+ "		\n"
				+ "		<#macro repeat count,start>\n"
				+ "  			<#list start..count as x>\n"
				+ "    			<#nested x, x/2, x==count>\n"
				+ " 			 </#list>\n"
				+ "		</#macro>\n"
				+ "\n");
		
		MultiTemplateLoader mtl = new MultiTemplateLoader(new TemplateLoader[] {fileTemplateLoader,templateLoader});
		
		cfg.setTemplateLoader(mtl);
		
		Template temp = cfg.getTemplate("freemarker");
		
		Writer out = new OutputStreamWriter(System.out);
		
		temp.process(model, out);
	}

	private static Account getAccountDetails() {
		Account account = new Account();
		account.setAccountId(4001);
		account.setAccountNumber("3000100000001");
		account.setAccountStatus(1);
		account.setAccountType(1);
		account.setBalance(45800);
		account.setBranchId(3001);
		account.setPrimaryAccount(true);
		account.setUserId(1008);
		return account;
	}
	
	private static List<Account> getListOfAccounts(){
		List<Account> list = new ArrayList<Account>();
		for(int i=0;i<5;i++) {
			Account account = new Account();
			account.setAccountId(4001+i);
			account.setAccountNumber("3000100000001"+i);
			account.setAccountStatus(1);
			account.setAccountType(1);
			account.setBalance(45800);
			account.setBranchId(3001);
			account.setPrimaryAccount(true);
			account.setUserId(1008);
			
			list.add(account);
		}
		return list;
	}
	
	static class MyTemplateExceptionHandler implements TemplateExceptionHandler {
	    public void handleTemplateException(TemplateException te, Environment env, java.io.Writer out)
	            throws TemplateException {
	        try {
	            out.write("[ERROR: " + te.getMessage() + "]");
	        } catch (IOException e) {
	            throw new TemplateException("Failed to print error message. Cause: " + e, env);
	        }
	    }
	}
}
