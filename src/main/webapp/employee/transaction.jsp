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
			<li><a href="<%=request.getContextPath()%>/employee/account.jsp">Accounts</a></li>
			<li><a
				href="<%=request.getContextPath()%>/employee/transaction.jsp"
				class="active">Transactions</a></li>
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

	<div class="transaction-page">
		<form id="searchFormTransaction">
			<div class="search-bar">
				<input type="number" id="searchTransaction" name="accountNumber"
					placeholder="Enter Account Number" required>
				<button class="searchButton">
					<i class="fas fa-search"></i>
				</button>
			</div>
			<div>
				<span>From</span> <input type="date" name="fromDate"> <span>To</span>
				<input type="date" name="toDate">
				<button type="submit">Get</button>
			</div>
		</form>
	</div>

	<div class="customerlist-container">
		<table id="table">
			<thead>
				<tr>
					<th>S.No</th>
					<th>Trans Id</th>
					<th>User Id</th>
					<th>From Account</th>
					<th>To Account</th>
					<th>Type</th>
					<th>Amount</th>
					<th>Balance</th>
					<th>Date</th>
					<th>Remark</th>
					<th>Status</th>
					<th>Reference Id</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>1</td>
					<td>5001</td>
					<td>1001</td>
					<td>300700000002</td>
					<td>300700000001</td>
					<td>Withdraw</td>
					<td>2000.00</td>
					<td>55000.00</td>
					<td>2024-02-21</td>
					<td>Fund Transfer</td>
					<td>Success</td>
					<td>1710330334344</td>
				</tr>
			</tbody>
		</table>
	</div>
	<div class="pagination" id="pagination"></div>
	<script src="<%=request.getContextPath()%>/script/script.js"></script>
</body>
</html>