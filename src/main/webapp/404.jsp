<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>JI Bank</title>
<style>
* {
	margin: 0;
	padding: 0;
	box-sizing: border-box;
}

body {
	font-family: Arial, sans-serif;
	background-color: #D3D3D3;
	display: flex;
	justify-content: center;
	align-items: center;
	height: 100vh;
}

.error-container {
	text-align: center;
	background-color: #fff;
	padding: 25px;
	border-radius: 5px;
	box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
	height: 27vh;
	width: 33%;
}

h1 {
	font-size: 5rem;
	color: #007bff;
	margin-bottom: 10px;
}

p {
	font-size: 1.5rem;
	color: #333;
	margin-bottom: 40px;
}

button {
	background-color: #007bff;
	color: #ffffff;
	padding: 15px 25px;
	border: none;
	border-radius: 5px;
	cursor: pointer;
	font-size: 18px;
	font-weight: 600;
	font-weight: bold;
	transition: background-color 0.3s ease;
}

button:hover {
	background-color: rgb(64, 123, 255);
}
</style>
</head>
<body>
	<div class="error-container">
		<h1>404</h1>
		<p>Oops! The page you're looking for is not found</p>
		<form action="<%=request.getContextPath()%>/home" method="get">
			<button type="submit">Home</button>
		</form>
	</div>
</body>
</html>