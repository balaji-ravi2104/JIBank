<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
	<%
	response.setHeader("Cache-Control", "no-cache,no-store,must-revalidate");
	response.setHeader("Pragma", "no-cache");

	if (session.getAttribute("user") == null) {
		response.sendRedirect("../login.jsp");
	}
	%>
	<div class="navbar-home">
		<div class="logo">
			<img src="<%=request.getContextPath()%>/images/logo.png" alt="logo">
		</div>
		<div>
			<li><a
				href="<%=request.getContextPath()%>/employee/customer.jsp"
				class="active">Customer</a></li>
			<li><a href="<%=request.getContextPath()%>/employee/account.jsp">Accounts</a></li>
			<li><a
				href="<%=request.getContextPath()%>/employee/transaction.jsp">Transactions</a></li>
			<li>
				<form id="logoutForm" action="<%=request.getContextPath()%>/logout"
					method="post">
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
			<form action="<%=request.getContextPath()%>/addCustomer"
				method="post">
				<button>Add Customer</button>
			</form>
			<c:if test="${user.typeOfUser == 'ADMIN'}">
				<form action="<%=request.getContextPath()%>/addEmployee"
					method="post">
					<button>Add Employee</button>
				</form>
			</c:if>
		</div>
		<form id="searchFormCustomer" method="get"
			action="<%=request.getContextPath()%>/getcustomer">
			<div>
				<div class="search-bar">
					<input type="text" id="searchCustomer" name="userId"
						maxlength="4" pattern="\d{1,4}" placeholder="Enter User Id"
						required>
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
					formaction="<%=request.getContextPath()%>/updateCustomer">Update
					Customer</button>
			</div>
		</form>
	</div>

	<div class="customerlist-container">
		<table id="table">
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