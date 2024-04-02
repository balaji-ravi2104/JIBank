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
	<%
	response.setHeader("Cache-Control", "no-cache,no-store,must-revalidate");
	response.setHeader("Pragma", "no-cache");

	if (session.getAttribute("user") == null) {
		response.sendRedirect(request.getContextPath() + "/login");
	}
	%>
	<div class="navbar-home">
		<div class="logo">
			<img src="<%=request.getContextPath()%>/images/logo.png" alt="logo">
		</div>
		<div>
			<li><a href="<%=request.getContextPath()%>/employee/customer">Customers</a></li>
			<li><a href="<%=request.getContextPath()%>/employee/account">Accounts</a></li>
			<li><a href="<%=request.getContextPath()%>/employee/transaction"
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
		<div class="search-bar">
			<div>
				<button id="deposit-button" onclick="clearSuccessAttribute()">Deposit</button>
			</div>
			<div>
				<button id="withdraw-button" onclick="clearSuccessAttribute()">Withdraw</button>
			</div>
		</div>
		<form id="searchFormTransaction"
			action="<%=request.getContextPath()%>/getTransactions" method="post">
			<div class="transaction-search">
				<div>
					<input type="text" id="searchTransaction" name="accountNumber"
						pattern="\d{12}" maxlength="12" placeholder="Enter Account Number"
						value="${param.accountNumber}" required>
					<c:if test="${not empty error}">
						<div class="invalid-userid-error">
							<i class="fa-solid fa-triangle-exclamation"></i>
							<p>${error}</p>
						</div>
					</c:if>
					<c:if test="${not empty message}">
						<div class="invalid-userid-error">
							<p>${message}</p>
						</div>
					</c:if>
				</div>
				<div>
					<div>
						<span>From</span> <input type="date" name="fromDate"
							value="${param.fromDate}" required> <span>To</span> <input
							type="date" name="toDate" value="${param.toDate}" required>
						<button type="submit">View</button>
					</div>
					<c:if test="${not empty dateError}">
						<div class="invalid-userid-error">
							<i class="fa-solid fa-triangle-exclamation"></i>
							<p>${dateError}</p>
						</div>
					</c:if>
				</div>
			</div>
		</form>
	</div>

	<div class="customerlist-container">
		<table id="table">
			<c:if test="${not empty transactionList}">
				<thead>
					<tr>
						<th>S.No</th>
						<th>Trans Id</th>
						<th>User Id</th>
						<th>Viewer Account</th>
						<th>Transacted Account</th>
						<th>Type</th>
						<th>Amount</th>
						<th>Balance</th>
						<th>Date</th>
						<th>Remark</th>
						<th>Status</th>
						<th>Reference Id</th>
					</tr>
				</thead>
			</c:if>
			<tbody>
				<c:set var="serialNumber" value="1" />
				<c:forEach var="transaction" items="${transactionList}">
					<tr>
						<td>${serialNumber}</td>
						<td>${transaction.transactionId}</td>
						<td>${transaction.userId}</td>
						<td>${transaction.viewerAccount}</td>
						<td>${transaction.transactedAccount}</td>
						<td>${transaction.transactionType}</td>
						<td>₹${transaction.transactedAmount}</td>
						<td>₹${transaction.balance}</td>
						<td>${DateUtils.formateLongToDate(transaction.dateOfTransaction)}</td>
						<td>${transaction.remark}</td>
						<td>${transaction.status}</td>
						<td>${transaction.referenceId}</td>
					</tr>
					<c:set var="serialNumber" value="${serialNumber + 1}" />
				</c:forEach>
			</tbody>
		</table>

		<div id="modal" class="modal">
			<div class="modal-content">
				<span class="close">&times;</span>
				<div id="update-password-form">
					<form id="transaction-form" action="" method="post">
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
							<div class="usercreation-message failed"
								id="usercreation-message">
								<i class="fa-solid fa-thumbs-down"></i>
								<p>${failed}</p>
							</div>
						</c:if>
						<label for="accountNumber">Account Number</label>
						<c:choose>
							<c:when test="${not empty success}">
								<input type="text" id="accountNumber"
									name="transactionaccountNumber" pattern="\d{12}" maxlength="12"
									placeholder="Enter the AccountNumber" value="" required>
							</c:when>
							<c:otherwise>
								<input type="text" id="accountNumber"
									name="transactionaccountNumber" pattern="\d{12}" maxlength="12"
									placeholder="Enter the AccountNumber"
									value="${param.transactionaccountNumber}" required>
							</c:otherwise>
						</c:choose>
						<c:if test="${not empty invalidAccount}">
							<div id="invalid-account-error"
								class="invalid-accountnumber-error">
								<i class="fa-solid fa-triangle-exclamation"></i>
								<p>${invalidAccount}</p>
							</div>
						</c:if>
						<label id="amount-label" for="amount">${transactionType}</label>
						<c:choose>
							<c:when test="${not empty success}">
								<input type="number" id="amount" name="amount" value=""
									placeholder="Enter the Amount" required>
							</c:when>
							<c:otherwise>
								<input type="number" id="amount" name="amount"
									value="${param.amount}" placeholder="Enter the Amount" required>
							</c:otherwise>
						</c:choose>
						<c:if test="${not empty invalidBalance}">
							<div id="invalid-account-error"
								class="invalid-accountnumber-error">
								<i class="fa-solid fa-triangle-exclamation"></i>
								<p>${invalidBalance}</p>
							</div>
						</c:if>
						<label for="description">Description</label>
						<c:choose>
							<c:when test="${not empty success}">
								<input type="text" name="description" value=""
									placeholder="Enter Small Description" required>
							</c:when>
							<c:otherwise>
								<input type="text" name="description"
									value="${param.description}"
									placeholder="Enter Small Description" required>
							</c:otherwise>
						</c:choose>
						<input type="submit" id="submit" value="${submitType}">
					</form>
				</div>
			</div>
		</div>
	</div>
	<div class="pagination" id="pagination"></div>
	<script>
 	
	document.addEventListener("DOMContentLoaded", function() {
        document.getElementById("deposit-button").addEventListener("click", clearSuccessMessage);
        document.getElementById("withdraw-button").addEventListener("click", clearSuccessMessage);

        function clearSuccessMessage() {
            document.querySelector('.usercreation-message.success').innerHTML = '';
        }
    });
	
    window.onload = function() {
        var modal = document.getElementById("modal");
        var invalidAccountError = document.getElementById("invalid-account-error");
        var messageAboutDeposit = document.getElementById("usercreation-message");
        
        if ((invalidAccountError && invalidAccountError.innerText.trim() !== "") ||
            (messageAboutDeposit && messageAboutDeposit.innerText.trim() !== "")) {
            modal.style.display = "block";
        }
    };

	
	// Deposite and Withdraw Form...
	var modal = document.getElementById("modal");
    var depositBtn = document.getElementById("deposit-button");
    var withdrawBtn = document.getElementById("withdraw-button");
    var span = document.getElementsByClassName("close")[0];
    var amountLabel = document.getElementById("amount-label");
    var submitButton = document.getElementById("submit");
    var form = document.getElementById("transaction-form");
    
    depositBtn.onclick = function() {
        modal.style.display = "block";
        amountLabel.textContent = "Deposit Amount";
        submitButton.value = "Deposit";
        form.action = "<%=request.getContextPath()%>/employeeDeposit";
    }

    withdrawBtn.onclick = function() {
        modal.style.display = "block";
        amountLabel.textContent = "Withdraw Amount";
        submitButton.value = "Withdraw";
        form.action = "<%=request.getContextPath()%>/employeeWithdraw";
    }

    span.onclick = function() {
        modal.style.display = "none";
    }

    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }

	// Pagination..
    const table = document.getElementById('table');
    const tbody = table.querySelector('tbody');
    const rows = Array.from(tbody.getElementsByTagName('tr'));
    const itemsPerPage = 10;
    let currentPage = 1;

    function displayRows(startIndex, endIndex) {
        rows.forEach((row, index) => {
            if (index >= startIndex && index < endIndex) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });
    }

    function renderPaginationButtons() {
        const totalPages = Math.ceil(rows.length / itemsPerPage);
        const paginationContainer = document.getElementById('pagination');
        paginationContainer.innerHTML = '';

        // Check if pagination is necessary
        if (totalPages > 1) {
            const prevButton = document.createElement('button');
            prevButton.textContent = '<<';
            prevButton.addEventListener('click', function() {
                if (currentPage > 1) {
                    currentPage--;
                    renderPaginationButtons();
                    scrollToTop();
                }
            });
            paginationContainer.appendChild(prevButton);

 
            for (let i = 1; i <= totalPages; i++) {
                createButton(i);
            }

            const nextButton = document.createElement('button');
            nextButton.textContent = '>>';
            nextButton.addEventListener('click', function() {
                if (currentPage < totalPages) {
                    currentPage++;
                    renderPaginationButtons();
                    scrollToTop();
                }
            });
            paginationContainer.appendChild(nextButton);

          
            displayRows((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);
        } else {
 
            rows.forEach(row => {
                row.style.display = '';
            });
        }
    }

    function createButton(pageNumber) {
        const button = document.createElement('button');
        button.textContent = pageNumber;
        button.addEventListener('click', function() {
            currentPage = pageNumber;
            renderPaginationButtons();
            scrollToTop();
        });
        if (pageNumber === currentPage) {
            button.classList.add('active');
        }
        document.getElementById('pagination').appendChild(button);
    }

    function scrollToTop() {
        window.scrollTo({
            top: 0,
            left: 0,
            behavior: 'smooth'
        });
    }

    renderPaginationButtons();
</script>

</body>
</html>