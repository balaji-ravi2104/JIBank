<html> 
	<head> 
		<title>Welcome! to FreeMarker HelloWorld Example</title> 
		<p>${company}</p>
	</head> 
	<body> 
		<h1>Welcome ${user}<#if user == "Balaji">,our beloved leader</#if></h1> 
		<h1>Welcome ${user!"visitor"}</h1> <#-- Here if the user data-model is not present the default value visitor will be printed -->
		<h1>${user?upper_case}</h1>
		<p>User Id : ${accountDetails.userId?c}</p>
		<p>Account Id : ${accountDetails.accountId?c}</p>
		<p>Account Number : ${accountDetails.accountNumber}</p>
		<p>Account Status : ${accountDetails.accountStatus}</p>
		<p>Account Type : ${accountDetails.accountType}</p>
		<p>Balance : ${accountDetails.balance}</p>
		<p>Branch Id : ${accountDetails.branchId?c}</p>
		<p>Primary Account : ${accountDetails.primaryAccount?string("Yes", "No")}</p>
		
		<#list accountList as account>
			<p>User Id : ${account.userId?c}</p>
			<p>Account Id : ${account.accountId?c}</p>
			<p>Account Number : ${account.accountNumber}</p>
			<p>Account Status : ${account.accountStatus}</p>
			<p>Account Type : ${account.accountType}</p>
			<p>Balance : ${account.balance}</p>
			<p>Branch Id : ${account.branchId?c}</p>
			<p>Primary Account : ${account.primaryAccount?string("Yes", "No")}</p>
		</#list>
		<p>${(5 + 8)/2}</p>
		
		<#assign s = "Hello ${user}!">
		${s} <#-- Just to see what the value of s is -->
		
		<#list ["Joe", "Fred"] + ["Julia", "Kate"] as user>
			${user}
		</#list>
		
		<#assign ages = {"Joe":23, "Fred":25} + {"Joe":30, "Julia":18}>
 		Joe is ${ages.Joe} <#-- If both hashes contain the same key, the hash on the right-hand side of the + takes precedence. -->
 		Fred is ${ages.Fred}
 		Julia is ${ages.Julia}
 		
 		<#-- Call the function -->
 		${Repeat("Balaji", 3)}
 		${Repeat(Repeat("Ravi", 2), 3)}
 		${Repeat("baby", 4)?upper_case}
 		
 		<#assign temp = true>
		<#if temp>
   			Balaji Ravi
		</#if>
		
		<#-- User defines directives -->
		<@greet />
		
		<@do_thrice>
  			Anything.
		</@do_thrice>
		
		<@repeat count=4 start=1 ; c, halfc, last>
  			${c}. ${halfc}<#if last> Last!</#if>
		</@repeat>
 		
		<#include "/footer.html">
		
		<#-- Define the function -->
		<#function Repeat str times>
   			 <#local result = "">
    		 <#list 1..times as i>
        			<#local result = result + str>
    		 </#list>
    		<#return result>		
		</#function>
		
	</body> 
</html>

<#-- User defines directives -->
		<#macro greet>
   			 <font size="+2">Hello ${user}!</font>
		</#macro>
		
		<#macro do_thrice>
  			<#nested>
  			<#nested>
  			<#nested>
		</#macro>
		
		<#macro repeat count,start>
  			<#list start..count as x>
    			<#nested x, x/2, x==count>
 			 </#list>
		</#macro>

