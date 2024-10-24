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

<style>
	.active {
		font-weight: bold;
		border-bottom: 4em solid #111;
	}
	
	.post-title {
		position: relative;
	}
	
	.post-title .thumbnail {
		display: none;
		position: absolute;
		bottom: 100%;
		left: 0;
		background-color: #fff;
		/*             margin-bottom: 10px; /* 이미지와 제목 사이의 간격 */ */
		z-index: 1000; /* 이미지가 맨 앞에 표시되도록 설정 */
	}
	
	.post-title:hover .thumbnail {
		display: block;
	}
</style>

<%-- 기본 URL --%>
<c:url var="_BASE_PARAM" value="">
	<c:param name="pageUnit" value="${searchVO.recordCountPerPage}"/>
	<c:if test="${not empty searchVO.category}"><c:param name="category" value="${searchVO.category}"/></c:if>
	<c:if test="${not empty searchVO.searchCondition}"><c:param name="searchCondition" value="${searchVO.searchCondition}"/></c:if>
	<c:if test="${not empty searchVO.searchKeyword}"><c:param name="searchKeyword" value="${searchVO.searchKeyword}"/></c:if>
</c:url>


<%-- 검색 영역 --%>
<div style="">
	<form action="/board" method="get">
		<input type="hidden" name="pageUnit" value="${searchVO.recordCountPerPage}"/>
		<input type="hidden" name="category" value="${searchVO.category}"/>
		<label for="ftext" class="">검색분류선택</label>
		<select name="searchCondition" id="ftext">
			<option value="0" <c:if test="${searchVO.searchCondition eq '0'}">selected="selected"</c:if> >제목</option> 
			<option value="1" <c:if test="${searchVO.searchCondition eq '1'}">selected="selected"</c:if> >내용</option> 
			<option value="2" <c:if test="${searchVO.searchCondition eq '2'}">selected="selected"</c:if> >제목+내용</option> 
			<option value="3" <c:if test="${searchVO.searchCondition eq '3'}">selected="selected"</c:if> >작성자</option> 
		</select>
		<label for="" class="">검색어입력</label>
		<input name="searchKeyword" value="<c:out value="${searchVO.searchKeyword}"/>" type="text" class="" id="">
		<span class=""><input type="submit" value="검색" title=""></span>
	</form>
</div>



<div style="padding: 0; display: flex; justify-content: center;">
	<%-- 게시글 목록 수 영역 --%>
	<div style="border: 0px; margin: 0; margin-left: auto;">
		<form id="pageSizeForm" action="/board" method="get">
			<input type="hidden" name="category" value="${searchVO.category}"/>
			<input type="hidden" name="searchCondition" value="${searchVO.searchCondition}"/>
			<input type="hidden" name="searchKeyword" value="${searchVO.searchKeyword}"/>
			<label for="selectRecord">게시글 표시</label>
			<select id="selectRecord" name="pageUnit">
			
				<%-- <option value="1" ${searchVO.recordCountPerPage eq '1' ? 'selected' : ''} >1개</option> --%>
				<option value="10" ${searchVO.recordCountPerPage eq '10' ? 'selected' : ''} >10개</option>
				<option value="30" ${searchVO.recordCountPerPage eq '30' ? 'selected' : ''} >30개</option>
				<option value="50" ${searchVO.recordCountPerPage eq '50' ? 'selected' : ''} >50개</option>
			</select>
		</form>
	</div>
	<%-- 게시글 카테고리 --%>
	<div style="border: 0px; text-align: center; margin: 0 auto; position: absolute">
		<c:url var="allPost" value="/board"/>
		<a href="${allPost}" class="${empty searchVO.category ? 'active' : 'inactive'}" style="border: 0;">전체</a>
		|
		<c:url var="recommendPost" value="/board">
			<c:param name="category" value="-1"/>
		</c:url>
		<a href="${recommendPost}" class="${searchVO.category == -1 ? 'active' : 'inactive'}" style="border: 0;">추천</a>
		|
		<c:url var="generalPost" value="/board">
			<c:param name="category" value="1"/>
		</c:url>
		<a href="${generalPost}" class="${searchVO.category == 1 ? 'active' : 'inactive'}" style="border: 0;">일반</a>
		|
		<c:url var="infoPost" value="/board">
			<c:param name="category" value="2"/>
		</c:url>
		<a href="${infoPost}" class="${searchVO.category == 2 ? 'active' : 'inactive'}" style="border: 0;">정보</a>
		|
		<c:url var="questionPosts" value="/board">
			<c:param name="category" value="3"/>
		</c:url>
		<a href="${questionPosts}" class="${searchVO.category == 3 ? 'active' : 'inactive'}" style="border: 0;">질문</a>
		|
		<c:url var="suggestionReportPost" value="/board">
			<c:param name="category" value="4"/>
		</c:url>
		<a href="${suggestionReportPost}" class="${searchVO.category == 4 ? 'active' : 'inactive'}" style="border: 0;">건의/신고</a>
	</div>
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
				<sec:authorize access="hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')">
					<th class="" scope="col">관리</th>
				</sec:authorize>
			</tr>
		</thead>
		<tbody>
		<!-- 공지영역 -->
		<c:forEach var="result" items="${noticeResultList}" varStatus="status">
			<tr class="notice">
				<td class="num"><span class="label-bbs spot">공지</span></td>
				<td class="tit" style="padding-left: 20px; text-align: left;">
					<c:url var="viewUrl" value="/board/${result.boardIdNum}${_BASE_PARAM}">
						<c:param name="pageIndex" value="${searchVO.pageIndex}"/>
					</c:url>
					<a href="${viewUrl}" style="border: 0;"><c:out value="${result.boardSj} [${result.replyCnt}]"/></a>
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
		<tr><td colspan="8" style="padding: 0px;"><hr></td></tr>
		<!-- 일반글 영역 -->
		<c:forEach var="result" items="${resultList}" varStatus="status">
			<tr>
				<td class="num">
					<c:out value="${paginationInfo.totalRecordCount - ((searchVO.pageIndex-1) * searchVO.pageUnit) - (status.index)}"/>
				</td>
				<td style="padding-left: 20px; text-align: left;" class="post-title">
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
					<c:url var="viewUrl" value="/board/${result.boardIdNum}${_BASE_PARAM}">
						<c:param name="pageIndex" value="${searchVO.pageIndex}"/>
					</c:url>
					<a href="${viewUrl}" style="border: 0;">
						<c:out value="${categoryName}"/> 
						<c:if test="${result.chkImage >= 1}">
							<img alt="이미지 게시글 아이콘" src="/img/ico_img.png">
						</c:if>
						<c:if test="${result.othbcAt eq 'Y'}">
							<img alt="비밀글 아이콘" src="/img/ico_board_lock.gif">
						</c:if>
						<c:out value="${result.boardSj} [${result.replyCnt}]"/>
					</a>
					<c:if test="${not empty result.streFileNm}">
						<img src="/image/thumbnail/${result.streFileNm}.${result.fileExtsn}" alt="${result.boardSj}" class="thumbnail">
					</c:if>
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
				<sec:authorize access="hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')">
					<td>
		                <c:url var="delUrl" value="/board/${result.boardIdNum}/delete${_BASE_PARAM}">
		                    <c:param name="pageIndex" value="${searchVO.pageIndex}"/>
		                </c:url>
		                <button type="button" data-url="${delUrl}" class="btn-del" data-file-id="${result.atchFileId}">삭제</button>
		            </td>
	            </sec:authorize>
			</tr>
		</c:forEach>
		<%-- 조건에 맞는 글이 없을 경우 --%>
		<c:if test="${fn:length(resultList) == 0}">
			<tr class="empty"><td colspan="6">검색 데이터가 없습니다.</td></tr>
		</c:if>
		</tbody>
	</table>
	<form id="delForm" action="#" method="post" style="display: none;">
		<input type="hidden" name="registerId" value=""/>
		<input type="hidden" name="atchFileId" value=""/>
		<sec:csrfInput/>
	</form>
</div>

<div style="padding: 0; display: flex; justify-content: center;">
	<%-- 페이징 영역 --%>
	<div id="paging" style="border: 0px; margin: 0 auto; align-items: center; position: absolute;">
		<%-- 처음 --%>
		<c:if test="${paginationInfo.firstPageNoOnPageList > 1}">
			<c:url var="firstPageUrl" value="/board${_BASE_PARAM}">
				<c:param name="pageIndex" value="1"/>
			</c:url>
			<a href="${firstPageUrl}">처음</a>
		</c:if>
		<%-- 이전 --%>
		<c:if test="${paginationInfo.currentPageNo > 1}">
			<c:url var="prevPageUrl" value="/board${_BASE_PARAM}">
				<c:param name="pageIndex" value="${paginationInfo.prevPage}"/>
			</c:url>
			<a href="${prevPageUrl}">이전</a>
		</c:if>
		<%-- 번호 --%>
		<c:forEach var="pageNum" begin="${paginationInfo.firstPageNoOnPageList}" end="${paginationInfo.lastPageNoOnPageList}">
			<c:choose>
				<c:when test="${pageNum == paginationInfo.currentPageNo}">
					<span><b>${pageNum}</b></span>
				</c:when>
				<c:otherwise>
					<c:url var="pageUrl" value="/board${_BASE_PARAM}">
						<c:param name="pageIndex" value="${pageNum}"/>
					</c:url>
					<a href="${pageUrl}">${pageNum}</a>
				</c:otherwise>
			</c:choose>
		</c:forEach>
		<%-- 다음 --%>
		<c:if test="${paginationInfo.currentPageNo < paginationInfo.totalPageCount}"> 
			<c:url var="nextPageUrl" value="/board${_BASE_PARAM}">
				<c:param name="pageIndex" value="${paginationInfo.nextPage}"/>
			</c:url>
			<a href="${nextPageUrl}">다음</a>
		</c:if>
		<%-- 마지막 --%>
		<c:if test="${paginationInfo.lastPageNoOnPageList < paginationInfo.totalPageCount}">
			<c:url var="lastPageUrl" value="/board${_BASE_PARAM}">
				<c:param name="pageIndex" value="${paginationInfo.totalPageCount}"/>
			</c:url>
			<a href="${lastPageUrl}">마지막</a>
		</c:if>
	</div>
	
	
	<div style=" border: 0px; margin: 0; margin-left: auto;">
		<c:url var="postUrl" value="/board/write${_BASE_PARAM}"/>
		<a href="${postUrl}">글쓰기</a>
	</div>
</div>



<script>
$(document).ready(function(){
	// 게시글 개수 표시 선택
	$("#selectRecord").change(function() {
	    $('#pageSizeForm').submit();
	});
	
	// 게시글 삭제
	$('.btn-del').click(function() {
		if (!confirm("삭제하시겠습니까?")) {
			return false;
		}
		const delUrl = $(this).attr('data-url');
		const fileId = $(this).attr('data-file-id');
		
		$('#delForm').attr('action', delUrl);
		$('input[name = atchFileId]').val(fileId);
		$('#delForm').submit();
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