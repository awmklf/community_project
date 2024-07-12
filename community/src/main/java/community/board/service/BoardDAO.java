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
}
