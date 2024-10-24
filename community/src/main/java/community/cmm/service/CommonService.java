package community.cmm.service;

import org.springframework.security.access.AccessDeniedException;

public interface CommonService {

	/** 게시글 번호 치환 */
	public String convertNumToBoardId(int boardIdNum) throws Exception;
	
	/** 접근 권한 확인 */
	public String roleChk(String registerId) throws AccessDeniedException;
}
