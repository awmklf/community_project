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
<meta name="csrf-token" content="<c:out value='${_csrf.token}'/>"/>
<meta name="csrf-header" content="<c:out value='${_csrf.headerName}'/>"/>
<title>Community</title>
</head>
<body>
<div>
	<div>
		<a href="/">HOME</a>
	</div>
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
		<span id="pwdValidMsg" class="msg" style="display: none;">8자리 이상만 사용 가능합니다.</span>
		<br>
		<label for="pwdHint">비밀번호 힌트 : </label>
		<input type="text" name="passwordHint" id="pwdHint" placeholder="비밀번호 찾기 시 질문" required="required">
		<span id="pwdHintNullMsg" class="msg" style="display: none;">비밀번호 찾기 질문을 입력해 주세요.</span>
		<br>
		<label for="pwdCnsr">비밀번호 답 : </label>
		<input type="text" name="passwordCnsr" id="pwdCnsr" placeholder="비밀번호 찾기 시 답변" required="required">
		<span id="pwdCnsrNullMsg" class="msg" style="display: none;">비밀번호 찾기 답변을 입력해 주세요.</span>
		<br>
		<button id="btn" type="submit">가입</button>
		<sec:csrfInput/>
	</form>
</div>
	
	<script src="/js/jquery-3.7.1.min.js"></script>
	<script>
		$(document).ready(function(){
			
			var token = $("meta[name='csrf-token']").attr("content");
		    var header = $("meta[name='csrf-header']").attr("content");
		    
		    // 아이디 유효성 검사
		    $("#id").keyup(validateId);
		    
		 	// 닉네임 유효성 검사
		    $("#nickname").keyup(validateNickname);
		    
		 	// 비밀번호 유효성 검사
		    $("#pwd").keyup(validatePwd);

			// 가입 제출 시 최종 유효성 검사
			$("#signUpForm").on("submit", function(event){
				event.preventDefault();  // 폼 제출 방지
				Promise.all([validateId(), validateNickname(), validatePwd()]).then(function(results) {
				// 모든 유효성 검사가 'green'인지 확인
				if(results.every(status => status == "green")) {
					if(confirm("회원가입을 진행하시겠습니까?")) {
						$("#signUpForm").off("submit").submit();
					}
				} else {
					alert("필드값을 확인해주세요.")
				}
				}).catch(function(error) {
					console.error(error);
					alert("유효성 검사 중 오류가 발생했습니다.");
				});
			});
		     
		    // 아이디 유효성 검사 로직
		    function validateId() {
				return new Promise(function(resolve, reject) {
					var userId = $("#id").val();
					$.ajax({
						url: '/user/idCheck',
						type: 'post',
						data: {userId: userId},
						beforeSend: function(xhr){
							// 요청 헤더에 CSRF 토큰 값 설정
							xhr.setRequestHeader(header, token);
						},
						success: function(response){
							$("#idGreenMsg").hide();
							$("#idNullMsg").hide();
							$("#idValidMsg").hide();
							$("#idDuplMsg").hide();
							
							if(response.idStatus == "green"){ // 사용가능
								$("#idGreenMsg").show();
								resolve("green");
							} else if (response.idStatus == "null") { // 아이디를 입력
								$("#idNullMsg").show();
								resolve("null");
							} else if (response.idStatus == "valid") { // 5~20자리 소문자,숫자만 가능
								$("#idValidMsg").show();
								resolve("valid");
							} else if (response.idStatus == "dupl") { // 중복된 아이디
								$("#idDuplMsg").show();
							}
						},
						error: function(error){
							reject(error);
						}
					});
				});
		    }

			 // 닉네임 유효성 검사 로직
			function validateNickname() {
				return new Promise(function(resolve, reject) {
					var nickname = $("#nickname").val();
					$.ajax({
						url: '/user/nicknameCheck',
						type: 'post',
						data: {nickname: nickname},
						beforeSend: function(xhr){
							// 요청 헤더에 CSRF 토큰 값 설정
							xhr.setRequestHeader(header, token);
						},
						success: function(response){
							$("#nicknameGreenMsg").hide();
							$("#nicknameNullMsg").hide();
							$("#nicknameValidMsg").hide();
							$("#nicknameDuplMsg").hide();
							
							if(response.nicknameStatus == "green"){ // 사용가능
								$("#nicknameGreenMsg").show();
								resolve("green");
							} else if (response.nicknameStatus == "null") { // 닉네임을 입력
								$("#nicknameNullMsg").show();
								resolve("null");
							} else if (response.nicknameStatus == "valid") { // 2~10자의 한글, 영문, 숫자만 사용 가능
								$("#nicknameValidMsg").show();
								resolve("valid");
							} else if (response.nicknameStatus == "dupl") { // 중복된 닉네임
								$("#nicknameDuplMsg").show();
								resolve("dupl");
							}
						},
						error: function(error){
							reject(error);
						}
					});
		   		});
			}
			 
			// 비밀번호 유효성 검사 로직
			function validatePwd() {
				return new Promise(function(resolve, reject) {
					var password = $("#pwd").val();
					$.ajax({
						url: '/user/pwdCheck',
						type: 'post',
						data: {password: password},
						beforeSend: function(xhr){
							// 요청 헤더에 CSRF 토큰 값 설정
							xhr.setRequestHeader(header, token);
						},
						success: function(response){
							$("#pwdGreenMsg").hide();
							$("#pwdNullMsg").hide();
							$("#pwdValidMsg").hide();
							
							if(response.pwdStatus == "green"){ // 사용가능
								$("#pwdGreenMsg").show();
								resolve("green");
							} else if (response.pwdStatus == "null") { // 8자이상 사용 가능
								$("#pwdNullMsg").show();
								resolve("null");
							} else if (response.pwdStatus == "valid") { // 비밀번호를 입력
								$("#pwdValidMsg").show();
								resolve("valid");
							} 
						},
						error: function(error){
							reject(error);
						}
					});
				});
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