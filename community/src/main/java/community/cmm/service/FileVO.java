package community.cmm.service;

import java.io.BufferedInputStream;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FileVO {

	private MultipartFile upFile;

	/** 첨부파일ID */
	private String atchFileId;
	/** 첨부파일 순번 */
	private String fileSn;
	/** 첨부파일 생성일 */
	private LocalDateTime creatDt;
	/** 사용여부 */
	private String useAt;
	/** 삭제일시 */
	private LocalDateTime deletedAt;
	/** 첨부파일 저장 경로 */
	private String fileStreCours;
	/** 첨부파일 저장이름 */
	private String streFileNm;
	/** 첨부파일 원본 이름 */
	private String orignlFileNm;
	/** 첨부파일 확장자 */
	private String fileExtsn;
	/** 첨부파일 크기 */
	private String fileSize;

	/** 업로드 처리용 인풋스트림 */
	BufferedInputStream inputStream;
	/** 이미지 체크용 게시글 내용 */
	private String boardCn;
	/** 게시글 사용 여부 */
	private String useAtBoard;

}
