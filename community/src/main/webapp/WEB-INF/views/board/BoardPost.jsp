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
	<c:if test="${not empty searchVO.searchCondition}"><c:param name="searchCondition" value="${searchVO.searchCondition}"/></c:if>
	<c:if test="${not empty searchVO.searchKeyword}"><c:param name="searchKeyword" value="${searchVO.searchKeyword}"/></c:if>
</c:url>

<%-- 등록, 수정 url --%>
<c:choose>
	<c:when test="${not empty searchVO.boardId}">
		<c:set var="actionUrl" value="/board/update"/>
	</c:when>
	<c:otherwise>
		<c:set var="actionUrl" value="/board/insert"/>
	</c:otherwise>
</c:choose>

<div>
	<form action="${actionUrl}" method="post" onsubmit="return regist()" id="form" >
		<input type="hidden" name="boardId" value="${result.boardId}">
		<div>
			<select id="" name="category">
				<option value="1" <c:if test="${result.category eq '1'}">selected="selected"</c:if> >일반</option>
				<option value="2" <c:if test="${result.category eq '2'}">selected="selected"</c:if> >정보</option>
				<option value="3" <c:if test="${result.category eq '3'}">selected="selected"</c:if> >질문</option>
				<option value="4" <c:if test="${result.category eq '4'}">selected="selected"</c:if> >건의&신고</option>
			</select>
			<input type="text" name="boardSj" id="boardSj" title="제목" class="" placeholder="제목" value="<c:out value="${result.boardSj}"/>">
		</div>
		<sec:authorize access="hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')">
			<div>
				공지여부
				<label for="isNoticeY">예 : </label>
				<input type="radio" id="isNoticeY" name="isNotice" value="Y" <c:if test="${result.isNotice eq 'Y'}">checked="checked"</c:if>>
				&nbsp;&nbsp;&nbsp;
				<label for="isNoticeN">아니오 : </label>
				<input type="radio" id="isNoticeN" name="isNotice" value="N" <c:if test="${result.isNotice ne 'Y'}">checked="checked"</c:if>>
			</div>
		</sec:authorize>
		<div id="privateSection" style="display: none;">
			비공개 여부
			<label for="othbcAtY">예 : </label>
			<input type="radio" name="othbcAt" id="othbcAtY" value="Y" <c:if test="${result.othbcAt eq 'Y'}">checked="checked"</c:if>>
			&nbsp;&nbsp;&nbsp;
			<label for="othbcAtN">아니오 : </label>
			<input type="radio" name="othbcAt" id="othbcAtN" value="N" <c:if test="${result.othbcAt ne 'Y'}">checked="checked"</c:if>>
		</div>
		<div>
			<textarea id="boardCn" name="boardCn" rows="15" title="내용입력"><c:out value="${result.boardCn}"/></textarea>
		</div>
		<div>
			<c:choose>
				<c:when test="${userId == result.registerId}">
					<c:url var="udtUrl" value="board/write${_BASE_PARAM}">
						<c:param name="boardId" value="${searchVO.boardId}"/>
					</c:url>
					<a href="${uptUrl}" id="btn-reg" class="">수정</a>
				</c:when>
				<c:otherwise>
					<a href="#none" id="btn-reg" class="">등록</a>
				</c:otherwise>
			</c:choose>
			<c:url var="listUrl" value="/board/list${_BASE_PARAM}"/>
			<a href="${listUrl}" id="btn-cnl">취소</a>
		</div>
		<sec:csrfInput/>
	</form>
</div>


<script>
	$(document).ready(function() {
		// 게시글 등록
		$("#btn-reg").click(function() {
			$("#form").submit();
			return false;
		});
		
		// 취소
		$("#btn-cnl").click(function() {
			if (!confirm("취소하시겠습니까?")) {
				return false;
			}
		});
	
		
		// 비공개 여부 섹션 활성화
		$('select[name="category"]').change(function(){
		    if ($(this).val() == '4') {
		      $('#privateSection').show();
		    } else {
		      $('#privateSection').hide();
		      $('#othbcAtN').prop('checked', true);
		    }
		  });
		// 페이지 로드 시에 '비공개 여부' 섹션 표시 여부 체크
		$('select[name="category"]').change(updatePrivateSectionVisibility);
		updatePrivateSectionVisibility();
	});
		// 미입력 방지
		function regist() {
			if (!$("#boardSj").val()) {
				alert("제목을 입력해주세요.");
				$("#boardSj").focus();
				return false;
			}
			if (!tinymce.get('boardCn').getContent()) {
		        alert("내용을 입력해주세요.");
		        tinymce.get('boardCn').focus();
		        return false;
		    }
		}
	
</script>

<!-- 에디터 -->
<script src="https://cdn.tiny.cloud/1/ndtna16z7sd5pkn6gv8ju69m7r2zpuve06tu07befwsym50f/tinymce/6/tinymce.min.js" referrerpolicy="origin"></script>
<script>
$(function(){
    var plugins = [
        "advlist", "autolink", "lists", "link", "image", "charmap", "print", "preview", "anchor",
        "searchreplace", "visualblocks", "code", "fullscreen", "insertdatetime", "media", "table",
        "paste", "code", "help", "wordcount", "save"
    ];
    var edit_toolbar = 'formatselect fontselect fontsizeselect |'
               + ' forecolor backcolor |'
               + ' bold italic underline strikethrough |'
               + ' alignjustify alignleft aligncenter alignright |'
               + ' bullist numlist |'
               + ' table tabledelete |'
               + ' link image';

    tinymce.init({
    language: "ko_KR", //한글판으로 변경
        selector: '#boardCn',
        height: 500,
        menubar: false,
        plugins: plugins,
        toolbar: edit_toolbar,
        
        /*** image upload ***/
        image_title: true,
        /* enable automatic uploads of images represented by blob or data URIs*/
        automatic_uploads: true,
        /*
            URL of our upload handler (for more details check: https://www.tiny.cloud/docs/configure/file-image-upload/#images_upload_url)
            images_upload_url: 'postAcceptor.php',
            here we add custom filepicker only to Image dialog
        */
        file_picker_types: 'image',
        /* and here's our custom image picker*/
        file_picker_callback: function (cb, value, meta) {
            var input = document.createElement('input');
            input.setAttribute('type', 'file');
            input.setAttribute('accept', 'image/*');

            /*
            Note: In modern browsers input[type="file"] is functional without
            even adding it to the DOM, but that might not be the case in some older
            or quirky browsers like IE, so you might want to add it to the DOM
            just in case, and visually hide it. And do not forget do remove it
            once you do not need it anymore.
            */
            input.onchange = function () {
                var file = this.files[0];

                var reader = new FileReader();
                reader.onload = function () {
                    /*
                    Note: Now we need to register the blob in TinyMCEs image blob
                    registry. In the next release this part hopefully won't be
                    necessary, as we are looking to handle it internally.
                    */
                    var id = 'blobid' + (new Date()).getTime();
                    var blobCache =  tinymce.activeEditor.editorUpload.blobCache;
                    var base64 = reader.result.split(',')[1];
                    var blobInfo = blobCache.create(id, file, base64);
                    blobCache.add(blobInfo);

                    /* call the callback and populate the Title field with the file name */
                    cb(blobInfo.blobUri(), { title: file.name });
                };
                reader.readAsDataURL(file);
            };
            input.click();
        },
        /*** image upload ***/
        
        content_style: 'body { font-family:Helvetica,Arial,sans-serif; font-size:14px }'
    });
});
</script>

<!-- 푸터 -->
<c:import url="/footer" charEncoding="utf-8"/>