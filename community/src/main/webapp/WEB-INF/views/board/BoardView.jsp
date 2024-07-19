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

<input type="hidden" name="boardId" id="boardId" value="<c:out value="${result.boardId}"/>">

<div>
	<div> 
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
		<span><c:out value="${categoryName}"/></span>
		<span><c:out value="${result.boardSj}"/></span>
	</div>
	<div>
		작성자 : <span><c:out value="${result.nickname}"/></span> <br>
		작성일 : <span><fmt:formatDate value="${result.frstRegistPnttm}" pattern="yyyy.MM.dd HH:mm:ss" /></span> <br>
		<c:if test="${not empty result.lastUpdtPnttm}">
			수정일 : <span><fmt:formatDate value="${result.lastUpdtPnttm}" pattern="yyyy.MM.dd HH:mm:ss" /></span> <br>
		</c:if>
		조회수 : <span><c:out value="${result.inqireCo}"/></span> <br>
		추천수 : <span class="recView"><c:out value="${result.recommendCnt}"/></span> <br>
	</div>
	<div>
		<span><c:out value="${result.boardCn}" escapeXml="false"/></span>
	</div>
	<div>
		<button type="button" id="btn-rec">추천</button>
		<span id="rec-count" class="recView"><c:out value="${result.recommendCnt}"/></span>
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
					<c:param name="registerId" value="${result.registerId}" />
				</c:url>
				<a href="${delUrl}" id="btn-del" class="btn"><i class="ico-del"></i> 삭제</a>
			</c:when>
            <%-- 관리자 또는 매니저에게 수정 및 삭제 링크 표시 --%>
			<c:otherwise>
	            <sec:authorize access="hasRole('ROLE_ADMIN')">
	                <c:url var="udtUrl" value="/board/post${_BASE_PARAM}">
	                    <c:param name="boardId" value="${searchVO.boardId}"/>
	                </c:url>
	                <a href="${udtUrl}">수정</a>
	            </sec:authorize>
	            <sec:authorize access="hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')">
	                <c:url var="delUrl" value="/board/delete${_BASE_PARAM}">
	                    <c:param name="boardId" value="${searchVO.boardId}"/>
	                    <c:param name="registerId" value="${result.registerId}" />
	                </c:url>
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
		
		var token = $("meta[name='csrf-token']").attr("content");
	    var header = $("meta[name='csrf-header']").attr("content");
		
		// 게시글 추천
		$('#btn-rec').click(async function() {
			if (!confirm("추천하시겠습니까?")) {
				return false;
			}
			var boardId = $("#boardId").val();
			var recCnt;
			var currentRecCount = parseInt($('#rec-count').text());
			console.log(currentRecCount);
			
			try {
				response = await $.ajax({
					url: '/board/recommend',
					type: 'post',
					data: {
						boardId: boardId
						, recommendCnt: currentRecCount
					},
					beforeSend: function(xhr){
						xhr.setRequestHeader(header, token);
					}
				});
				console.log(recCnt);
				if (response.recCnt != null) {
					$(".recView").text(response.recCnt); // 추천수 갱신
				} else {
					alert(response.message);
				}
			} catch (error) {
				console.log(error);
				alert("처리중 오류가 발생하였습니다.");
			}
		});
		
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