package community.board.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import community.board.service.BoardDAO;
import community.board.service.BoardService;
import community.board.service.BoardVO;

@Service
public class BoardServiceImpl implements BoardService {

	/** boardDAO DI */
	@Autowired
	private BoardDAO boardDAO;

	/** 게시글 목록 조회 */
	@Override
	public List<BoardVO> selectBoardList(BoardVO vo) throws Exception {
		return boardDAO.selectBoardList(vo);
	}

	/** 게시글 수 조회 */
	@Override
	public int selectBoardListCnt(BoardVO vo) throws Exception {
		return boardDAO.selectBoardListCnt(vo);
	}

	/** 게시글 내용 조회 */
	@Override
	public BoardVO selectBoard(BoardVO vo) throws Exception {
		boardDAO.updateViewCnt(vo);
		return boardDAO.selectBoard(vo);
	}

	/** 게시글 작성 */
	@Override
	public String insertBoard(BoardVO vo) throws Exception {
		// 게시글 번호 증가 로직
		String lastId = boardDAO.selectLastBoardId();
		int nextIdNum;
		try {
			nextIdNum = Integer.parseInt(lastId.substring(lastId.indexOf("_") + 1)) + 1;
		} catch (NumberFormatException e) {
			nextIdNum = Integer.parseInt(lastId.substring(lastId.indexOf("0"))) + 1;
		}
		String nextId = String.format("BOARD_%09d", nextIdNum);
		vo.setBoardId(nextId);
		boardDAO.insertBoard(vo);

		return nextId;
	}
}
