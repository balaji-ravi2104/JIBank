/**
 * 
 */

// Pagination
const table = document.getElementById('table');
const tbody = table.querySelector('tbody');
const rows = Array.from(tbody.getElementsByTagName('tr'));
const itemsPerPage = 20;
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

	if (totalPages <= 5) {
		for (let i = 1; i <= totalPages; i++) {
			createButton(i);
		}
	}
	else {
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
	nextButton.addEventListener('click', function() {
		if (currentPage < totalPages) {
			currentPage++;
			renderPaginationButtons();
			scrollToTop();
		}
	});
	paginationContainer.appendChild(nextButton);

	displayRows((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);
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

