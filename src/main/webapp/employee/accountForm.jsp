<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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

	<div class="container-accountform">
		<div class="accountform-content">
			<div class="accountform-image">
				<img src="<%=request.getContextPath()%>/images/createCustomer.png"
					alt="Create Customer Image">
			</div>
			<div id="form">
				<form class="input-form accountform">
					<label for="UserId">User Id</label> <input type="number"
						name="UserId" placeholder="Enter the User Id" required> <label
						for="branch-id">Branch Id</label> <input type="number"
						name="branch-id" placeholder="Enter the Branch Id" required>
					<label for="amount">Account Type</label> <select
						class="account-form">
						<option>Current</option>
						<option>Savings</option>
						<option>Salary</option>
						<option>Businsess</option>
					</select> <label for="balance">Balance</label> <input type="number"
						name="balance" placeholder="Enter the Balance" required> <input
						type="submit" value="Create">
				</form>
			</div>
		</div>
	</div>
</body>
</html>