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
<div style="border: 0; text-align: left;">
	<c:if test="${not empty searchVO.boardIdNum}">
		<c:url var="udtUrl" value="/board/${searchVO.boardIdNum}/edit${_BASE_PARAM}"/>
		<c:url var="delUrl" value="/board/${searchVO.boardIdNum}/delete${_BASE_PARAM}"/>
		<form id="delForm" action="${delUrl}" method="post" style="display: none;">
			<input type="hidden" name="registerId" value="${result.registerId}" />
			<sec:csrfInput/>
		</form>
		<c:choose>
			<%-- 글 작성자 확인 --%>
			<c:when test="${userId == result.registerId}">
				<a href="${udtUrl}">수정</a>
				<a id="btn-del" class="btn" href="#">삭제</a>
			</c:when>
            <%-- 관리자 또는 매니저에게 수정 및 삭제 링크 표시 --%>
			<c:otherwise>
	            <sec:authorize access="hasRole('ROLE_ADMIN')">
	                <a href="${udtUrl}">수정</a>
	            </sec:authorize>
	            <sec:authorize access="hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')">
					<a id="btn-del" class="btn" href="#">삭제</a>
	            </sec:authorize>
	       </c:otherwise>
		</c:choose>
	</c:if>
	<c:url var="listUrl" value="/board${_BASE_PARAM}"/>
	<a href="${listUrl}" class="btn">목록</a>
</div>
<%-- 덧글 영역 --%>
<c:import url="/WEB-INF/views/board/Reply.jsp" charEncoding="utf-8"/>

<script>
	$(document).ready(function() {
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
		
	});

<c:if test="${not empty sessionScope.message}">
	alert("<c:out value='${sessionScope.message}'/>");
	<c:remove var="message" scope="session"/>
</c:if>
	
</script>

<!-- 푸터 -->
<c:import url="/footer" charEncoding="utf-8"/>