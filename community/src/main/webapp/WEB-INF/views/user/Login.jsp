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
		margin: 0px;
		margin-top: 30px;
		padding: 0;
		display: flex;
		justify-content: center;
	}
	
	.container {
		max-width: 550px;
		width: 100%;
		padding: 20px;
		box-sizing: border-box;
		border: 0px;
		text-align: center;
		border: 1px solid #ccc;
		border-radius: 5px;
	}
	
	
	div {
		margin: 0;
		padding: 0;
		border: none;
		background-color: #f9f9f9;
	}
	
	a {
		text-decoration: none;
		color: black;
		border: 1px solid #ccc;
		border: none;
	}
	
	a:hover {
		color: gray;
	}
	
	label {
		margin-right: 10px;
		display: inline-block;
		text-align: right;
		width: 150px;
	}
	
	input {
		width: 250px;
		height: 30px;
	}
	
	#btn {
		margin: auto;
		width: 100px;
		height: 50px;
		font-size: 18px;
		padding: 10px 20px;
	}
</style>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
</head>
<body>
	<div class="container">
		<div style="border: none; margin: 0;'">
			<h1><a href="/">COMMUNITY</a></h1>
		</div>
		<div class="form-container" style="border: 1px solid #ccc;">
			<h2>로그인</h2>
			<div>
				<form action="/user/login" method="post" id="loginForm">
					<div style="display: flex; align-items: center;">
						<div style="border: none;">
							<label for="id">아이디 : </label>
						</div>
						<div style="border: none;">
							<input type="text" name="userId" id="id" maxlength="20">
						</div>
					</div>
					<div style="display: flex; align-items: center;">
						<div style="border: none;">
							<label for="pwd">비밀번호 : </label>
						</div>
						<div style="border: none;">
							<input type="password" name="password" id="pwd">
						</div>
					</div>
			</div>
			<button id="btn" type="submit" style="margin: 10px auto">로그인</button>
			<sec:csrfInput />
			</form>
		</div>
		<div style="margin-top: 15px;">
			<a href="/user/recoverPwd">비밀번호 찾기</a> | <a href="/user/signup">회원가입</a>
		</div>
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
	
	// 로그인
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
	
	// 공백제거
	$('input').on('input', function() {
		$(this).val($(this).val().replace(/\s/g, ''));
	});
});
</script>



</body>
</html>