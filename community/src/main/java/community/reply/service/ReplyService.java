package community.reply.service;

import java.util.List;

public interface ReplyService {

	/** 덧글 목록 조회 */
	public List<ReplyVO> selectReplyList(ReplyVO vo) throws Exception;

	/** 덧글 개수 조회 */
	int selectReplyListCnt(ReplyVO vo) throws Exception;
	
	/** 덧글 작성 */
	public int addReply(ReplyVO vo) throws Exception;
	
	/** 덧글 수정 */
	public int editReply(ReplyVO vo) throws Exception;
	
	/** 덧글 삭제 */
	public int delReply(ReplyVO vo) throws Exception;
}
