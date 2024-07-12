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
<c:url var="url" value="/user"/>

<div>
	<a href="/">HOME</a>
</div>

<div id="findById_Box">
	<label for="id">아이디 : </label>
	<input type="text" name="userId" id="id">
	<span id="idNullMsg" style="display: none;">아이디를 입력해주세요.</span>
	<span id="notFound" style="display: none;">유효하지 않은 아이디입니다.</span> <br>
	<button type="button" id="btn">아이디 확인 </button>
</div>

<div id="pwdQNA_Box" style="display: none;">
	<label for="passwordHint">비밀번호 힌트 : </label>
	<input type="text" name="passwordHint" id="passwordHint" value="" readonly="readonly"> <br>
	<label for="passwordCnsr">비밀번호 답변 : </label>
	<input type="text" name="passwordCnsr" id="passwordCnsr">
	<span id="incorrectPwdCnsr" style="display: none;">비밀번호 답변이 올바르지 않습니다.</span> <br>
	<button type="buton" id="btn_pwdQNA">확인</button>
	<sec:csrfInput/>
</form>
</div>

<div id="changePwd_Box" style="display: none;">
	<form action="${url}/changePwd" method="post" id="changePwdForm">
		<input type="hidden" name="userId" id="hiddenUserId" readonly="readonly">
		<input type="hidden" name="allowPwdChange" id="allowPwdChange" readonly="readonly">
		<label for="pwd">변경할 비밀번호 : </label>
		<input type="password" name="password" id="pwd" placeholder="비밀번호(8자리 이상)">
		<button type="button" id="pwdToggleBtn">show/hide</button>
		<span id="pwdGreenMsg" class="msg" style="display: none;">사용 가능한 비밀번호입니다.</span>
		<span id="pwdNullMsg" class="msg" style="display: none;">비밀번호를 입력해 주세요.</span>
		<span id="pwdValidMsg" class="msg" style="display: none;">8자리 이상만 사용 가능합니다.</span>
		<br>
		<button type="submit">변경</button>
		<sec:csrfInput/>
	</form>
</div>


<script src="/js/jquery-3.7.1.min.js"></script>
<script>
$(document).ready(function(){
	var token = $("meta[name='csrf-token']").attr("content");
    var header = $("meta[name='csrf-header']").attr("content");

	// 비밀번호 질문 가져오기 실행
	$("#btn").click(getPasswordHint);

	// 비밀번호 힌트 답 제출 유효성 검사 실행
	$("#btn_pwdQNA").click(validateAndSubmitPwdAns);
	
	// 비밀번호 필드값 유효성 검사 살행
	$("#pwd").change(validatePwd);

	// 비밀번호 변경 제출 최종 유효성 검사 실행
	$("#changePwdForm").on("submit", changePwdVailid);
	
	
	// 비밀번호 질문 가져오기
	function getPasswordHint() {
		var userId = $("#id").val();
		$.ajax({
			url: '/user/getPwdHint',
			type: 'post',
			data: {userId: userId},
			dataType: 'json',
			beforeSend: function(xhr){
				xhr.setRequestHeader(header, token);
			},
			success: function(response){
				if(response.passwordHint != null){
					$("#findById_Box").hide();
					$("#passwordHint").val(response.passwordHint);
					$("#pwdQNA_Box").show();
				} else if (response.blankField != null) {
					$("#pwdQNA_Box").hide();
					$("#idNullMsg").show();
				} else {
					$("#pwdQNA_Box").hide();
					$("#notFound").show();
				}
			},
			error: function(error){
				alert("처리중 오류가 발생했습니다.");
	        }
		});
	}

	// 비밀번호 답 제출 최종 유효성 검사
	function validateAndSubmitPwdAns(){
		var userId = $("#id").val();
		var passwordHint = $("#passwordHint").val();
		var passwordCnsr = $("#passwordCnsr").val();
		$.ajax({
			url: '/user/getPwdCnsr',
			type: 'post',
			data: {
				userId: userId
				, passwordHint: passwordHint
				, passwordCnsr: passwordCnsr
			},
			beforeSend: function(xhr){
				xhr.setRequestHeader(header, token);
			},
			success: function(response){
				if(response != null && response.check == "Y"){
					console.log(userId);
					$("#hiddenUserId").val(userId);
					$("#pwdQNA_Box").hide();
					$("#changePwd_Box").show();
				} else {
					$("#incorrectPwdCnsr").show();
				}
			},
			error: function(error){
				alert("처리중 오류가 발생했습니다.");
	        }
		});
	}
	
	// 비밀번호 유효성 검사
	function validatePwd(callback) {
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
				} else if (response.pwdStatus == "null") { // 8자이상 사용 가능
					$("#pwdNullMsg").show();
				} else if (response.pwdStatus == "valid") { // 비밀번호를 입력
					$("#pwdValidMsg").show();
				} 
				if(typeof callback === 'function') { // callback이 함수인지 확인
					callback(response.pwdStatus); // callback 함수 호출
				}
			},
			error: function(error){
				alert("처리중 오류가 발생하였습니다.")
			}
		});
	}
	
	// 비밀번호 변경 제출 최종 유효성 검사
	function changePwdVailid(event){
		event.preventDefault();  // 폼 제출 방지
		// 유효성 검사
		validatePwd(function(pwdStatus) {// validatePwd 호출 시 callback 함수 전달
			if(pwdStatus == "green") {
				if(confirm("비밀번호 변경을 진행하시겠습니까?")) {
					$("#allowPwdChange").val("Y");
					$("#changePwdForm").off('submit').submit(); // 폼 제출
				}
			}
		});
	}
	
	
});

// 비밀번호 보이기/숨기기
$("#pwdToggleBtn").click(function(){
	var x = $("#pwd");
	if (x.attr("type") === "password") {
	x.attr("type", "text");
	} else {
	x.attr("type", "password");
	}
});
</script>
</body>
</html>