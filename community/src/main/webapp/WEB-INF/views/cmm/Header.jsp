<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Community</title>
<style>
	table {
		width: 100%;
		border-collapse: collapse;
	}
	th, td {
		padding: 10px;
	}
	thead {
		background-color: #f2f2f2;
	}
	tr:nth-child(even) {
		background-color: #f9f9f9;
	}
	div {
		margin: 20px;
		padding: 10px;
		border: 1px solid #ccc;
		background-color: #f9f9f9;
	}
</style>
<script src="/js/jquery-3.7.1.min.js"></script>

</head>
<body>
	<header class="">
		<div class="">
			<div class="">
				<ul>
					<li><a href="/">HOME</a></li>
					<c:choose>
						<c:when test="${empty userId}">
							<li><a href="/user/login">로그인</a></li>
						</c:when>
						<c:otherwise>
							<li><strong><c:out value="${nickname}"/></strong>님 환영합니다</li>
							<form id="logoutForm" method="POST" action="/user/logout">
								<sec:csrfInput/>
							</form>
							<li>
							<a href="#" onclick="document.getElementById('logoutForm').submit();">로그아웃</a>
							</li>
						</c:otherwise>
					</c:choose>
				</ul>
			</div>
		</div>
	</header>