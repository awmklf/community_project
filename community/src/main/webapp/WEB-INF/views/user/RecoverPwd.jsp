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
<title>비밀번호 변경 - 커뮤니티</title>
<style>
	div {
			margin: 20px;
			padding: 10px;
			border: 1px solid #ccc;
			background-color: #f9f9f9;
		}
</style>
</head>
<body>
<c:url var="url" value="/user"/>

<div>
	<a href="/">HOME</a>
</div>
<div>
	비밀번호 찾기
</div>
<!-- 아이디 조회 영역 -->
<div id="findById_Box">
	<p>비밀번호를 찾기 위해 아이디를 입력해 주세요.</p>
	<label for="id">아이디 : </label>
	<input type="text" name="userId" id="id">
	<span id="idNullMsg" style="display: none;">아이디를 입력해주세요.</span>
	<span id="notFound" style="display: none;">유효하지 않은 아이디입니다.</span> <br>
	<button type="button" id="btn">아이디 확인 </button>
</div>

<!-- 비밀번호 답변 영역 -->
<div id="pwdQnA_Box" style="display: none;">
	<p>비밀번호 힌트에 대한 답변을 입력해 주세요.</p>
	<label for="passwordHint">비밀번호 힌트 : </label>
	<input type="text" name="passwordHint" id="passwordHint" value="" readonly="readonly"> <br>
	<label for="passwordCnsr">비밀번호 답변 : </label>
	<input type="text" name="passwordCnsr" id="passwordCnsr">
	<span id="incorrectPwdCnsr" style="display: none;">답변이 올바르지 않습니다.</span>
	<span id="nullPwdCnsr" style="display: none;">답변을 입력해 주세요.</span> <br>
	<button type="buton" id="btn_pwdQnA">확인</button>
</div>


<!-- 비밀번호 변경 영역 -->
<div id="changePwd_Box" style="display: none;">
	<p>변경할 비밀번호를 입력해 주세요.</p>
	<form action="${url}/changePwd" method="post" id="changePwdForm">
		<input type="hidden" name="userId" id="hiddenUserId" readonly="readonly">
		<input type="hidden" name="allowPwdChange" id="allowPwdChange" readonly="readonly">
		<label for="pwd">변경할 비밀번호 : </label>
		<input type="password" name="password" id="pwd" placeholder="비밀번호(8자리 이상)">
		<button type="button" id="pwdToggleBtn">show/hide</button>
		<span id="pwdNullMsg" class="msg" style="display: none;">비밀번호를 입력해 주세요.</span>
		<span id="pwdValidMsg" class="msg" style="display: none;">비밀번호는 8자리 이상이어야 하며, 연속된 동일 문자 4자 이상 사용 불가능합니다.</span>
		<br>
		<button type="submit">변경</button>
		<sec:csrfInput/>
	</form>
</div>


<script src="/js/jquery-3.7.1.min.js"></script>
<script>
$(document).ready(function(){
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
    
    // 아이디 필드 메세지 초기화
    $("#id").keydown(function() {
    	$("#notFound").hide();
    	$("#idNullMsg").hide();
    });
    
    // 비밀번호 답변 필드 메세지 초기화
    $("#passwordCnsr").keydown(function() {
        $("#incorrectPwdCnsr").hide();
        $("#nullPwdCnsr").hide();
    });
    
    // 비밀번호 필드 메세지 초기화
    $("#pwd").keydown(function() {
        $("#pwdNullMsg").hide();
        $("#pwdValidMsg").hide();
    });

	// 비밀번호 질문 가져오기 실행
	$("#btn").click(getPasswordHint);

	// 비밀번호 답 제출 유효성 검사 실행
	$("#btn_pwdQnA").click(validateAndSubmitPwdAns);
	
	// 변경할 비밀번호 제출 유효성 검사 실행
	$("#changePwdForm").on("submit", function(event) {
		event.preventDefault();  // 폼 제출 방지
		validatePwd().then(function(status) {
			if(status.pwdStatus == "green") { // 유효성 통과
				if(confirm("비밀번호 변경을 진행하시겠습니까?")) {
					$("#allowPwdChange").val("Y");
					$("#changePwdForm").off('submit').submit(); // 폼 제출
				} 
			} else if (status.pwdStatus == "null") {  // 비밀번호 미입력
				$("#pwdNullMsg").show();
			} else if (status.pwdStatus == "valid") { // 8자리 이상 조건 미충족 또는 같은 문자나 숫자를 연속적으로 사용
				$("#pwdValidMsg").show();
			}
		}).catch(function(error) {
			alert("비밀번호 변경 중 문제가 발생하였습니다.");
		});
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
				if (response.passwordHint == "blank") { // 아이디 미입력
					$("#idNullMsg").show();
				} else if(response.passwordHint == "null") { // 아이디 조회 결과 없음
					$("#notFound").show();
				} else if(response.passwordHint != null){ // 정상 조회
					$("#findById_Box").hide();
					$("#passwordHint").val(response.passwordHint);
					$("#pwdQnA_Box").show();
				}
			},
			error: function(error){
				alert("아이디 조회 중 오류가 발생했습니다.");
	        }
		});
	}

	// 비밀번호 답 유효성 검사
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
				if(response.check == "Y"){ // 유효성 통과
					$("#hiddenUserId").val(userId);
					$("#pwdQnA_Box").hide();
					$("#changePwd_Box").show();
				} else if(response.check == "N"){ // 답변 틀림
					$("#incorrectPwdCnsr").show();
				} else if (response.check == "null") { // 답변 미입력
					$("#nullPwdCnsr").show();
				}
			},
			error: function(error){
				alert("비밀번호 질문에 대한 답을 검사 중 오류가 발생했습니다.");
	        }
		});
	}
	
	// 변경할 비밀번호 유효성 검사
	async function validatePwd() {
		var password = $("#pwd").val();
		var pwdStatus = null;
		try {
			pwdStatus = await $.ajax({
				url: '/user/checkPwd',
				type: 'post',
				data: {password: password},
				beforeSend: function(xhr){
					xhr.setRequestHeader(header, token);
				}
			});
		} catch (error) {
			alert("처리중 오류가 발생하였습니다.")
		}
		return pwdStatus;
	}
});

</script>
</body>
</html>