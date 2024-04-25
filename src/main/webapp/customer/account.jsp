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
		response.sendRedirect(request.getContextPath() + "/bank/login");
	}
	%>
	<div class="navbar-home">
		<div class="logo">
			<img src="<%=request.getContextPath()%>/images/logo.png" alt="logo">
		</div>
		<div>
			<li><a href="<%=request.getContextPath()%>/bank/customer/account"
				class="active">Accounts</a></li>
			<li><a href="<%=request.getContextPath()%>/bank/customer/transaction">Transactions</a></li>
			<li><a href="<%=request.getContextPath()%>/bank/customer/Statement">Statements</a></li>
			<li><a href="<%=request.getContextPath()%>/bank/customer/profile">Profile</a></li>
			<li>
				<form id="logoutForm" action="<%=request.getContextPath()%>/bank/logout"
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
	<div class="container-deposit">
		<div class="deposit-content">
			<div class="deposit-image">
				<img src="<%=request.getContextPath()%>/images/Account.png"
					alt="Account Image">
			</div>
			<div id="account-details">
				<c:choose>
					<c:when test="${not empty currentAccount}">
						<div class="account-logo">
							<img src="<%=request.getContextPath()%>/images/AccountLogo.png"
								alt="Account Logo">
						</div>
						<table>
							<tr>
								<th>Account Id</th>
								<td>${currentAccount.accountId}</td>
							</tr>
							<tr>
								<th>Account Number</th>
								<td>${currentAccount.accountNumber}</td>
							</tr>
							<tr>
								<th>Branch Id</th>
								<td>${currentAccount.branchId}</td>
							</tr>
							<tr>
								<th>Account Type</th>
								<td>${currentAccount.accountType}</td>
							</tr>
							<tr>
								<th>Balance</th>
								<td>${currentAccount.balance}</td>
							</tr>
							<tr>
								<th>Primary Account</th>
								<td>${currentAccount.primaryAccount ?'Yes' :'No'}</td>
							</tr>
							<tr>
								<th>Account Status</th>
								<td>${currentAccount.accountStatus}</td>
							</tr>
						</table>
						<c:if test="${accountsCount>1}">
							<div class="Switch-account-button">
								<form action="<%=request.getContextPath()%>/bank/switchAccount"
									method="post">
									<button>Switch Account</button>
								</form>
							</div>
						</c:if>
					</c:when>
					<c:otherwise>
						<h1>No Account Found</h1>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
</body>
</html>