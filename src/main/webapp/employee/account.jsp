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
				href="<%=request.getContextPath()%>/employee/customer.jsp">Customer</a></li>
			<li><a href="<%=request.getContextPath()%>/employee/account.jsp"
				class="active">Accounts</a></li>
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
		<div>
			<button onclick="window.location.href='accountForm.jsp'">Create
				Account</button>
		</div>
		<form id="searchFormAccount">
			<div class="search-bar">
				<input type="text" id="searchAccount" name="accountNumber"
					maxlength="12" pattern="\d{1,12}"
					placeholder="Enter Account Number" required>
				<button class="searchButton">
					<i class="fas fa-search"></i>
				</button>
			</div>
			<div>
				<button>Update Account</button>
				<button>Remove Account</button>
			</div>
		</form>
	</div>

	<div class="customerlist-container">
		<table id="table">
			<thead>
				<tr>
					<th>User Id</th>
					<th>Account Id</th>
					<th>Account Number</th>
					<th>Branch Id</th>
					<th>Balance</th>
					<th>Primary Account</th>
					<th>Account Type</th>
					<th>Status</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>1009</td>
					<td>4001</td>
					<td>300700000001</td>
					<td>3007</td>
					<td>57500.00</td>
					<td>Yes</td>
					<td>Savings</td>
					<td>Active</td>
				</tr>
			</tbody>
		</table>
	</div>
</body>
</html>