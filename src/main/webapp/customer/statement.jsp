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
			<li><a href="<%=request.getContextPath()%>/customer/account">Accounts</a></li>
			<li><a href="<%=request.getContextPath()%>/customer/transaction">Transactions</a></li>
			<li><a href="<%=request.getContextPath()%>/customer/Statement"
				class="active">Statements</a></li>
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
	<div class="transaction-page statement-page">
		<form id="searchFormTransaction"
			action="<%=request.getContextPath()%>/getStatements" method="post">
			<div class="transaction-search">
				<div>
					<div>
						<input type="hidden" id="currentAccountId" name="accountNumber"
							value="${currentAccount.accountNumber}"> <span>From</span>
						<input type="date" name="fromDate" value="${param.fromDate}"
							required> <span>To</span> <input type="date"
							name="toDate" value="${param.toDate}" required>
						<button type="submit">View</button>
					</div>
					<c:if test="${not empty dateError}">
						<div class="invalid-userid-error">
							<i class="fa-solid fa-triangle-exclamation"></i>
							<p>${dateError}</p>
						</div>
					</c:if>
					<c:if test="${not empty message}">
						<div class="invalid-userid-error">
							<p>${message}</p>
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
						<th>Transaction Id</th>
						<th>Date</th>
						<th>Transacted Account</th>
						<th>Type</th>
						<th>Amount</th>
						<th>Balance</th>
						<th>Remark</th>
						<th>Status</th>
					</tr>
				</thead>
			</c:if>
			<tbody>
				<c:set var="serialNumber" value="1" />
				<c:forEach var="transaction" items="${transactionList}">
					<tr>
						<td>${serialNumber}</td>
						<td>${transaction.referenceId}</td>
						<td>${DateUtils.formateLongToDate(transaction.dateOfTransaction)}</td>
						<td>${transaction.transactedAccount}</td>
						<td>${transaction.transactionType}</td>
						<td>₹${transaction.transactedAmount}</td>
						<td>₹${transaction.balance}</td>
						<td>${transaction.remark}</td>
						<td>${transaction.status}</td>
					</tr>
					<c:set var="serialNumber" value="${serialNumber + 1}" />
				</c:forEach>
			</tbody>
		</table>
	</div>
	<div class="pagination" id="pagination"></div>
	<script>
	
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

        if (totalPages > 1) {
            const prevButton = document.createElement('button');
            prevButton.textContent = '<<';
            prevButton.addEventListener('click', function () {
                if (currentPage > 1) {
                    currentPage--;
                    renderPaginationButtons();
                    scrollToTop();
                }
            });
            paginationContainer.appendChild(prevButton);

            if (totalPages <= 5) {
                for (let i = 1; i <= totalPages; i++) {
                    createButton(i);
                }
            } else {
                let startPage, endPage;
                if (currentPage <= 3) {
                    startPage = 1;
                    endPage = 4;
                } else if (currentPage >= totalPages - 2) {
                    startPage = totalPages - 2;
                    endPage = totalPages;
                } else {
                    startPage = currentPage - 2;
                    endPage = currentPage + 2;
                }

                if (startPage > 1) {
                    createButton(1);
                    if (startPage > 2) {
                        const ellipsisButtonStart = document.createElement('button');
                        ellipsisButtonStart.textContent = '...';
                        ellipsisButtonStart.disabled = true;
                        paginationContainer.appendChild(ellipsisButtonStart);
                    }
                }

                for (let i = startPage; i <= endPage; i++) {
                    createButton(i);
                }

                if (endPage < totalPages) {
                    if (endPage < totalPages - 1) {
                        const ellipsisButtonEnd = document.createElement('button');
                        ellipsisButtonEnd.textContent = '...';
                        ellipsisButtonEnd.disabled = true;
                        paginationContainer.appendChild(ellipsisButtonEnd);
                    }
                    createButton(totalPages);
                }
            }

            const nextButton = document.createElement('button');
            nextButton.textContent = '>>';
            nextButton.addEventListener('click', function () {
                if (currentPage < totalPages) {
                    currentPage++;
                    renderPaginationButtons();
                    scrollToTop();
                }
            });
            paginationContainer.appendChild(nextButton);

            displayRows((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);
        } else {
            // If there's only one page, do not display pagination buttons
            displayRows(0, rows.length);
        }
    }

    function createButton(pageNumber) {
        const button = document.createElement('button');
        button.textContent = pageNumber;
        button.addEventListener('click', function () {
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