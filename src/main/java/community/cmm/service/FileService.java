package community.cmm.service;

import java.util.List;


public interface FileService {

	/** 업로드 파일 DB 등록 */
	public Integer insertFile(FileVO vo) throws Exception;
	
	/** 스마트 에디터 이미지 업로드 */
	public String uploadImage(FileVO vo) throws Exception;
	
	/** 게시글 내용으로부터 업로드된 이미지의 아이디 추출 */
	public List<String> extractImageIds (String inputString) throws Exception;

	/** 업로드 파일 상태정보 수정 */
	String updateFileUseAt(FileVO vo) throws Exception;
	
	/** 썸네일 작업 */
	public FileVO thumbnail(FileVO vo) throws Exception;
	
	/** 썸네일 DB 정보 갱신 */
	public void updateThumbnail (FileVO vo) throws Exception;
}
