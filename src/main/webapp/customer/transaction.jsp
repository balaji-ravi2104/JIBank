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
			<li><a href="<%=request.getContextPath()%>/customer/account">Accounts</a></li>
			<li><a
				href="<%=request.getContextPath()%>/customer/transaction"
				class="active">Transactions</a></li>
			<li><a
				href="<%=request.getContextPath()%>/customer/Statement">Statements</a></li>
			<li><a href="<%=request.getContextPath()%>/customer/profile">Profile</a></li>
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
	<div class="container-deposit" id="transfer-container">
		<div class="deposit-content" id="transfer-content">
			<div class="deposit-image">
				<img src="<%=request.getContextPath()%>/images/Payment.png"
					alt="Withdraw Image" id="transfer-image">
			</div>
			<div id="form">
				<form class="input-form" id="transfer-form" method="post"
					action="${not outSideBank ? withinBank: otherBank}">
					<c:if test="${not empty inactiveAccount}">
						<div id="invalid-account-error"
							class="invalid-accountnumber-error">
							<i class="fa-solid fa-triangle-exclamation"></i>
							<p>${inactiveAccount}</p>
						</div>
					</c:if>
					<c:if test="${not empty success}">
						<div class="usercreation-message success"
							id="usercreation-message">
							<i class="fa-solid fa-thumbs-up"></i>
							<p>${success}</p>
						</div>
					</c:if>
					<c:if test="${not empty failed}">
						<div class="usercreation-message failed" id="usercreation-message">
							<i class="fa-solid fa-thumbs-down"></i>
							<p>${failed}</p>
						</div>
					</c:if>
					<label for="account-number">Account Number</label> <input
						type="number" name="accountNumber"
						placeholder="Enter the Account Number"
						value="${param.accountNumber}" required>
					<c:if test="${not empty invalidAccount}">
						<div id="invalid-account-error"
							class="invalid-accountnumber-error transfer">
							<i class="fa-solid fa-triangle-exclamation"></i>
							<p>${invalidAccount}</p>
						</div>
					</c:if>
					<c:if test="${not outSideBank}">
						<label for="branch-id">Branch</label>
						<select name="branchId" class="account-type" id="branch-id"
							class="account-form">
							<option value="3007"
								${param.branchId == '3007' ? 'selected' : ''}>Coimbatore</option>
							<option value="3008"
								${param.branchId == '3008' ? 'selected' : ''}>Chennai</option>
							<option value="3009"
								${param.branchId == '3009' ? 'selected' : ''}>Madurai</option>
							<option value="3010"
								${param.branchId == '3010' ? 'selected' : ''}>Trichy</option>
							<option value="3011"
								${param.branchId == '3011' ? 'selected' : ''}>Salem</option>
						</select>
					</c:if>
					<label for="amount">Transfer Amount</label> <input type="number"
						name="amount" step="0.01" placeholder="Enter Amount to Transfer"
						value="${param.amount}" required>
					<c:if test="${not empty invalidBalance}">
						<div id="invalid-account-error"
							class="invalid-accountnumber-error transfer">
							<i class="fa-solid fa-triangle-exclamation"></i>
							<p>${invalidBalance}</p>
						</div>
					</c:if>
					<label for="description">Small Description</label>
					<textarea id="message" name="message" rows="2" cols="50"
						placeholder="Enter Your  Description" required>${param.message}</textarea>
					<input type="submit" value="Transfer">
				</form>
			</div>
		</div>
	</div>
	<div class="transfer-change-button">
		<c:if test="${not outSideBank}">
			<form action="<%=request.getContextPath()%>/transferOutSideBank"
				method="post">
				<button>Transfer Out Side Bank</button>
			</form>
		</c:if>
		<c:if test="${outSideBank}">
			<form action="<%=request.getContextPath()%>/transferInBank"
				method="post">
				<button>Transfer With in Bank</button>
			</form>
		</c:if>
	</div>
</body>
</html>