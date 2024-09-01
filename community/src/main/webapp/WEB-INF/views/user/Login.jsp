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
<link rel="icon" href="/img/favicon.png">
<title>로그인 - 커뮤니티</title>
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
	    text-align: center;
	}
	div {
			margin: 20px;
			padding: 10px;
			border: 1px solid #ccc;
			background-color: #f9f9f9;
	}
	label {
		display:inline-block;
		text-align:right;
		width:150px;
	}
	a {
		text-decoration: none;
		color: black;
	}
</style>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
</head>
<body>
<div class="container">
	<h2 style="margin: 0 auto;">로그인</h2>
	<c:url var="url" value="/user"/>
	<div>
		<form action="${url}/login" method="post" id="loginForm">
			<label for="id">아이디 : </label>
			<input type="text" name="userId" id="id">
			<br>
			<label for="pwd">비밀번호 : </label>
			<input type="password" name="password" id="pwd">
			<br>
			<input type="submit" value="로그인" style="margin: 10px;">
			<sec:csrfInput/>
		</form>
	</div>
	<div>
	<a href="${url}/recoverPwd">비밀번호 찾기</a> | 
	<a href="${url}/signup">회원가입</a>
	</div>
</div>
	
<script>
// 로그인 실패 메세지
<c:if test="${not empty sessionScope.errorMessage}">
	alert("<c:out value='${sessionScope.errorMessage}'/>");
	<c:remove var="errorMessage" scope="session"/>
</c:if>
</script>
<script>
$(document).ready(function() {
	console.log("dd");
	$('#loginForm').submit(function(event) {
        event.preventDefault(); // 기본 제출 동작 방지
        if (!regist()) {
			return false;
		}
        this.submit();
    });
	
	//미입력 방지
	function regist() {
		if (!$("#id").val().trim()) {
			alert("아이디를 입력해주세요.");
			$("#id").focus();
			return false;
		}
		if (!$("#pwd").val().trim()) {
			alert("비밀번호를 입력해주세요.");
			$("#pwd").focus();
			return false;
		}
		return true;
	}
});
</script>



</body>
</html>