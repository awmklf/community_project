<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" 	uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" 		uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" 		uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt"   	uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec"		uri="http://www.springframework.org/security/tags" %>

<!-- 헤더 -->
<c:import url="/header" charEncoding="utf-8">
		<c:param name="title" value="글쓰기 - 커뮤니티"/>
</c:import>

<%-- 기본 URL --%>
<c:url var="_BASE_PARAM" value="">
	<c:param name="pageIndex" value="${searchVO.pageIndex}"/>
	<c:param name="pageUnit" value="${searchVO.pageUnit}"/>
	<c:if test="${not empty searchVO.searchCondition}"><c:param name="searchCondition" value="${searchVO.searchCondition}"/></c:if>
	<c:if test="${not empty searchVO.searchKeyword}"><c:param name="searchKeyword" value="${searchVO.searchKeyword}"/></c:if>
</c:url>

<style>
	#boardSj {
		width: 300px;
		height: 30px;
		pause: 0px;
	}
</style>

<%-- 등록, 수정 url --%>
<c:choose>
	<c:when test="${not empty searchVO.boardIdNum}">
		<c:set var="actionUrl" value="/board/${searchVO.boardIdNum}/update"/>
	</c:when>
	<c:otherwise>
		<c:set var="actionUrl" value="/board/insert"/>
	</c:otherwise>
</c:choose>

<%-- 등록, 수정 폼 영역 --%>
<div>
	<form action="${actionUrl}" method="post" id="form" >
		<input type="hidden" name="boardId" value="${result.boardId}">
		<input type="hidden" name="registerId" value="${result.registerId}">
		<input type="hidden" name="atchFileId" id="atchFileId" value="${result.atchFileId}">
		<div style="text-align: left;">
			<select id="" name="category" style="height: 34px;">
				<option value="1" ${result.category eq '1' ? 'selected' : ''}>일반</option>
				<option value="2" ${result.category eq '2' ? 'selected' : ''}>정보</option>
				<option value="3" ${result.category eq '3' ? 'selected' : ''}>질문</option>
				<option value="4" ${result.category eq '4' ? 'selected' : ''}>건의&신고</option>
			</select>
			<input type="text" name="boardSj" id="boardSj" title="제목" class="" placeholder="제목" value="<c:out value="${result.boardSj}"/>" maxlength="20">
		</div>
		<sec:authorize access="hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')">
			<div style="text-align: left;">
				공지 여부&nbsp;
				<label for="isNoticeY">예 : </label>
				<input type="radio" id="isNoticeY" name="isNotice" value="Y" ${result.isNotice eq 'Y' ? 'checked' : ''}>
				&nbsp;&nbsp;&nbsp;
				<label for="isNoticeN">아니오 : </label>
				<input type="radio" id="isNoticeN" name="isNotice" value="N" ${result.isNotice ne 'Y' ? 'checked' : ''}>
			</div>
		</sec:authorize>
		<div id="privateSection" style="display: none; text-align: left;">
			비공개 여부&nbsp;
			<label for="othbcAtY">예 : </label>
			<input type="radio" name="othbcAt" id="othbcAtY" value="Y" ${result.othbcAt eq 'Y' ? 'checked' : ''}>
			&nbsp;&nbsp;&nbsp;
			<label for="othbcAtN">아니오 : </label>
			<input type="radio" name="othbcAt" id="othbcAtN" value="N" ${result.othbcAt ne 'Y' ? 'checked' : ''}>
		</div>
		<div>
			<!-- 에디터 -->
			<script type="text/javascript" src="/resources/smarteditor2/js/HuskyEZCreator.js" charset="utf-8"></script>
			<textarea id="boardCn" name="boardCn" cols="120" rows="30" title="내용입력" style="display: none; width: 100%;"><c:out value="${result.boardCn}"/></textarea>
			<script id="smartEditor" type="text/javascript"> 
				var oEditors = [];
// 				var aAdditionalFontSet = [["나눔고딕", "나눔고딕"]];
				nhn.husky.EZCreator.createInIFrame({
				    oAppRef: oEditors
				    , elPlaceHolder: "boardCn" //textarea ID 입력
				    , sSkinURI: "/resources/smarteditor2/SmartEditor2Skin.html" //martEditor2Skin.html 경로 입력
				    , fCreator: "createSEditor2"
				    , htParams : { 
				        bUseToolbar : true // 툴바 사용 여부 (true:사용/ false:사용하지 않음) 
				        , bUseVerticalResizer : true // 입력창 크기 조절바 사용 여부 (true:사용/ false:사용하지 않음) 
				        , bUseModeChanger : true // 모드 탭(Editor | HTML | TEXT) 사용 여부 (true:사용/ false:사용하지 않음) 
// 						, aAdditionalFontList : aAdditionalFontSet	// 추가 글꼴 목록
				        , fOnBeforeUnload: function() {
							// 로드 전에 실행할 코드
						}
						, fOnAppLoad: function() {
							// 로드 후에 실행할 코드
						}
				    }
				});
			</script>
		</div>
		<div style="border: 0;">
			<c:set var="btnText" value="등록"/>
			<c:set var="cslUrl" value="/board${_BASE_PARAM}"/>
			<c:choose>
			    <c:when test="${userId == result.registerId}">
			        <c:set var="btnText" value="수정"/>
			        <c:set var="cslUrl" value="/board/${searchVO.boardIdNum}${_BASE_PARAM}"/>
			    </c:when>
			    <c:when test="${not empty result.registerId}">
			        <sec:authorize access="hasRole('ROLE_ADMIN')">
			            <c:set var="btnText" value="수정"/>
			            <c:set var="cslUrl" value="/board/${searchVO.boardIdNum}${_BASE_PARAM}"/>
			        </sec:authorize>
			    </c:when>
			</c:choose>
			<button type="button" id="btn-reg">${btnText}</button>
			<button type="button" id="btn-reg" onclick="location.href='${cslUrl}'">취소</button>
		</div>
		<sec:csrfInput/>
	</form>
</div>

<script type="text/javascript">
// 게시글 작성 임시 쿠키 생성
function setSessionCookie(name, value) {
    document.cookie = name + "=" + (value || "") + "; path=/";
}
function generateUniqueValue() {
    return fetch('/temp-id', {
        method: 'GET',
        headers: {'Accept':'application/json'}
    }).then(
        resp => resp.json()
    ).then(function (data) {
        return data.tempImageId;
    }).catch(function (err) {
        console.log(err);
        return null;
    });
}
// 게시글 작성의 경우 신규 쿠키생성, 수정의 경우 파일아이디값 사용
<c:choose>
	<c:when test="${not empty result.atchFileId}">
		var fileId = $('#atchFileId').val();
		setSessionCookie('tempUniqueVal', fileId);
	</c:when>
	<c:otherwise>
		generateUniqueValue().then(function(uniqueValue) {
		    if (uniqueValue) {
		        setSessionCookie('tempUniqueVal', uniqueValue);
		        console.log('Temporary Unique Value:', uniqueValue);
		    } else {
		        console.log('Failed to generate unique value.');
		    }
		});
	</c:otherwise>
</c:choose>

window.addEventListener('beforeunload', function() {
	// 페이지 나가기 방지
	event.preventDefault();
	event.returnValue = '';
	
	// 페이지를 벗어나면 쿠키 삭제
    document.cookie = 'tempUniqueVal=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
});
</script>


<script>
	var tempId;

	$(document).ready(function() {
		
		// 페이지 로드 시에 '비공개 여부' 섹션 표시 여부 체크
		updatePrivateSectionVisibility();
		
		// 비공개 여부 섹션 활성화
		$('select[name="category"]').change(updatePrivateSectionVisibility);
		
		
		// 게시글 등록 or 수정
		$("#btn-reg").click(function() {
			if (!regist()) {
				return false;
			}
			$("#btn-reg").prop("disabled", true);
			$("#form").submit();
 			return false;
		});
		
		// 취소
		$("#btn-cnl").click(function() {
			if (!confirm("취소하시겠습니까?")) {
				return false;
			}
		});
		
		// 제목칸 엔터 이벤트 방지
		$("#boardSj").keydown(function(event) {
		    if (event.key === "Enter") {
		        event.preventDefault();
		        return false;
		    }
		});
		
		// 미입력 방지
		function regist() {
			if (!$("#boardSj").val().trim()) {
				alert("제목을 입력해주세요.");
				$("#boardSj").focus();
				return false;
			}
			oEditors.getById["boardCn"].exec("UPDATE_CONTENTS_FIELD", []); // 스마트에디터 내용 옮기기
			// 정규 표현식을 사용하여 태그 및 공백 제거
			var boardCn = $("#boardCn").val().replace(/<\/?[^>]+(>|$)/g, "").replace(/&nbsp;/g, "").replace(/\u200B/g, "").trim();
			if (boardCn.length == 0) {
				alert("내용을 입력해주세요.");
				oEditors.getById["boardCn"].exec("FOCUS");
				return false;
			}
			return true;
		}
		
	});

	
	// '비공개 여부' 섹션 확인
	function updatePrivateSectionVisibility() {
		if ($('select[name="category"]').val() == '4') {
			$('#privateSection').show();
		} else {
			$('#privateSection').hide();
			$('#othbcAtN').prop('checked', true);
		}
	}
	
	// 로그인 세션 연장
	setInterval(function() {
	    $.ajax({
	        url: '/keep-alive',
	        success: function(data) {
	            console.log('Session has been refreshed');
	        }
	    });
	}, 3300000);// 55분마다 서버에 요청
</script>

<!-- 푸터 -->
<c:import url="/footer" charEncoding="utf-8"/>