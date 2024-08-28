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
<title>${param.title}</title>
<style>
	 body {
	    margin: 0;
	    padding: 0;
	    display: flex;
	    justify-content: center;
	}
	.container {
	    max-width: 1100px;
	    width: 100%;
	    padding: 20px;
	    box-sizing: border-box;
	    border: 0px;
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
	    margin-left: 100px;
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
	<h1 style="margin-top: 5px; margin-bottom: 30px;">COMMUNITY</h1>
	<header class="">
		<div class="">
			<a href="/">HOME</a> |
			<c:choose>
				<c:when test="${empty userId}">
					<a href="/user/login">로그인</a>
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