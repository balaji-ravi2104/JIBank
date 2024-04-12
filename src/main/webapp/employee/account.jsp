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
			<li><a href="<%=request.getContextPath()%>/bank/employee/customer">Users</a></li>
			<li><a href="<%=request.getContextPath()%>/bank/employee/account"
				class="active">Accounts</a></li>
			<li><a href="<%=request.getContextPath()%>/bank/employee/transaction">Transactions</a></li>
			<c:if test="${user.typeOfUser == 'ADMIN'}">
				<li><a
					href="<%=request.getContextPath()%>/bank/employee/apiservice">API Service</a></li>
			</c:if>
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

	<div class="customer-page">
		<form action="<%=request.getContextPath()%>/bank/createaccount"
			method="post">
			<button>Create Account</button>
		</form>
		<form id="searchFormAccount"
			action="<%=request.getContextPath()%>/bank/account/getAccounts"
			method="post">
			<div class="accountsearch-bar">
				<div>
					<input type="text" id="searchCustomerAccounts" name="userId"
						maxlength="4" pattern="\d{1,4}" placeholder="Enter User Id"
						required value="${param.userId}">
				</div>
				<c:if test="${user.typeOfUser eq 'EMPLOYEE'}">
					<input type="hidden" value="${employeeBranchId}" name="branchId">
				</c:if>
				<c:if test="${user.typeOfUser eq 'ADMIN'}">
					<div class="branchId-div" id="branchIdDiv">
						<select id="branchId" name="branchId" required>
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
					</div>
				</c:if>

				<div>
					<button class="searchButton" type="submit">
						<i class="fas fa-search"></i>
					</button>
				</div>
			</div>
			<c:if test="${not empty error}">
				<div class="invalid-userid-error">
					<i class="fa-solid fa-triangle-exclamation"></i>
					<p>${error}</p>
				</div>
			</c:if>
			<c:if test="${not empty updatedSuccess}">
				<div class="usercreation-message success">
					<i class="fa-solid fa-thumbs-up"></i>
					<p>${updatedSuccess}</p>
				</div>
			</c:if>
			<c:if test="${not empty updationFailed}">
				<div class="usercreation-message failed">
					<i class="fa-solid fa-thumbs-down"></i>
					<p>${updationFailed}</p>
				</div>
			</c:if>
		</form>
	</div>

	<div class="customerlist-container">
		<table id="table">
			<c:if test="${not empty customerAccounts}">
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
						<th>Action</th>
					</tr>
				</thead>
			</c:if>
			<tbody>
				<c:forEach var="accountEntry" items="${customerAccounts}">
					<tr>
						<c:set var="account" value="${accountEntry.value}" />
						<td>${account.userId}</td>
						<td>${account.accountId}</td>
						<td>${account.accountNumber}</td>
						<td>${account.branchId}</td>
						<td>₹${account.balance}</td>
						<td>${account.primaryAccount ? 'Yes' : 'No'}</td>
						<td>${account.accountType}</td>
						<td>${account.accountStatus}</td>
						<td>
							<form action="<%=request.getContextPath()%>/bank/updateAccountStatus"
								method="post">
								<input type="hidden" name="userId" value="${account.userId}" />
								<input type="hidden" name="branchId" value="${account.branchId}" />
								<input type="hidden" name="accountNumber"
									value="${account.accountNumber}" /> <input type="hidden"
									name="status" value="${account.accountStatus}" />
								<button type="submit" class="updateButton">
									<c:choose>
										<c:when test="${account.accountStatus == 'ACTIVE'}">INACTIVE</c:when>
										<c:otherwise>ACTIVE</c:otherwise>
									</c:choose>
								</button>
							</form>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</body>
</html>