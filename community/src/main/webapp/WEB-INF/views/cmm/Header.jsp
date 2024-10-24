<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" 	uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" 		uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" 		uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt"   	uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec"		uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<sec:csrfMetaTags/>
<link rel="icon" href="/img/favicon.png">
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100..900&display=swap" rel="stylesheet">
<title>${param.title}</title>
<style>
body {
	margin: 0;
	padding: 0;
	display: flex;
	justify-content: center;
	font-family: "Noto Sans KR", sans-serif;
	font-optical-sizing: auto;
	font-weight : < weight >;
	font-style: normal;
	font-weight: <weight>;
}

.container {
	max-width: 1100px;
	width: 100%;
	margin: 5px 10px 5px;
	padding: 10px 20px 10px;
	box-sizing: border-box;
	border: 0px;
	background-color: #edede9;
}

table {
	width: 100%;
	border-collapse: collapse;
}

th, td {
	padding: 10px;
	text-align: center;
}

thead {
	background-color: #f2f2f2;
}

tr:nth-child(even) {
	background-color: #f9f9f9;
}

div {
	margin: 10px;
	padding: 10px;
	border: 1px solid #ccc;
	background-color: #f9f9f9;
	text-align: center;
}

.boardCn div {
	margin: 0px;
	padding: 0px;
	border: 0px;
	background-color: #f9f9f9;
}

a {
	text-decoration: none;
	color: black;
	border: 1px solid #ccc;
	border-radius: 5px;
}

a:hover {
	color: gray;
}

.reply-indent {
	padding: 10px;
	border: 1px solid #ccc;
	border-radius: 5px;
	background-color: #f9f9f9;
}
</style>
<!-- <script src="/js/jquery-3.7.1.min.js"></script> -->
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
</head>
<body>
	<div class="container">
	<header class="" style="height: 100px; display: flex; justify-content: center;">
		<div style="border: 0px; text-align: center; margin: 0 auto; position: absolute; background-color:transparent;">
			<h1 style="margin-top: 15px;"><a href="/" style="text-decoration: none; border: none;">COMMUNITY</a></h1>
		</div>
		<div class="" style="border: 0px; margin: 0; margin-left: auto; background-color:transparent;">
			<c:choose>
				<c:when test="${empty userId}">
					<a href="/user/login">로그인</a> | <a href="/user/signup">회원가입</a>
				</c:when>
				<c:otherwise>
					<strong><c:out value="${nickname}"/></strong>님 환영합니다 |
					<a href="#" onclick="document.getElementById('logoutForm').submit();">로그아웃</a>
					<form id="logoutForm" method="POST" action="/user/logout" style="display: none;">
						<sec:csrfInput/>
					</form>
				</c:otherwise>
			</c:choose>
		</div>
	</header>