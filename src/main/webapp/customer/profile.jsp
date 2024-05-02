<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="com.banking.utils.DateUtils"%>
<%@ page import="com.banking.model.User"%>
<%@ page import="com.banking.model.UserType"%>
<%@ page import="com.banking.utils.CustomException"%>
<%@ page import="com.banking.controller.UserController"%>
<%@ page import="com.banking.utils.CookieEncryption"%>
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
	%>  --%>
	<%
	response.setHeader("Cache-Control", "no-cache,no-store,must-revalidate");
	response.setHeader("Pragma", "no-cache");
	String UserId = null;
	User user = null;
	Cookie[] cookies = request.getCookies();
	if (cookies != null) {
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("userId")) {
		UserId = cookie.getValue();
			}
		}
	}
	if (UserId == null) {
		response.sendRedirect(request.getContextPath() + "/bank/login");
	} else {
		/* int userId = (int) session.getAttribute("currentUserId"); */
		try {
			String decryptUserId = CookieEncryption.decrypt(UserId);
			if (decryptUserId == null) {
				response.sendRedirect(request.getContextPath() + "/bank/logout");
			}
			int userId = Integer.parseInt(decryptUserId);
			UserController userController = new UserController();
			user = userController.getCustomerDetailsById(userId);
			request.setAttribute("user", user);
			if(user.getTypeOfUser() != UserType.CUSTOMER){
				response.sendRedirect(request.getContextPath() + "/bank/404");
			}
		} catch (CustomException e) {
			e.printStackTrace();
			response.sendRedirect(request.getContextPath() + "/bank/logout");
		}
	}
	%>
	<div class="navbar-home">
		<div class="logo">
			<img src="<%=request.getContextPath()%>/images/logo.png" alt="logo">
		</div>
		<div>
			<li><a
				href="<%=request.getContextPath()%>/bank/customer/account">Accounts</a></li>
			<li><a
				href="<%=request.getContextPath()%>/bank/customer/transaction">Transactions</a></li>
			<li><a
				href="<%=request.getContextPath()%>/bank/customer/Statement">Statements</a></li>
			<li><a
				href="<%=request.getContextPath()%>/bank/customer/profile"
				class="active">Profile</a></li>
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
	<div class="container-profile">
		<div id="profile-details">
			<div>
				<img src="<%=request.getContextPath()%>/images/profile.png"
					alt="profile">
			</div>
			<table>
				<tr>
					<th>User Id</th>
					<td>${user.getUserId()}</td>
				</tr>
				<tr>
					<th>Name</th>
					<td>${user.getFirstName()}${user.getLastName()}</td>
				</tr>
				<tr>
					<th>Gender</th>
					<td>${user.getGender()}</td>
				</tr>
				<tr>
					<th>Email</th>
					<td>${user.getEmail()}</td>
				</tr>
				<tr>
					<th>Mobile</th>
					<td>${user.getContactNumber()}</td>
				</tr>
				<tr>
					<th>DOB</th>
					<td>${DateUtils.longToDate(user.getDateOfBirth())}</td>
				</tr>
				<tr>
					<th>Address</th>
					<td class="address">${user.getAddress()}</td>
				</tr>
				<tr>
					<th></th>
					<td><button id="update-password-button">Update
							Password</button></td>
				</tr>
			</table>
		</div>

		<div id="modal" class="modal">
			<div class="modal-content">
				<span class="close">&times;</span>
				<div id="update-password-form">
					<form id="password-form"
						action="<%=request.getContextPath()%>/bank/updatePassword"
						method="post">
						<c:if test="${not empty success}">
							<div class="usercreation-message success"
								id="usercreation-message">
								<i class="fa-solid fa-thumbs-up"></i>
								<p>${success}</p>
							</div>
						</c:if>
						<c:if test="${not empty failed}">
							<div class="usercreation-message failed"
								id="usercreation-message">
								<i class="fa-solid fa-thumbs-down"></i>
								<p>${failed}</p>
							</div>
						</c:if>
						<input type="hidden" id="userId" value="${user.getUserId()}"
							name="userId"> <label for="new-password">Old
							Password</label>
						<c:choose>
							<c:when test="${not empty success}">
								<input type="password" id="old-password" name="oldpassword"
									value="" placeholder="Enter Old Password" required>
							</c:when>
							<c:otherwise>
								<input type="password" id="old-password" name="oldpassword"
									value="${param.oldpassword}" placeholder="Enter Old Password"
									required>
							</c:otherwise>
						</c:choose>
						<c:if test="${not empty wrongPassword}">
							<div id="invalid-account-error"
								class="invalid-accountnumber-error">
								<i class="fa-solid fa-triangle-exclamation"></i>
								<p>${wrongPassword}</p>
							</div>
						</c:if>
						<label for="new-password">New Password</label>
						<c:choose>
							<c:when test="${not empty success}">
								<input type="password" id="new-password" name="newpassword"
									value="" placeholder="Enter New Password" required>
							</c:when>
							<c:otherwise>
								<input type="password" id="new-password" name="newpassword"
									value="${param.newpassword}" placeholder="Enter New Password"
									required>
							</c:otherwise>
						</c:choose>
						<c:if test="${not empty InvalidPassword}">
							<div id="invalid-account-error"
								class="invalid-accountnumber-error password-font">
								<i class="fa-solid fa-triangle-exclamation"></i>
								<p>${InvalidPassword}</p>
							</div>
						</c:if>
						<label for="confirm-password">Confirm Password</label>
						<c:choose>
							<c:when test="${not empty success}">
								<input type="password" id="confirm-password"
									name="confirmpassword" value=""
									placeholder="Enter Confirm Password" required>
							</c:when>
							<c:otherwise>
								<input type="password" id="confirm-password"
									name="confirmpassword" value="${param.confirmpassword}"
									placeholder="Enter Confirm Password" required>
							</c:otherwise>
						</c:choose>
						<c:if test="${not empty diffPassword}">
							<div id="invalid-account-error"
								class="invalid-accountnumber-error">
								<i class="fa-solid fa-triangle-exclamation"></i>
								<p>${diffPassword}</p>
							</div>
						</c:if>
						<input type="submit" value="Update">
					</form>
				</div>
			</div>
		</div>
	</div>
	<script>
		document
				.addEventListener(
						"DOMContentLoaded",
						function() {
							document.getElementById("update-password-button")
									.addEventListener("click",
											clearSuccessMessage);
							function clearSuccessMessage() {
								document
										.querySelector('.usercreation-message.success').innerHTML = '';
							}
						});

		window.onload = function() {
			var modal = document.getElementById("modal");
			var invalidAccountError = document
					.getElementById("invalid-account-error");
			var messageAboutDeposit = document
					.getElementById("usercreation-message");

			if ((invalidAccountError && invalidAccountError.innerText.trim() !== "")
					|| (messageAboutDeposit && messageAboutDeposit.innerText
							.trim() !== "")) {
				modal.style.display = "block";
			}
		};

		var modal = document.getElementById("modal");
		var btn = document.getElementById("update-password-button");
		var span = document.getElementsByClassName("close")[0];
		btn.onclick = function() {
			modal.style.display = "block";
		}
		span.onclick = function() {
			modal.style.display = "none";
		}
		window.onclick = function(event) {
			if (event.target == modal) {
				modal.style.display = "none";
			}
		}
	</script>
</body>
</html>