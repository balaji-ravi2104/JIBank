<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="com.banking.model.User"%>
<%@ page import="com.banking.model.UserType"%>
<%@ page import="com.banking.controller.UserController"%>
<%@ page import="com.banking.utils.CookieEncryption"%>
<%@ page import="com.banking.utils.CustomException"%>
<%@ page import="com.banking.servlet.AccountServletHelper" %>
<%@ page import="com.banking.servlet.UserServletHelper" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>JI Bank</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link
	href="https://fonts.googleapis.com/css2?family=Roboto:ital,wght@0,100;0,300;0,400;0,500;0,700;0,900;1,100;1,300;1,400;1,500;1,700;1,900&display=swap"
	rel="stylesheet">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/style.css">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
	<%-- <%
	response.setHeader("Cache-Control", "no-cache,no-store,must-revalidate");
	response.setHeader("Pragma", "no-cache");

	User user = (User) session.getAttribute("user");
	if (user != null) {
		if(user.getTypeOfUser() == UserType.CUSTOMER){
			response.sendRedirect(request.getContextPath() + "/bank/customer/account");
		}else{
			response.sendRedirect(request.getContextPath() + "/bank/employee/customer");	
		}
	}
	%> --%>
	<%
	response.setHeader("Cache-Control", "no-cache,no-store,must-revalidate");
	response.setHeader("Pragma", "no-cache");
	String UserId = null;
	Cookie[] cookies = request.getCookies();
	if (cookies != null) {
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("userId")) {
				UserId = cookie.getValue();
			}
		}
	}
	if (UserId != null) {
		try {
			String decryptUserId = CookieEncryption.decrypt(UserId);
			if (decryptUserId == null) {
				response.sendRedirect(request.getContextPath() + "/bank/login");
			} else {
				int userId = Integer.parseInt(decryptUserId);
				UserController userController = new UserController();
				User user = userController.getCustomerDetailsById(userId);
				if (user.getTypeOfUser() == UserType.CUSTOMER) {
					AccountServletHelper.getCustomerAccounts(user.getUserId(), request, response);
					response.sendRedirect(request.getContextPath() + "/bank/customer/account");
				} else {
					if(user.getTypeOfUser() == UserType.EMPLOYEE){
						UserServletHelper.getEmployeeBranch(request, response, user.getUserId());
					}
					response.sendRedirect(request.getContextPath() + "/bank/employee/customer");
				}
			}
		} catch (CustomException e) {
			e.printStackTrace();
			response.sendRedirect(request.getContextPath() + "/bank/logout");
		}
	}
	%>
	<div class="navbar">
		<div class="logo">
			<img src="<%=request.getContextPath()%>/images/logo.png" alt="logo">
		</div>
	</div>

	<div class="container">
		<div id="login-image">
			<img src="<%=request.getContextPath()%>/images/Login.png"
				alt="Login Images" />
		</div>
		<div id="form">
			<c:if test="${not empty error}">
				<div class="usercreation-message failed login-error">
					<i class="fa-solid fa-triangle-exclamation"></i>
					<p>${error}</p>
				</div>
			</c:if>
			<form class="input-form" method="post"
				action="<%=request.getContextPath()%>/bank/login">
				<label for="userId">User Id</label> <input type="text" name="userId"
					pattern="\d{1,4}" placeholder="Enter Your User Id" maxlength="4"
					required> <label for="password">Password</label> <input
					type="password" name="password" placeholder="Enter Your Password"
					required> <input type="submit" value="Login">
			</form>
		</div>
	</div>
</body>
</html>