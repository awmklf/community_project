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
<link rel="icon" href="/img/favicon.png">
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100..900&display=swap" rel="stylesheet">
<title>회원가입 - 커뮤니티</title>
<style>
	body {
		margin: 0px;
		margin-top: 30px;
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
		max-width: 550px;
		width: 100%;
		padding: 20px;
		box-sizing: border-box;
		border: 0px;
		text-align: center;
		border: 1px solid #ccc;
		border-radius: 5px;
	}
	
	form {
		height: 470px;
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
		margin-right: 10px; display : inline-block;
		text-align: right;
		width: 150px;
		display: inline-block;
	}
	
	input {
		width: 250px;
		height: 30px;
	}
	
	select {
		width: 258px;
		height: 36px;
	}
	
	#btn {
		width: 150px;
		height: 50px;
		font-size: 18px;
		padding: 10px 20px;
	}
</style>
</head>
<body>
<div class="container">
	<div style="border: none;'">
		<h1 style="margin-top: 15px;"><a href="/">COMMUNITY</a></h1>
	</div>
	<div class="form-container" style="border: 1px solid #ccc;">
		<h2>회원가입</h2>
		<form action="/user/signup" method="post" id="signUpForm">
				<div style="display: flex; align-items: center;">
					<div style="border: none;">
						<label for="id">아이디</label>
					</div>
					<div style="border: none;">
						<input type="text" name="userId" id="id" maxlength="20">
					</div>
				</div>

				<div style="display: flex; align-items: center;">
					<div style="border: none;">
						<label for="nickname">닉네임</label>
					</div>
					<div style="border: none;">
						<input type="text" name="nickname" id="nickname" maxlength="10">
					</div>
				</div>
				
				<div style="display: flex; align-items: center;">
					<div style="border: none;">
						<label for="pwd">비밀번호</label>
					</div>
					<div style="border: none; position: relative;">
						<input type="password" name="password" id="pwd" style="display: flex; justify-content: center;">
						<img src="/img/ico-hide.png" id="togglePwd" style="position: absolute; right: 5px; top: 50%; transform: translateY(-50%); cursor: pointer;">
					</div>
				</div>
				
				<div style="display: flex; align-items: center;">
					<div style="border: none;">
						<label for="pwdHint">비밀번호 힌트</label>
					</div>
					<div style="border: none;">
						<select name="passwordHint" id="pwdHint" required>
							<option value="취미 생활은?">취미 생활은?</option>
							<option value="애완견 이름은?">애완견 이름은?</option>
							<option value="취직하고 싶은 곳은?">취직하고 싶은 곳은?</option>
							<option value="여행가고 싶은 곳은?">여행가고 싶은 곳은?</option>
							<option value="custom">직접 입력</option>
						</select>
<!-- 						<input type="text" name="passwordHint" id="pwdHint" placeholder="비밀번호 찾기 시 질문"> -->
					</div>
				</div>
				<div class="customHintBox" style="display: none; align-items: center;">
					<div style="border: none;">
						<label for="customHint">직접입력</label>
					</div>
					<div style="border: none;">
						<input type="text" name="" id="customHint" placeholder="비밀번호 찾기 질문">
					</div>
				</div>
				
				<div style="display: flex; align-items: center;">
					<div style="border: none;">
						<label for="pwdCnsr">비밀번호 답</label>
					</div>
					<div style="border: none;">
						<input type="text" name="passwordCnsr" id="pwdCnsr">
					</div>
				</div>
			<br><span id="idMsg" class="msg"></span>
			<br><span id="nicknameMsg" class="msg"></span>
			<br><span id="pwdMsg" class="msg"></span>
			<br><span id="pwdHintMsg" class="msg"></span>
			<br><span id="pwdCnsrMsg" class="msg"></span>
			<br><button id="btn" type="submit" style="margin: 10px auto">가입</button>
			<sec:csrfInput/>
		</form>
	</div>
</div>

<script src="/js/jquery-3.7.1.min.js"></script>
<script>
$(document).ready(function(){
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
    
    // 아이디 유효성 검사
    $("#id").keyup(validateId);
    
 	// 닉네임 유효성 검사
    $("#nickname").keyup(validateNickname);
    
 	// 비밀번호 유효성 검사
    $("#pwd").keyup(validatePwd);
 	
	// 비밀번호 질문 유효성 검사
    $("#customHint").keyup(validatePwdQnA);
	
	// 비밀번호 답변 유효성 검사
    $("#pwdCnsr").keyup(validatePwdQnA);

	// 가입 제출 시 최종 유효성 검사
	$("#signUpForm").on("submit", async function(event){
		event.preventDefault();  // 폼 제출 방지
		try {
			const idStatus = await validateId();
			const nicknameStatus = await validateNickname();
			const pwdStatus = await validatePwd();
			const pwdQnAStatus = await validatePwdQnA();
			
			// 모든 유효성 검사가 'green'인지 확인
			if(idStatus === "green" && nicknameStatus === "green" 
				&& pwdStatus === "green" && pwdQnAStatus === "green") {
				if(confirm("회원가입을 진행하시겠습니까?")) {
					$("#signUpForm").off("submit").submit();
				}
			} else {
				alert("필드값을 확인해주세요.");
			}
		} catch (error) {
			console.error(error);
			alert("유효성 검사 중 오류가 발생했습니다.");
		}
	});
     
    // 아이디 유효성 검사 로직
    async function validateId() {
		var userId = $("#id").val();
		var response = null;
		try {
			response = await $.ajax({
				url: '/user/checkId',
				type: 'post',
				data: {userId: userId},
				beforeSend: function(xhr){
					xhr.setRequestHeader(header, token);
				}
			});
			if(response.idStatus == "green"){ // 사용가능
				$('#idMsg').css("color", "green").html("아이디 : 사용할 수 있습니다.");
			} else if (response.idStatus == "null") { // 아이디 미입력
				$('#idMsg').css("color", "red").html("아이디 : 필수 정보입니다.");
			} else if (response.idStatus == "valid") { // 5~20자리 소문자,숫자 조건 미충족
				$('#idMsg').css("color", "red").html("아이디 : 5~20자의 영문 소문자, 숫자만 사용 가능합니다.");
			} else if (response.idStatus == "dupl") { // 중복된 아이디
				$('#idMsg').css("color", "red").html("아이디 : 중복되어 사용할 수 없습니다.");
			} else {
				$('#idMsg').html("");
			}
		} catch (error) {
			console.error(error);
			alert("아이디 유효성 검사 중 오류가 발생했습니다.");
		}
		return response.idStatus;
    }
    
    // 닉네임 유효성 검사 로직
	async function validateNickname() {
		var nickname = $("#nickname").val();
		var response = null;
		try {
			response = await $.ajax({
				url: '/user/checkNickname',
				type: 'post',
				data: {nickname: nickname},
				beforeSend: function(xhr){
					xhr.setRequestHeader(header, token);
				}
			});
			if(response.nicknameStatus == "green"){ // 사용가능
				$('#nicknameMsg').css("color", "green").html("닉네임 : 사용할 수 있습니다.");
			} else if (response.nicknameStatus == "null") { // 닉네임 미입력
				$('#nicknameMsg').css("color", "red").html("닉네임 : 필수 정보입니다.");
			} else if (response.nicknameStatus == "valid") { // 2~10자의 한글, 영문, 숫자 조건 미충족
				$('#nicknameMsg').css("color", "red").html("닉네임 : 2~10자의 한글, 영문, 숫자만 사용 가능합니다.");
			} else if (response.nicknameStatus == "dupl") { // 중복된 닉네임
				$('#nicknameMsg').css("color", "red").html("닉네임 : 중복되어 사용할 수 없습니다.");
			} else {
				$('#nicknameMsg').html("");
			}
		} catch (error) {
			console.error(error);
			alert("닉네임 유효성 검사 중 오류가 발생했습니다.");
		}
		return response.nicknameStatus;
	}
	 
	// 비밀번호 유효성 검사 로직
	async function validatePwd() {
		var password = $("#pwd").val();
		var response = null;
		try {
			response= await $.ajax({
				url: '/user/checkPwd',
				type: 'post',
				data: {password: password},
				beforeSend: function(xhr){
					xhr.setRequestHeader(header, token);
				}
			});
			if(response.pwdStatus == "green"){ // 사용가능
				$('#pwdMsg').css("color", "green").html("비밀번호 : 사용할 수 있습니다.");
			} else if (response.pwdStatus == "null") { // 비밀번호 미입력
				$('#pwdMsg').css("color", "red").html("비밀번호 : 필수 정보입니다.");
			} else if (response.pwdStatus == "valid") { // 8자 이상 영문, 숫자, 두 종류 이상 조합, 동일 문자 4개 미만
				$('#pwdMsg').css("color", "red").html("비밀번호 : 8자 이상이어야 하며 영문자, 숫자, 특수문자 중 두 가지 이상의 조합이어야 합니다. 연속된 4개의 같은 문자는 사용불가능 합니다.");
			} else {
				$('#pwdMsg').html("");
			}
		} catch (error) {
			console.error(error);
			alert("비밀번호 유효성 검사 중 오류가 발생했습니다.");
		}
		return response.pwdStatus;
	}

	// 비밀번호 질문, 답변 유효성 검사
	async function validatePwdQnA() {
		var passwordHint = $("#pwdHint").val();
		console.log(passwordHint);
		if (passwordHint == 'custom') {
			var customHint = $('#customHint').val();
			console.log(customHint);
			passwordHint = customHint;
		}
		var passwordCnsr = $("#pwdCnsr").val();
		var response = null;
		var pwdQnA = null;
		
		try {
			response = await $.ajax({
				url: '/user/checkPwdQnA',
				type: 'post',
				data: {passwordHint: passwordHint
						, passwordCnsr: passwordCnsr
				},
				beforeSend: function(xhr){
					xhr.setRequestHeader(header, token);
				}
			});
			if (response.pwdHintStatus == "green" && response.pwdCnsrStatus == "green") { // 모두 입력됨
				pwdQnA = "green"
			}
			if (response.pwdHintStatus != "green") { // 비밀번호 찾기 질문 미입력
				$('#pwdHintMsg').css("color", "red").html("비밀번호 힌트 : 질문을 입력해 주세요.");
			} else {
				$('#pwdHintMsg').html("");
			}
			if (response.pwdCnsrStatus != "green") { // 비밀번호 찾기 답변 미입력
				$('#pwdCnsrMsg').css("color", "red").html("비밀번호 답 : 답변을 입력해 주세요.");
			} else {
				$('#pwdCnsrMsg').html("");
			}
		} catch (error) {
			console.error(error);
			alert("비밀번호 질문, 답변의 유효성 검사 중 오류가 발생했습니다.");
		}
		return pwdQnA;
	}
	
	// 공백제거
	$('input').on('input', function() {
		$(this).val($(this).val().replace(/\s/g, ''));
	});
	
	// 비밀번호 보이기/숨기기
	$("#togglePwd").click(function(){
		if ($("#pwd").attr("type") === "password") {
			$("#pwd").attr("type", "text");
			$(this).attr('src', '/img/ico-show.png');
		} else {
			$("#pwd").attr("type", "password");
			$(this).attr('src', '/img/ico-hide.png');
		}
	});
	
	// 비밀번호 답변 직접입력 활성화
	$('#pwdHint').change(function () {
		var customHint = $('.customHintBox');
		if (this.value == 'custom') {
			$('#pwdHint').attr('name', '');
			$('#customHint').attr('name', 'passwordHint');
			customHint.css('display', 'flex');
		} else {
			$('#pwdHint').attr('name', 'passwordHint');
			$('#customHint').attr('name', '');
			$('#customHint').val('');
			customHint.hide();
		}
	});

});
</script>

</body>
</html>