package community.board.service;

import java.util.Map;

import community.cmm.pagination.PaginationCalc;

public interface BoardService {

	/** 게시글 목록 조회 */
	public Map<String, Object> selectBoardList(BoardVO vo) throws Exception;
	
	/** 검색 키워드 핸들러 */
	public String[] handleSearchKeywords (BoardVO vo) throws Exception;

	/** 페이지네이션 */
	public PaginationCalc pagination(BoardVO vo) throws Exception;

	/** 게시글 수 조회 */
	public int selectBoardListCnt(BoardVO vo) throws Exception;

	/** 게시글 내용 조회 */
	public BoardVO selectBoard(BoardVO vo) throws Exception;

	/** 게시글 작성 */
	public int insertBoard(BoardVO vo) throws Exception;

	/** 게시글 수정 */
	public void updateBoard(BoardVO vo) throws Exception;

	/** 게시글 삭제 */
	public void deleteBoard(BoardVO vo) throws Exception;

	/** 게시글 추천 */
	public int updateBoardRecCnt(BoardVO vo) throws Exception;

}
