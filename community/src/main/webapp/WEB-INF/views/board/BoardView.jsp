<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" 	uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" 		uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" 		uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt"   	uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec"		uri="http://www.springframework.org/security/tags" %>

<!-- 헤더 -->
<c:import url="/header" charEncoding="utf-8">
		<c:param name="title" value="${result.boardSj} - 커뮤니티"/>
</c:import>

<sec:authorize access="hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')" var="mngRole"/>
<sec:authorize access="hasRole('ROLE_ADMIN')" var="adminRole"/>
<sec:authorize access="isAuthenticated()" var="auth">
	<sec:authentication property="principal" var="principal"/>
</sec:authorize>


<%-- 기본 URL --%>
<c:url var="_BASE_PARAM" value="">
	<c:param name="pageIndex" value="${searchVO.pageIndex}"/>
	<c:param name="pageUnit" value="${searchVO.pageUnit}"/>
	<c:if test="${not empty searchVO.category}"><c:param name="category" value="${searchVO.category}"/></c:if>
	<c:if test="${not empty searchVO.searchCondition}"><c:param name="searchCondition" value="${searchVO.searchCondition}"/></c:if>
	<c:if test="${not empty searchVO.searchKeyword}"><c:param name="searchKeyword" value="${searchVO.searchKeyword}"/></c:if>
</c:url>
<input type="hidden" id="boardIdNum" value="<c:out value="${result.boardIdNum}"/>">
<%-- 게시글 내용 영역 --%>
<div>
	<div style="border: 0px;">
		<c:choose>
			<c:when test="${result.category == 1}">
				<c:set var="categoryName" value="[일반]"/>
			</c:when>
			<c:when test="${result.category == 2}">
				<c:set var="categoryName" value="[정보]"/>
			</c:when>
			<c:when test="${result.category == 3}">
				<c:set var="categoryName" value="[질문]"/>
			</c:when>
			<c:when test="${result.category == 4}">
				<c:set var="categoryName" value="[건의/신고]"/>
			</c:when>
		</c:choose>
		<b>
		<span><c:out value="${categoryName}"/></span>
		<span><c:out value="${result.boardSj}"/></span>
		</b>
	</div>
	<div style="border: 0px;">
		작성자 : <span><c:out value="${result.nickname}"/></span> <br>
		작성일 : <span><fmt:formatDate value="${result.frstRegistPnttm}" pattern="yyyy.MM.dd HH:mm:ss" /></span>
		<c:if test="${not empty result.lastUpdtPnttm}">
			(<span><fmt:formatDate value="${result.lastUpdtPnttm}" pattern="yyyy.MM.dd HH:mm:ss" /></span> 수정됨)
		</c:if>
		<br>
		조회수 : <span><c:out value="${result.inqireCo}"/></span> <br>
		추천수 : <span class="recView"><c:out value="${result.recommendCnt}"/></span> <br>
	</div>
	<hr>
	<div style="border: 0px;">
		<span class="boardCn"><c:out value="${result.boardCn}" escapeXml="false"/></span>
	</div>
	<div style="border: 0px;">
		<button type="button" id="btn-rec">추천</button>
		<span id="rec-count" class="recView"><c:out value="${result.recommendCnt}"/></span>
	</div>
</div>
<%-- 게시글 액션 영역 --%>
<div style="border: 0; display: flex; justify-content: space-between;">
	<c:if test="${not empty searchVO.boardIdNum}">
		<div style="border: 0; margin: 0;">
			<c:url var="udtUrl" value="/board/${searchVO.boardIdNum}/edit${_BASE_PARAM}"/>
			<c:url var="delUrl" value="/board/${searchVO.boardIdNum}/delete${_BASE_PARAM}"/>
			<form id="delForm" action="${delUrl}" method="post" style="display: none;">
				<input type="hidden" name="registerId" value="${result.registerId}" />
				<sec:csrfInput/>
			</form>
			<c:url var="listUrl" value="/board${_BASE_PARAM}"/>
			<a href="${listUrl}" class="btn">목록</a>
			<c:if test="${userId == result.registerId || adminRole}">
				<a href="${udtUrl}">수정</a>
			</c:if>
			<c:if test="${userId == result.registerId || mngRole}">
				<a id="btn-del" class="btn" href="#">삭제</a>
			</c:if>
		</div>
		<c:if test="${mngRole}">
			<div style="border: 1px solid #ccc; border-radius: 5px; text-align: center; margin: 0;">
				<c:url var="udtSttusUrl" value="/board/${searchVO.boardIdNum}/update-status"/>
				<form action="${udtSttusUrl}" method="post">
					<label for="category">카테고리 변경</label>
					<select id="category" name="category">
						<option value="1" ${result.category eq '1' ? 'selected' : ''}>일반</option>
						<option value="2" ${result.category eq '2' ? 'selected' : ''}>정보</option>
						<option value="3" ${result.category eq '3' ? 'selected' : ''}>질문</option>
						<option value="4" ${result.category eq '4' ? 'selected' : ''}>건의&신고</option>
					</select>
					<label for="isNotice">공지 여부 변경</label>
					<select name="isNotice" id="isNotice">
						<option value="Y" ${result.isNotice eq 'Y' ? 'selected' : ''}>예</option>
						<option value="N" ${result.isNotice eq 'N' ? 'selected' : ''}>아니오</option>
					</select>
					<label for="othbcAt" id="othbcAtLabel">비공개 여부 변경</label>
					<select name="othbcAt" id="othbcAt">
						<option value="Y" ${result.othbcAt eq 'Y' ? 'selected' : ''}>예</option>
						<option value="N" ${result.othbcAt eq 'N' ? 'selected' : ''}>아니오</option>
					</select>
					<input type="submit" value="적용">
					<sec:csrfInput/>
				</form>
			</div>
		</c:if>
	</c:if>
</div>
<%-- 덧글 영역 --%>
<c:import url="/WEB-INF/views/board/Reply.jsp" charEncoding="utf-8"/>

<script>
	$(document).ready(function() {
		console.log($('input:checkbox[id="chk1"]').val());
		var token = $("meta[name='_csrf']").attr("content");
		var header = $("meta[name='_csrf_header']").attr("content");
		var boardIdNum = $("#boardIdNum").val();
		
		// 게시글 추천
		$('#btn-rec').click(async function() {
			if (!confirm("추천하시겠습니까?")) {
				return false;
			}
			var recCnt;
			var currentRecCount = parseInt($('#rec-count').text());
			
			try {
				response = await $.ajax({
					url: '/board/' + boardIdNum + '/recommend',
					type: 'post',
					data: {
						recommendCnt: currentRecCount
					},
					beforeSend: function(xhr){
						xhr.setRequestHeader(header, token);
						xhr.setRequestHeader("Accept", "application/json");
					}
				});
// 				console.log(recCnt);
				if (response.recCnt != null) {
					$(".recView").text(response.recCnt); // 추천수 갱신
				} else {
					alert(response.message);
				}
			} catch (error) {
// 				console.log(error);
				if (error.responseJSON && error.responseJSON.message) {
			        alert(error.responseJSON.message);
			    } else {
					alert("처리중 오류가 발생하였습니다.");
				}
			}
		});
		

		// 게시글 삭제
		$('#btn-del').click(function() {
			if (!confirm("삭제하시겠습니까?")) {
				return false;
			}
			$("#delForm").submit();
		});
		
		$('#isNotice').change(function() {
			if ($('#isNotice').val() == 'Y') {
				$('#othbcAt').prop('disabled', true);
				$('#othbcAt').val('N');
			} else {
				$('#othbcAt').prop('disabled', false);
			}
		});
	});

<c:if test="${not empty sessionScope.message}">
	alert("<c:out value='${sessionScope.message}'/>");
	<c:remove var="message" scope="session"/>
</c:if>
	
</script>

<!-- 푸터 -->
<c:import url="/footer" charEncoding="utf-8"/>