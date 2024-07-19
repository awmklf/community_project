package community.board.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import community.board.service.BoardDAO;
import community.board.service.BoardService;
import community.board.service.BoardVO;

@Service
public class BoardServiceImpl implements BoardService {

	/** boardDAO DI */
	@Autowired
	private BoardDAO boardDAO;

	/** redisTemplate DI */
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

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
		// 조회수 키 생성(게시글 아이디, 유저 아이디, 유저 아이피)
		String keyForId = "ID:" + vo.getUserId() + ":view:" + vo.getBoardId();
		String keyForIp = "IP:" + vo.getUserIp() + ":view:" + vo.getBoardId();
		// 조회수 증가 검증
		if (vo.getTriggerViewCntUp().equals("Y")) {
			// 레디스 서버로부터 조회수 키 조회 및 검증
			Boolean isViewedByUser = (Boolean) redisTemplate.opsForValue().get(keyForId);
			Boolean isViewedByIp = (Boolean) redisTemplate.opsForValue().get(keyForIp);
			if ((isViewedByIp == null || !isViewedByIp) 
					&& (isViewedByUser == null || !isViewedByUser)) {
				boardDAO.updateViewCnt(vo); // 조회수 증가
				// Redis에 조회 정보 저장 (5분 유지)
				redisTemplate.opsForValue().set(keyForIp, true, 5, TimeUnit.MINUTES);
				if (vo.getUserId() != null) {
				redisTemplate.opsForValue().set(keyForId, true, 5, TimeUnit.MINUTES);
				}
			}
		}
		return boardDAO.selectBoard(vo);
	}
	
	/** 비밀글 확인 */
	@Override
	public Boolean othbcChk(BoardVO vo) throws Exception {
		BoardVO othbcAt = selectBoard(vo);
		if (othbcAt.getOthbcAt().equals("Y")) {
			if (!vo.getMngAt().equals("Y") 
					&& vo.getUserId().equals(othbcAt.getUserId())) {
				return true;
			}
		}
		return false;
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

	/** 게시글 수정 */
	@Override
	public void updateBoard(BoardVO vo) throws Exception {
		boardDAO.updateBoard(vo);
	}
	
	/** 게시글 삭제 */
	@Override
	public void deleteBoard(BoardVO vo) throws Exception{
		boardDAO.deleteBoard(vo);
	}
	
	/** 게시글 추천 */
	@Override
	public int updateBoardRecCnt(BoardVO vo) throws Exception {
		// 추천 키 생성(게시글 아이디, 유저 아이디)
		String keyForId = "ID:" + vo.getUserId() + ":rec:" + vo.getBoardId();
		// 레디스 서버로부터 추천 키 조회 및 검증
		Boolean isRecommendedByUser = (Boolean) redisTemplate.opsForValue().get(keyForId);
			if (isRecommendedByUser == null || !isRecommendedByUser) {
			boardDAO.updateBoardRecCnt(vo); // 추천수 증가
			// 유저의 게시글 추천 정보 확인
			BoardVO recInfo = boardDAO.selectRecommend(vo);
			if (recInfo != null && StringUtils.hasText(recInfo.getBoardId()) && StringUtils.hasText(recInfo.getUserId())) {
				boardDAO.updateRecommend(recInfo); // 추천 정보 갱신
			} else
				boardDAO.insertRecommend(vo); // 첫 추천 정보 입력
			redisTemplate.opsForValue().set(keyForId, true, 1, TimeUnit.DAYS);
			return boardDAO.viewBoardRecCnt(vo);
		}
		return -1;
	}
}
