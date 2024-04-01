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
			<li><a href="<%=request.getContextPath()%>/employee/customer">Customer</a></li>
			<li><a href="<%=request.getContextPath()%>/employee/account"
				class="active">Accounts</a></li>
			<li><a href="<%=request.getContextPath()%>/employee/transaction">Transactions</a></li>
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
				<form class="input-form accountform" method="post"
					action="<%=request.getContextPath()%>/createAccount">
					<c:if test="${not empty accountExists}">
						<div class="usercreation-message failed">
							<i class="fa-solid fa-triangle-exclamation"></i>
							<p>${accountExists}</p>
						</div>
					</c:if>
					<c:if test="${not empty success}">
						<div class="usercreation-message success">
							<i class="fa-solid fa-thumbs-up"></i>
							<p>${success}</p>
						</div>
					</c:if>
					<c:if test="${not empty failure}">
						<div class="usercreation-message failed">
							<i class="fa-solid fa-thumbs-down"></i>
							<p>${failure}</p>
						</div>
					</c:if>
					<label for="UserId">User Id</label>
					<c:choose>
						<c:when test="${not empty success}">
							<input type="number" name="userId"
								placeholder="Enter the User Id" value="" required>
						</c:when>
						<c:otherwise>
							<input type="number" name="userId"
								placeholder="Enter the User Id" value="${param.userId}" required>
						</c:otherwise>
					</c:choose>
					<c:if test="${not empty error}">
						<p class="message-error">${error}</p>
					</c:if>
					<label for="branch-id">Branch</label> <select name="branchId"
						class="account-type" id="branch-id" class="account-form">
						<option value="3007" ${param.branchId == '3007' ? 'selected' : ''}>Coimbatore</option>
						<option value="3008" ${param.branchId == '3008' ? 'selected' : ''}>Chennai</option>
						<option value="3009" ${param.branchId == '3009' ? 'selected' : ''}>Madurai</option>
						<option value="3010" ${param.branchId == '3010' ? 'selected' : ''}>Trichy</option>
						<option value="3011" ${param.branchId == '3011' ? 'selected' : ''}>Salem</option>
					</select> <input type="hidden" id="branch-id-hidden" name="branchId">
					<label for="amount">Account Type</label> <select name="accountType"
						class="account-type" class="account-form">
						<option value="1" ${param.accountType == '1' ? 'selected' : ''}>Current</option>
						<option value="2" ${param.accountType == '2' ? 'selected' : ''}>Savings</option>
						<option value="3" ${param.accountType == '3' ? 'selected' : ''}>Salary</option>
						<option value="4" ${param.accountType == '4' ? 'selected' : ''}>Business</option>
					</select> <label for="balance">Balance</label>
					<c:choose>
						<c:when test="${not empty success}">
							<input type="number" name="balance"
								placeholder="Enter the Balance" value="" required>
						</c:when>
						<c:otherwise>
							<input type="number" name="balance"
								placeholder="Enter the Balance" value="${param.balance}"
								required>
						</c:otherwise>
					</c:choose>
					<c:if test="${not empty invalidBalance}">
						<p class="message-error">${invalidBalance}</p>
					</c:if>
					<input type="submit" value="Create">
				</form>
			</div>
		</div>
	</div>
	<script>
		window.onload = function() {
			var employeeBranchId = "${employeeBranchId}";
			if (employeeBranchId) {
				var branchSelect = document.getElementById("branch-id");
				var branchHiddenInput = document
						.getElementById("branch-id-hidden");
				branchSelect.value = employeeBranchId;
				branchHiddenInput.value = employeeBranchId;
				branchSelect.disabled = true;
				branchSelect.classList.add("removeDropdown");
			}

			var successMessage = "${success}";
			var failureMessage = "${failure}";
			if (successMessage || failureMessage) {
				document.getElementById('form').reset();
			}
		};
	</script>
</body>
</html>