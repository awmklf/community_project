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
	<c:param name="pageUnit" value="${searchVO.recordCountPerPage}"/>
	<c:if test="${not empty searchVO.searchCondition}"><c:param name="searchCondition" value="${searchVO.searchCondition}"/></c:if>
	<c:if test="${not empty searchVO.searchKeyword}"><c:param name="searchKeyword" value="${searchVO.searchKeyword}"/></c:if>
</c:url>

<%-- 게시글 목록 수 영역 --%>
<div>
	<form id="pageSizeForm" action="/board/list" method="post">
		<input type="hidden" name="searchCondition" value="${searchVO.searchCondition}"/>
		<input type="hidden" name="searchKeyword" value="${searchVO.searchKeyword}"/>
		<select id="selectRecord" name="pageUnit">
			<option value="1" <c:if test="${searchVO.recordCountPerPage eq '1'}">selected="selected"</c:if> >1개</option>
			<option value="10" <c:if test="${searchVO.recordCountPerPage eq '10'}">selected="selected"</c:if> >10개</option>
			<option value="30" <c:if test="${searchVO.recordCountPerPage eq '30'}">selected="selected"</c:if> >30개</option>
			<option value="50" <c:if test="${searchVO.recordCountPerPage eq '50'}">selected="selected"</c:if> >50개</option>
		</select>
		<sec:csrfInput/>
	</form>
</div>

<!-- 게시글 영역 -->
<div id="postList">
	<table>
		<thead>
			<tr>
				<th class="" scope="col">번호</th>
				<th class="" scope="col">제목</th>
				<th class="" scope="col">작성자</th>
				<th class="" scope="col">작성일</th>
				<th class="" scope="col">조회수</th>
				<th class="" scope="col">추천수</th>
			</tr>
		</thead>
		<tbody>
		<!-- 공지영역 -->
		<c:forEach var="result" items="${noticeResultList}" varStatus="status">
			<tr class="notice">
				<td class="num"><span class="label-bbs spot">공지</span></td>
				<td class="tit">
					<c:url var="viewUrl" value="/board/view${_BASE_PARAM}">
						<c:param name="boardId" value="${result.boardId}"/>
						<c:param name="pageIndex" value="${searchVO.pageIndex}"/>
					</c:url>
					<a href="${viewUrl}"><c:out value="${result.boardSj}"/></a>
				</td>
				<td class="writer" data-cell-header="작성자 : "><c:out value="${result.nickname}"/></td>
				<td class="date" data-cell-header="작성일 : ">
					<fmt:formatDate var="now" value="${currentDate}" pattern="yyyy-MM-dd" type="date" />
					<fmt:formatDate var="frstRegistPnttm" value="${result.frstRegistPnttm}" pattern="yyyy-MM-dd" type="date" />
					<c:choose>
					    <c:when test="${frstRegistPnttm.equals(now)}">
					        <fmt:formatDate value="${result.frstRegistPnttm}" pattern="HH:mm:ss" type="time" />
					    </c:when>
					    <c:otherwise>
					        <fmt:formatDate value="${result.frstRegistPnttm}" pattern="yyyy.MM.dd" type="date" />
					    </c:otherwise>
					</c:choose>
				</td>
				<td class="hits" data-cell-header="조회수 : "><c:out value="${result.inqireCo}"/></td>
				<td data-cell-header="추천수 : ">
					<c:out value="${result.recommendCnt}"/>
				</td>
			</tr>
		</c:forEach>
		
		<!-- 일반글 영역 -->
		<c:forEach var="result" items="${resultList}" varStatus="status">
			<tr>
				<td class="num">
					<c:out value="${paginationInfo.totalRecordCount - ((searchVO.pageIndex-1) * searchVO.pageUnit) - (status.count - 1)}"/>
				</td>
				<td>
					<c:url var="viewUrl" value="/board/view${_BASE_PARAM}">
						<c:param name="boardId" value="${result.boardId}"/>
						<c:param name="pageIndex" value="${searchVO.pageIndex}"/>
					</c:url>
					<a href="${viewUrl}"><c:out value="${result.boardSj}"/></a>
				</td>
				<td>
					<c:out value="${result.nickname}"/>
				</td>
				<td>
					<fmt:formatDate var="now" value="${currentDate}" pattern="yyyy-MM-dd" type="date" />
					<fmt:formatDate var="frstRegistPnttm" value="${result.frstRegistPnttm}" pattern="yyyy-MM-dd" type="date" />
					<c:choose>
					    <c:when test="${frstRegistPnttm.equals(now)}">
					        <fmt:formatDate value="${result.frstRegistPnttm}" pattern="HH:mm:ss" type="time" />
					    </c:when>
					    <c:otherwise>
					        <fmt:formatDate value="${result.frstRegistPnttm}" pattern="yyyy.MM.dd" type="date" />
					    </c:otherwise>
					</c:choose>
				</td>
				<td>
					<c:out value="${result.inqireCo}"/>
				</td>
				<td>
					<c:out value="${result.recommendCnt}"/>
				</td>
			</tr>
		</c:forEach>
		<%-- 게시된 글이 없을 경우 --%>
		<c:if test="${fn:length(resultList) == 0}">
			<tr class="empty"><td colspan="6">검색 데이터가 없습니다.</td></tr>
		</c:if>
		</tbody>
	</table>
</div>

<div>
	<%-- 페이징 영역 --%>
	<div id="paging">
		<%-- 이전 --%>
		<c:if test="${paginationInfo.firstPageNoOnPageList != 1}">
			<c:url var="firstPageUrl" value="/board/list${_BASE_PARAM}">
				<c:param name="pageIndex" value="1"/>
			</c:url>
			<a href="${firstPageUrl}">처음</a>
		</c:if>
		<c:if test="${paginationInfo.currentPageNo !=1}">
			<c:url var="prevPageUrl" value="/board/list${_BASE_PARAM}">
				<c:choose>
					<c:when test="${paginationInfo.firstPageNoOnPageList == 1}">
						<c:param name="pageIndex" value="1"/>
					</c:when>
					<c:otherwise>
					<c:param name="pageIndex" value="${paginationInfo.firstPageNoOnPageList-1}"/>
					</c:otherwise>
				</c:choose>
			</c:url>
			<a href="${prevPageUrl}">이전</a>
		</c:if>
	
		<%-- 번호 --%>
		<c:forEach var="pageNum" begin="${paginationInfo.firstPageNoOnPageList}" end="${paginationInfo.lastPageNoOnPageList}">
			<c:choose>
				<c:when test="${pageNum == paginationInfo.currentPageNo}">
					<span>${pageNum}</span>
				</c:when>
				<c:otherwise>
					<c:url var="pageUrl" value="/board/list${_BASE_PARAM}">
						<c:param name="pageIndex" value="${pageNum}"/>
					</c:url>
					<a href="${pageUrl}">${pageNum}</a>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	   
		<%-- 다음 --%>
		<c:if test="${paginationInfo.currentPageNo != paginationInfo.totalPageCount}"> 
			<c:choose>
				 <c:when test="${paginationInfo.lastPageNoOnPageList == paginationInfo.totalPageCount}">
				 	<c:set var="addNum" value="0"/>
				 </c:when>
				 <c:otherwise>
				 	<c:set var="addNum" value="1"/>
				 </c:otherwise>
			</c:choose>
			<c:url var="nextPageUrl" value="/board/list${_BASE_PARAM}">
				<c:param name="pageIndex" value="${paginationInfo.lastPageNoOnPageList + addNum}"/>
			</c:url>
			<a href="${nextPageUrl}">다음</a>
		</c:if>
		<c:if test="${paginationInfo.lastPageNoOnPageList != paginationInfo.totalPageCount}">
			<c:url var="lastPageUrl" value="/board/list${_BASE_PARAM}">
				<c:param name="pageIndex" value="${paginationInfo.totalPageCount}"/>
			</c:url>
			<a href="${lastPageUrl}">마지막</a>
		</c:if>
	</div>
	
	
	<div>
		<c:url var="postUrl" value="/board/post${_BASE_PARAM}"/>
		<a href="${postUrl}">글쓰기</a>
	</div>
</div>



<%-- 검색 영역 --%>
<form action="/board/list" method="post">
	<input type="hidden" name="pageUnit" value="${searchVO.recordCountPerPage}"/>
	<fieldset>
		<legend>검색조건입력폼</legend>
		<label for="ftext" class="">검색분류선택</label>
		<select name="searchCondition" id="ftext">
			<option value="0" <c:if test="${searchVO.searchCondition eq '0'}">selected="selected"</c:if> >제목</option> 
			<option value="1" <c:if test="${searchVO.searchCondition eq '1'}">selected="selected"</c:if> >내용</option> 
			<option value="2" <c:if test="${searchVO.searchCondition eq '2'}">selected="selected"</c:if> >작성자</option> 
		</select>
		<label for="" class="">검색어입력</label>
		<input name="searchKeyword" value="<c:out value="${searchVO.searchKeyword}"/>" type="text" class="" id="">
		<span class=""><input type="submit" value="검색" title=""></span>
	</fieldset>
	<sec:csrfInput/>
</form>

<script>
$(document).ready(function(){
	// 게시글 개수 표시 선택
	$("#selectRecord").change(function() {
	    $('#pageSizeForm').submit();
	});
});
</script>

<script>
<c:if test="${not empty sessionScope.message}">
	alert("<c:out value='${sessionScope.message}'/>");
	<c:remove var="message" scope="session"/>
</c:if>
</script>


<!-- 푸터 -->
<c:import url="/footer" charEncoding="utf-8"/>