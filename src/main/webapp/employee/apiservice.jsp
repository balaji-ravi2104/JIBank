<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="com.banking.utils.DateUtils"%>
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
	<div class="navbar-home">
		<div class="logo">
			<img src="<%=request.getContextPath()%>/images/logo.png" alt="logo">
		</div>
		<div>
			<li><a
				href="<%=request.getContextPath()%>/bank/employee/customer">Users</a></li>
			<li><a
				href="<%=request.getContextPath()%>/bank/employee/account">Accounts</a></li>
			<li><a
				href="<%=request.getContextPath()%>/bank/employee/transaction">Transactions</a></li>
			<%-- <c:if test="${user.typeOfUser == 'ADMIN'}"> --%>
			<li><a
				href="<%=request.getContextPath()%>/bank/employee/apiservice"
				class="active">API Service</a></li>
			<%-- </c:if>	 --%>
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
	<div class="transaction-page statement-page">
		<form id="searchFormTransaction" action="" method="post">
			<div class="transaction-search">
				<div>
					<div>
						<input type="text" name="userId" maxlength="4" pattern="\d{1,4}"
							placeholder="Enter User Id" required value="${param.userId}">
						<button class="searchButton" type="submit" id="search-button">
							<i class="fas fa-search"></i>
						</button>
						<button type="submit" id="create-button">Create API Key</button>
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
				</div>
			</div>
		</form>
	</div>
	
	<div class="customerlist-container">
		<table id="table">
			<c:if test="${not empty userApiKeys}">
				<thead>
					<tr>
						<th>Token Id</th>
						<th>Created Time</th>
						<th>Valid Upto</th>
						<th>Status</th>
						<th>Token</th>
						<th>Action</th>
					</tr>
				</thead>
			</c:if>
			<tbody>
				<c:forEach var="apiEntry" items="${userApiKeys}">
					<tr>
						<c:set var="tokenObject" value="${apiEntry.value}" />
						<td>${tokenObject.tokenId}</td>
						<td>${DateUtils.formateLongToDate(tokenObject.createdTime)}</td>
						<td>${DateUtils.formateLongToDate(tokenObject.validUpto)}</td>
						<td>${tokenObject.tokenStatus}</td>
						<td>${tokenObject.token}</td>
						<td>
							<form action="<%=request.getContextPath()%>/bank/updateAccountStatus"
								method="post">
							</form>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>

	<script>
		var searchBtn = document.getElementById("search-button");
		var createBtn = document.getElementById("create-button");
		var form = document.getElementById("searchFormTransaction");
		var userIdInput = document.querySelector('input[name="userId"]');
		
		searchBtn.onclick = function() {
			form.action = "<%=request.getContextPath()%>/bank/api/getapikey";
		}
		
		createBtn.onclick = function() {
			form.action = "<%=request.getContextPath()%>/bank/api/createapikey";
		}
	</script>
</body>
</html>