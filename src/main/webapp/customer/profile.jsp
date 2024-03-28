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
			<li><a href="<%=request.getContextPath()%>/customer/account.jsp">Accounts</a></li>
			<li><a
				href="<%=request.getContextPath()%>/customer/transaction.jsp">Transactions</a></li>
			<li><a
				href="<%=request.getContextPath()%>/customer/Statement.jsp">Statements</a></li>
			<li><a href="<%=request.getContextPath()%>/customer/profile.jsp"
				class="active">Profile</a></li>
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
</body>
</html>