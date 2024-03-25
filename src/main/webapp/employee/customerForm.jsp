<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
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

	<div class="container-create-customer">
		<div class="create-customer-content">
			<div class="create-customer-image">
				<img src="<%=request.getContextPath()%>/images/createCustomer.png"
					alt="Customer Creation Image">
			</div>
			<div id="form">
				<c:if test="${not empty userCreationSuccess}">
					<div class="usercreation-message success">
						<i class="fa-solid fa-thumbs-up"></i>
						<p>${userCreationSuccess}</p>
					</div>
				</c:if>
				<c:if test="${not empty userCreationFailed}">
					<div class="usercreation-message failed">
						<i class="fa-solid fa-thumbs-down"></i>
						<p>${userCreationFailed}</p>
					</div>
				</c:if>
				<form class="customer-form" method="post"
					action="<%=request.getContextPath()%>/addUser">
					<div class="form-row">
						<div class="form-group wider">
							<label for="firstname">First Name</label> <input type="text"
								name="firstname" placeholder="Enter the First Name" required>
						</div>
						<div class="form-group">
							<label for="lastname">Last Name</label> <input type="text"
								name="lastname" placeholder="Enter the Last Name" required>
						</div>
					</div>
					<div class="form-row">
						<div class="form-group wider">
							<label for="email">Email</label> <input type="email" name="email"
								placeholder="Enter the Email" required>
						</div>
						<div class="form-group">
							<label for="gender">Gender</label> <select id="gender"
								name="gender" required>
								<option value="male">Male</option>
								<option value="female">Female</option>
								<option value="other">Other</option>
							</select>
						</div>
					</div>
					<c:if test="${not empty invalidEmail}">
						<div class="customer-form-error">
							<p>${invalidEmail}</p>
						</div>
					</c:if>
					<div class="form-row">
						<div class="form-group wider">
							<label for="contactnumber">Contact Number</label> <input
								type="number" name="contactnumber"
								placeholder="Enter the Contact Number" required>
						</div>
						<div class="form-group">
							<label for="date of birth">Date of Birth</label> <input
								type="date" name="dateofbirth" required>
						</div>
					</div>
					<c:if test="${not empty invalidMobile}">
						<div class="customer-form-error">
							<p>${invalidMobile}</p>
						</div>
					</c:if>
					<label for="address">Address</label>
					<textarea name="address" placeholder="Enter the Address" required></textarea>
					<c:if test="${customer}">
						<label for="pannumber">PAN Number</label>
						<input type="text" name="pannumber"
							placeholder="Enter the PAN Number" required>
						<c:if test="${not empty invalidPAN}">
							<div class="customer-form-error">
								<p>${invalidPAN}</p>
							</div>
						</c:if>
						<label for="aadharnumber">Aadhar Number</label>
						<input type="number" name="aadharnumber"
							placeholder="Enter the Aadhar Number" required>
						<c:if test="${not empty invalidAadhar}">
							<div class="customer-form-error">
								<p>${invalidAadhar}</p>
							</div>
						</c:if>
					</c:if>
					<c:if test="${employee}">
						<div class="form-group branchSelect">
							<label for="branchId">Branch Id</label> <select id="branchId"
								name="branchId" required>
								<option value="3007">3007</option>
								<option value="3008">3008</option>
								<option value="3009">3009</option>
								<option value="3010">3010</option>
								<option value="3011">3011</option>
							</select>
						</div>
					</c:if>
					<input type="submit" value="Create">
				</form>
			</div>
		</div>
	</div>
</body>
</html>