package community.board.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardDAO {
	/** 게시글 조회 */
	public List<BoardVO> selectBoardList(BoardVO vo) throws Exception;
	
	/** 게시글 수 조회 */
	public int selectBoardListCnt(BoardVO vo) throws Exception;
	
	/** 게시글 조회수 증가 */
	public void updateViewCnt(BoardVO vo) throws Exception;
	
	/** 게시글 내용 조회 */
	public BoardVO selectBoard(BoardVO vo) throws Exception;
	
	/** 마지막 게시글 번호 조회 */
	public String selectLastBoardId() throws Exception;

	/** 게시글 작성 */
	public void insertBoard(BoardVO vo) throws Exception;
	
	/** 게시글 수정 */
	public void updateBoard(BoardVO vo) throws Exception;
	
	/** 게시글 삭제 */
	public void deleteBoard(BoardVO vo) throws Exception;
	
	/** 게시글 추천수 증가 */
	public void updateBoardRecCnt(BoardVO vo) throws Exception;
	
	/** 게시글 추천수 조회 */
	public int viewBoardRecCnt(BoardVO vo) throws Exception;
	
	/** 게시글 추천 정보 조회 */
	public BoardVO selectRecommend(BoardVO vo) throws Exception;
	
	/** 게시글 추천 정보 추가 */
	public void insertRecommend(BoardVO vo) throws Exception;
	
	/** 게시글 추천 정보 갱신 */
	public void updateRecommend(BoardVO vo) throws Exception;
}
