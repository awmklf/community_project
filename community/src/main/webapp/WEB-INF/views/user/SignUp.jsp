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
<title>회원가입 - 커뮤니티</title>
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
	input {
		width: 250px;
		height: 30px;
	}
	#btn {
		display: block; 
   		margin: 0 auto;
	}
</style>
</head>
<body>
<div class="container">
	<div>
		<a href="/">HOME</a>
	</div>
	<h2>회원가입</h2>
	<div style="text-align: left;">
		<c:url var="url" value="/user"/>
		<form action="${url}/signup" method="post" id="signUpForm">
			<label for="id">아이디 : </label>
			<input type="text" name="userId" id="id" placeholder="아이디(5~20자의 영문 소문자, 숫자)" maxlength="20">
			<span id="idGreenMsg" class="msg" style="display: none;">사용가능한 아이디입니다.</span>
			<span id="idNullMsg" class="msg" style="display: none;">아이디를 입력해 주세요.</span>
			<span id="idValidMsg" class="msg" style="display: none;">5~20자의 영문 소문자, 숫자만 사용 가능합니다.</span>
			<span id="idDuplMsg" class="msg" style="display: none;">이미 사용중인 아이디입니다.</span>
			<br>
			<label for="nickname">닉네임 : </label>
			<input type="text" name="nickname" id="nickname" placeholder="닉네임(2~10자의 한글, 영문, 숫자)" maxlength="10">
			<span id="nicknameGreenMsg" class="msg" style="display: none;">사용 가능한 닉네임입니다.</span>
			<span id="nicknameNullMsg" class="msg" style="display: none;">닉네임을 입력해 주세요.</span>
			<span id="nicknameValidMsg" class="msg" style="display: none;">2~10자의 한글, 영문, 숫자만 사용 가능합니다.</span>
			<span id="nicknameDuplMsg" class="msg" style="display: none;">이미 사용중인 닉네임입니다.</span>
			<br>
			<label for="pwd">비밀번호 : </label>
			<input type="password" name="password" id="pwd" placeholder="비밀번호(8자리 이상)">
			<button type="button" id="pwdToggleBtn">show/hide</button>
			<span id="pwdGreenMsg" class="msg" style="display: none;">사용 가능한 비밀번호입니다.</span>
			<span id="pwdNullMsg" class="msg" style="display: none;">비밀번호를 입력해 주세요.</span>
			<span id="pwdValidMsg" class="msg" style="display: none;">8자리 미만, 연속된 동일 문자 4자 이상 사용 불가</span>
			<br>
			<label for="pwdHint">비밀번호 힌트 : </label>
			<input type="text" name="passwordHint" id="pwdHint" placeholder="비밀번호 찾기 시 질문">
			<span id="pwdHintNullMsg" class="msg" style="display: none;">비밀번호 찾기 질문을 입력해 주세요.</span>
			<br>
			<label for="pwdCnsr">비밀번호 답 : </label>
			<input type="text" name="passwordCnsr" id="pwdCnsr" placeholder="비밀번호 찾기 시 답변">
			<span id="pwdCnsrNullMsg" class="msg" style="display: none;">비밀번호 찾기 답변을 입력해 주세요.</span>
			<br>
			<button id="btn" type="submit" style="margin: 10px auto">가입</button>
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
	    $("#pwdHint").keyup(validatePwdQnA);
		
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
					alert("필드값을 확인해주세요.")
				}
			} catch (error) {
				console.error(error);
				alert("유효성 검사 중 오류가 발생했습니다.");
			};
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

				$("#idGreenMsg").hide();
				$("#idNullMsg").hide();
				$("#idValidMsg").hide();
				$("#idDuplMsg").hide();
				
				if(response.idStatus == "green"){ // 사용가능
					$("#idGreenMsg").show();
				} else if (response.idStatus == "null") { // 아이디 미입력
					$("#idNullMsg").show();
				} else if (response.idStatus == "valid") { // 5~20자리 소문자,숫자 조건 미충족
					$("#idValidMsg").show();
				} else if (response.idStatus == "dupl") { // 중복된 아이디
					$("#idDuplMsg").show();
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

				$("#nicknameGreenMsg").hide();
				$("#nicknameNullMsg").hide();
				$("#nicknameValidMsg").hide();
				$("#nicknameDuplMsg").hide();

				if(response.nicknameStatus == "green"){ // 사용가능
					$("#nicknameGreenMsg").show();
				} else if (response.nicknameStatus == "null") { // 닉네임 미입력
					$("#nicknameNullMsg").show();
				} else if (response.nicknameStatus == "valid") { // 2~10자의 한글, 영문, 숫자 조건 미충족
					$("#nicknameValidMsg").show();
				} else if (response.nicknameStatus == "dupl") { // 중복된 닉네임
					$("#nicknameDuplMsg").show();
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

				$("#pwdGreenMsg").hide();
				$("#pwdNullMsg").hide();
				$("#pwdValidMsg").hide();
				
				if(response.pwdStatus == "green"){ // 사용가능
					$("#pwdGreenMsg").show();
				} else if (response.pwdStatus == "null") { // 8자리 이상 조건 미충족 또는 같은 문자나 숫자를 연속적으로 사용
					$("#pwdNullMsg").show();
				} else if (response.pwdStatus == "valid") { // 비밀번호 미입력
					$("#pwdValidMsg").show();
				} 
			} catch (error) {
				console.error(error);
				alert("비밀번호 유효성 검사 중 오류가 발생했습니다.");
			}
			return response.pwdStatus;
		}

		// 비밀번호 질문, 답변 유효성 검사
		async function validatePwdQnA() {
			var passwordCnsr = $("#pwdCnsr").val();
			var passwordHint = $("#pwdHint").val();
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

				$("#pwdHintNullMsg").hide();
				$("#pwdCnsrNullMsg").hide();
				
				if (response.pwdHintStatus == "green" && response.pwdCnsrStatus == "green") { // 모두 입력됨
					pwdQnA = "green"
				}
				if (response.pwdHintStatus != "green") { // 비밀번호 찾기 질문 미입력
					$("#pwdHintNullMsg").show();
				}
				if (response.pwdCnsrStatus != "green") { // 비밀번호 찾기 답변 미입력
					$("#pwdCnsrNullMsg").show();
				}
			} catch (error) {
				console.error(error);
				alert("비밀번호 질문 또는 답변의 유효성 검사 중 오류가 발생했습니다.");
			}
			return pwdQnA;
		}
		
		// 비밀번호 보이기/숨기기
		$("#pwdToggleBtn").click(function(){
			var x = $("#pwd");
			if (x.attr("type") === "password") {
			x.attr("type", "text");
			} else {
			x.attr("type", "password");
			}
		});

	});
</script>

</body>
</html>