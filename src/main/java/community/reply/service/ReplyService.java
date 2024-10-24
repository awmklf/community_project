package community.reply.service;

import java.util.Map;

import community.cmm.pagination.PaginationCalc;

public interface ReplyService {

	/** 덧글 목록 조회 */
	public Map<String, Object> selectReplyList(ReplyVO vo) throws Exception;

	/** 덧글 개수 조회 */
	Map<String, Integer> selectReplyCnt(ReplyVO vo) throws Exception;
	
	/** 덧글 작성 */
	public int addReply(ReplyVO vo) throws Exception;
	
	/** 덧글 수정 */
	public int editReply(ReplyVO vo) throws Exception;
	
	/** 덧글 삭제 */
	public int delReply(ReplyVO vo) throws Exception;

	/** 페이지네이션 */
	PaginationCalc pagination(ReplyVO vo) throws Exception;
}
