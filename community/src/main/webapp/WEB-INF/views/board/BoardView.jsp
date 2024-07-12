<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" 	uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" 		uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" 		uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt"   	uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec"		uri="http://www.springframework.org/security/tags" %>

<!-- 헤더 -->
<c:import url="/header" charEncoding="utf-8">
		<c:param name="title" value="커뮤니티"/>
</c:import>

<%-- 기본 URL --%>
<c:url var="_BASE_PARAM" value="">
	<c:param name="pageIndex" value="${searchVO.pageIndex}"/>
	<c:param name="pageUnit" value="${searchVO.pageUnit}"/>
	<c:if test="${not empty searchVO.searchCondition}"><c:param name="searchCondition" value="${searchVO.searchCondition}"/></c:if>
	<c:if test="${not empty searchVO.searchKeyword}"><c:param name="searchKeyword" value="${searchVO.searchKeyword}"/></c:if>
</c:url>

<div>
	<div> 
		<span><c:out value="${result.category}"/></span>
		<span><c:out value="${result.boardSj}"/></span>
	</div>
	<div>
		<span>작성자 : <c:out value="${result.nickname}"/></span> <br>
		<span>작성일 : <fmt:formatDate value="${result.frstRegistPnttm}" pattern="yyyy.MM.dd HH:mm:ss" /></span> <br>
		<c:if test="${not empty result.lastUpdtPnttm}">
			<span>수정일 : <fmt:formatDate value="${result.lastUpdtPnttm}" pattern="yyyy.MM.dd HH:mm:ss" /></span> <br>
		</c:if>
		<span>조회수 : <c:out value="${result.inqireCo}"/></span> <br>
		<span>추천수 : <c:out value="${result.recommendCnt}"/></span> <br>
	</div>
	<div>
		<span><c:out value="${result.boardCn}" escapeXml="false"/></span>
	</div>
</div>
<div>
	<c:if test="${not empty searchVO.boardId}">
		<c:choose>
			<%-- 글 작성자 확인 --%>
			<c:when test="${userId == result.registerId}">
				<c:url var="udtUrl" value="/board/post${_BASE_PARAM}">
					<c:param name="boardId" value="${searchVO.boardId}"/>
				</c:url>
				<a href="${udtUrl}">수정</a>
				<c:url var="delUrl" value="/board/delete${_BASE_PARAM}">
					<c:param name="boardId" value="${searchVO.boardId}"/>
				</c:url>
				<a href="${delUrl}" id="btn-del" class="btn"><i class="ico-del"></i> 삭제</a>
			</c:when>
			<c:otherwise>
            <%-- 관리자 또는 매니저에게 수정 및 삭제 링크 표시 --%>
	            <sec:authorize access="hasRole('ROLE_ADMIN')">
	                <c:url var="udtUrl" value="/board/post${_BASE_PARAM}">
	                    <c:param name="boardId" value="${searchVO.boardId}"/>
	                </c:url>
	                <a href="${udtUrl}">수정</a>
	                <c:url var="delUrl" value="/board/delete${_BASE_PARAM}">
	                    <c:param name="boardId" value="${searchVO.boardId}"/>
	                </c:url>
	            </sec:authorize>
	            <sec:authorize access="hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')">
	                <a href="${delUrl}" id="btn-del" class="btn"><i class="ico-del"></i> 삭제</a>
	            </sec:authorize>
	       </c:otherwise>
		</c:choose>
	</c:if>
	<c:url var="listUrl" value="/board/list${_BASE_PARAM}"/>
	<a href="${listUrl}" class="btn">목록</a>
</div>


<script>
	$(document).ready(function() {
		// 게시글 삭제
		$('#btn-del').click(function() {
			if (!confirm("삭제하시겠습니까?")) {
				return false;
			}
		});
	});
</script>

<!-- 푸터 -->
<c:import url="/footer" charEncoding="utf-8"/>