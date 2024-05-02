<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="com.banking.model.User"%>
<%@ page import="com.banking.model.UserType"%>
<%@ page import="com.banking.utils.CustomException"%>
<%@ page import="com.banking.controller.UserController"%>
<%@ page import="com.banking.utils.CookieEncryption"%>
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
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/style.css">
</head>
<body>
	<%-- <%
	response.setHeader("Cache-Control", "no-cache,no-store,must-revalidate");
	response.setHeader("Pragma", "no-cache");

	if (session.getAttribute("user") == null) {
		response.sendRedirect(request.getContextPath() + "/bank/login");
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
	if (UserId == null) {
		response.sendRedirect(request.getContextPath() + "/bank/login");
	} else {
		/* int userId = (int) session.getAttribute("currentUserId"); */
		try {
			String decryptUserId = CookieEncryption.decrypt(UserId);
			if (decryptUserId == null) {
				response.sendRedirect(request.getContextPath() + "/bank/logout");
			}
			int userId = Integer.parseInt(decryptUserId);
			UserController userController = new UserController();
			User user = userController.getCustomerDetailsById(userId);
			request.setAttribute("user", user);
			if(user.getTypeOfUser() != UserType.EMPLOYEE && user.getTypeOfUser() != UserType.ADMIN){
				response.sendRedirect(request.getContextPath() + "/bank/404");
			}
		} catch (CustomException e) {
			e.printStackTrace();
			response.sendRedirect(request.getContextPath() + "/bank/logout");
		}
	}
	%>
	<div class="navbar-home">
		<div class="logo">
			<img src="<%=request.getContextPath()%>/images/logo.png" alt="logo">
		</div>
		<div>
			<li><a
				href="<%=request.getContextPath()%>/bank/employee/customer"
				class="active">Users</a></li>
			<li><a
				href="<%=request.getContextPath()%>/bank/employee/account">Accounts</a></li>
			<li><a
				href="<%=request.getContextPath()%>/bank/employee/transaction">Transactions</a></li>
			<c:if test="${user.getTypeOfUser() == UserType.ADMIN}">
				<li><a
					href="<%=request.getContextPath()%>/bank/employee/apiservice">API
						Service</a></li>
			</c:if>
			<li>
				<form id="logoutForm"
					action="<%=request.getContextPath()%>/bank/logout" method="post">
					<button type="submit"
						style="border: none; background: none; cursor: pointer;">
						<i class="fa fa-sign-out" aria-hidden="true"
							style="font-size: 25px"></i>
					</button>
				</form>
			</li>
		</div>
	</div>
	<div class="customer-page">
		<div class="create-button">
			<form action="<%=request.getContextPath()%>/bank/addCustomer"
				method="post">
				<button>Add Customer</button>
			</form>
			<c:if test="${user.getTypeOfUser() == UserType.ADMIN}">
				<form action="<%=request.getContextPath()%>/bank/addEmployee"
					method="post">
					<button>Add Employee</button>
				</form>
			</c:if>
		</div>
		<form id="searchFormCustomer" method="post"
			action="<%=request.getContextPath()%>/bank/getCustomer">
			<div>
				<div class="search-bar">
					<input type="text" id="searchCustomer" name="userId" maxlength="4"
						value="${param.userId}" pattern="\d{1,4}"
						placeholder="Enter User Id" required>
					<button class="searchButton" type="submit">
						<i class="fas fa-search"></i>
					</button>
				</div>
				<c:if test="${not empty error}">
					<div class="invalid-userid-error">
						<i class="fa-solid fa-triangle-exclamation"></i>
						<p>${error}</p>
					</div>
				</c:if>
			</div>
			<div>
				<button id="updateCustomerButton" type="submit"
					formaction="<%=request.getContextPath()%>/bank/updateCustomer">Update
					User</button>
			</div>
		</form>
	</div>

	<div class="customerlist-container">
		<table id="table">
			<c:if test="${not empty customerDetails}">
				<thead>
					<tr>
						<th>User Id</th>
						<th>Name</th>
						<th>Gender</th>
						<th>Email</th>
						<th>Mobile</th>
						<th>Address</th>
						<th>DOB</th>
						<th>Status</th>
					</tr>
				</thead>
			</c:if>
			<tbody>
				<c:if test="${not empty customerDetails}">
					<tr>
						<td>${customerDetails.userId}</td>
						<td>${customerDetails.firstName}&nbsp;${customerDetails.lastName}</td>
						<td>${customerDetails.gender}</td>
						<td>${customerDetails.email}</td>
						<td>${customerDetails.contactNumber}</td>
						<td>${customerDetails.address}</td>
						<td>${DOB}</td>
						<td>${customerDetails.status}</td>
					</tr>
				</c:if>
			</tbody>
		</table>
	</div>
	<script>
		window.onload = function() {
			var userIdInput = document.getElementById('searchCustomer');
			var urlParams = new URLSearchParams(window.location.search);
			if (urlParams.has('userId')) {
				userIdInput.value = urlParams.get('userId');
			}

			document.getElementById("updateCustomerButton").addEventListener(
					"click",
					function(event) {
						if (document.querySelector(".invalid-userid-error")) {
							document.querySelector(".invalid-userid-error")
									.remove();
						}
					});
		};
	</script>

</body>
</html>