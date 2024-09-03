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
<title>비밀번호 변경 - 커뮤니티</title>
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
	
	form {
		height: 250px;
	}
	
	div {
		margin: 0;
		padding: 0;
		border: none;
		background-color: #f9f9f9;
	}
	.frm {
		height: 250px;
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
	
	.btn {
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
		<h2>비밀번호 찾기</h2>
		
		<!-- 아이디 조회 영역 -->
		<div id="findById_Box" class="frm">
			<p>비밀번호를 찾기 위해 아이디를 입력해 주세요.</p>
			<div style="display: flex; align-items: center;">
				<div style="border: none;">
					<label for="id">아이디 : </label>
				</div>
				<div style="border: none;">
					<input type="text" name="userId" id="id">
				</div>
			</div>
			<br><span id="idMsg"></span>
			<br><button type="button" id="btn" class="btn" style="margin: 10px auto">아이디 확인 </button>
		</div>
		
		<!-- 비밀번호 답변 영역 -->
		<div id="pwdQnA_Box" class="frm" style="display: none;">
			<p>비밀번호 힌트에 대한 답변을 입력해 주세요.</p>
			<div style="display: flex; align-items: center;">
				<div style="border: none;">
					<label for="passwordHint">비밀번호 힌트 : </label>
				</div>
				<div style="border: none;">
					<input type="text" name="passwordHint" id="passwordHint" value="" readonly="readonly"> <br>
				</div>
			</div>
			<div style="display: flex; align-items: center;">
				<div style="border: none;">
					<label for="passwordCnsr">비밀번호 답 : </label>
				</div>
				<div style="border: none;">
					<input type="text" name="passwordCnsr" id="passwordCnsr">
				</div>
			</div>
			<br><span id="pwdCnsrMsg"></span>
			<br><button type="buton" id="btn_pwdQnA" class="btn" style="margin: 10px auto">확인</button>
		</div>
		
		
		<!-- 비밀번호 변경 영역 -->
		<div id="changePwd_Box" style="display: none;">
			<p>변경할 비밀번호를 입력해 주세요.</p>
			<form action="/user/changePwd" method="post" id="changePwdForm">
			<input type="hidden" name="userId" id="hiddenUserId" readonly="readonly">
			<input type="hidden" name="allowPwdChange" id="allowPwdChange" readonly="readonly">
			<div style="display: flex; align-items: center;">
				<div style="border: none;">
					<label for="pwd">변경할 비밀번호 : </label>
				</div>
				<div style="border: none; position: relative;">
					<input type="password" name="password" id="pwd" placeholder="비밀번호(8자 이상, 영문+숫자 포함)">
					<img src="/img/ico-hide.png" id="togglePwd" style="position: absolute; right: 5px; top: 50%; transform: translateY(-50%); cursor: pointer;">
				</div>
			</div>
				<br><span id="pwdMsg" class="msg"></span>
				<br><button type="submit" class="btn" style="margin: 10px auto">변경</button>
				<sec:csrfInput/>
			</form>
		</div>
	</div>
</div>

<script src="/js/jquery-3.7.1.min.js"></script>
<script>
$(document).ready(function(){
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");

	// 비밀번호 질문 가져오기 실행
	$("#btn").click(getPasswordHint);

	// 비밀번호 답 제출 유효성 검사 실행
	$("#btn_pwdQnA").click(validateAndSubmitPwdAns);
	
	
	// 아이디 필드 메세지 초기화
    $("#id").keydown(function() {
    	$('#idMsg').html("");
    });
	// 아이디 검증, 비밀번호 질문 가져오기
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
					$('#idMsg').css("color", "red").html("아이디를 입력해주세요.");
				} else if(response.passwordHint == "null") { // 아이디 조회 결과 없음
					$('#idMsg').css("color", "red").html("유효하지 않은 아이디입니다.");
				} else if(response.passwordHint != null){ // 정상 조회
					$("#passwordHint").val(response.passwordHint);
					$("#findById_Box").hide();
					$("#pwdQnA_Box").show();
				}
			},
			error: function(error){
				alert("아이디 조회 중 오류가 발생했습니다.");
	        }
		});
	}
	
	
	// 비밀번호 답변 필드 메세지 초기화
    $("#passwordCnsr").keydown(function() {
    	$('#pwdCnsrMsg').html("");
    });
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
					$('#pwdCnsrMsg').css("color", "red").html("답이 올바르지 않습니다.");
				} else if (response.check == "null") { // 답변 미입력
					$('#pwdCnsrMsg').css("color", "red").html("답을 입력해 주세요.");
				}
			},
			error: function(error){
				alert("비밀번호 질문에 대한 답변을 검사 중 오류가 발생했습니다.");
	        }
		});
	}
	
	
	// 비밀번호 필드 메세지 초기화
    $("#pwd").keydown(function() {
    	$('#pwdMsg').html("");
    });
	// 변경할 비밀번호 제출 유효성 검사 실행
	$("#changePwdForm").on("submit", async function(event) {
		event.preventDefault();  // 폼 제출 방지
		var password = $("#pwd").val();
		var status = null;
		try {
			status = await $.ajax({
				url: '/user/checkPwd',
				type: 'post',
				data: {password: password},
				beforeSend: function(xhr){
					xhr.setRequestHeader(header, token);
				}
			});
			if(status.pwdStatus == "green") { // 유효성 통과
				if(confirm("비밀번호 변경을 진행하시겠습니까?")) {
					$("#allowPwdChange").val("Y");
					$("#changePwdForm").off('submit').submit(); // 폼 제출
				} 
			} else if (status.pwdStatus == "null") {  // 비밀번호 미입력
				$('#pwdMsg').css("color", "red").html("비밀번호를 입력해 주세요.");
				$("#pwdNullMsg").show();
			} else if (status.pwdStatus == "valid") { // 8자 이상 영문과 숫자 혼합, 동일 문자 4개 미만 미충족
				$('#pwdMsg').css("color", "red").html("8자 이상, 영문+숫자 포함, 동일 문자 4개 미만 미충족");
			}
		} catch (error) {
			console.error(error);
			alert("비밀번호 변경 중 문제가 발생하였습니다.");
		}
	});
	
	
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
});

</script>
</body>
</html>