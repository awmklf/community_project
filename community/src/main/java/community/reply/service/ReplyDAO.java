package community.reply.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReplyDAO {
	/** 덧글 목록 조회 */
	public List<ReplyVO> selectReplyList(ReplyVO vo) throws Exception;

	/** 덧글 개수 조회 */
	public int selectReplyListCnt(String boardId) throws Exception;
	
	/** 덧글 표시 개수 조회 */
	public int selectReplyViewCnt(String boardId) throws Exception;
	
	/** 덧글 조회 */
	public ReplyVO selectReply(ReplyVO vo) throws Exception;

	/** 덧글 마지막 번호 조회 */
	public String selectLastReplyId(ReplyVO vo) throws Exception;

	/** 덧글 작성 */
	public int insertReply(ReplyVO vo) throws Exception;
	
	/** 덧글 수정 */
	public int updateReply(ReplyVO vo) throws Exception;
	
	/** 덧글 삭제 */
	public int deleteReply(ReplyVO vo) throws Exception;
}
