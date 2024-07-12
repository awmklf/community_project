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
<style type="text/css">
	.msg {
		display: none;
	}
</style>
</head>
<body>
<c:url var="url" value="/user"/>
<div>
	<a href="/">HOME</a>
</div>
<div>
	<form action="${url}/changePwd" method="post" id="changePwdForm">
		<input type="hidden" name="userId" value="<c:out value="${userId}"/>">
		<input type="hidden" name="allowPwdChange" id="allowPwdChange">
		<label for="pwd">변경할 비밀번호 : </label>
		<input type="password" name="password" id="pwd" placeholder="비밀번호(8자리 이상)">
		<button type="button" id="pwdToggleBtn">show/hide</button>
		<span id="pwdGreenMsg" class="msg">사용 가능한 비밀번호입니다.</span>
		<span id="pwdNullMsg" class="msg">비밀번호를 입력해 주세요.</span>
		<span id="pwdValidMsg" class="msg">8자리 이상만 사용 가능합니다.</span>
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
		    
		 	// 비밀번호 필드값 변경 시 유효성 검사
		    $("#pwd").change(validatePwd);

			// 제출시 최종 유효성 검사
			$("#changePwdForm").on("submit", function(event){
				event.preventDefault();  // 폼 제출 방지
				// 유효성 검사
				validatePwd(function(pwdStatus) {// validatePwd 호출 시 callback 함수 전달
					console.log(pwdStatus);
					if(pwdStatus == "green") {
						if(confirm("비밀번호 변경을 진행하시겠습니까?")) {
							$("#allowPwdChange").val("Y");
							$("#changePwdForm").off('submit').submit(); // 폼 제출
						}
					} else {
						alert("비밀번호는 8자리 이상만 사용 가능합니다.");
					}
				});
			});

			 
			// 비밀번호 유효성 검사 로직
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
							alert("유효성 검증중 오류가 발생하였습니다.")
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