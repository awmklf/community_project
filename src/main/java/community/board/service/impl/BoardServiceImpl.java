package community.board.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import community.board.service.BoardDAO;
import community.board.service.BoardService;
import community.board.service.BoardVO;
import community.cmm.pagination.PaginationCalc;
import community.cmm.service.CommonService;
import community.cmm.service.FileService;
import community.cmm.service.FileVO;

@Service
public class BoardServiceImpl implements BoardService {

	/** boardDAO DI */
	@Autowired
	private BoardDAO boardDAO;

	/** redisTemplate DI */
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	/** commService DI */
	@Autowired
	CommonService cmmService;

	/** fileService DI */
	@Autowired
	FileService fileService;

	/** 게시글 번호 치환 */
	public String convertNumToBoardId(int boardIdNum) throws Exception {
		return cmmService.convertNumToBoardId(boardIdNum);
	}

	/** 접근 권한 확인 */
	public String roleChk(String registerId) throws Exception {
		return cmmService.roleChk(registerId);
	}

	/** 게시글 목록 조회 */
	@Override
	public Map<String, Object> selectBoardList(BoardVO vo) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		// 일반글 페이지네이션
		if ("N".equals(vo.getIsNotice())) {
			PaginationCalc pgCalc = pagination(vo);

			vo.setFirstIndex(pgCalc.getFirstRecordIndex()); // 쿼리 조회용 현재 페이지의 첫 페이지 번호
			vo.setRecordCountPerPage(pgCalc.getRecordCountPerPage()); // 페이지당 게시물 수

			resultMap.put("pagination", pgCalc);
		}
		vo.setSearchKeywords(handleSearchKeywords(vo)); // 검색 키워드 처리
		List<BoardVO> resultList = boardDAO.selectBoardList(vo);
		resultMap.put("resultList", resultList);
		return resultMap;

	}
	
	/** 검색 키워드 핸들러 */
	@Override
	public String[] handleSearchKeywords (BoardVO vo) throws Exception {
		String replaceResult = vo.getSearchKeyword().replaceAll("&", "&amp;")
						        .replaceAll("<", "&lt;")
						        .replaceAll(">", "&gt;")
						        .replaceAll("\"", "&quot;")
						        .replaceAll("'", "&#039;")
						        ;
		if ("3".equals(vo.getSearchCondition()) ) { // 작성자 검색의 경우 스플릿 패스
			String[] result = new String[1];
			result[0] = replaceResult;
			return result;
		}
		return replaceResult.split(" ");
	}

	/** 페이지네이션 */
	@Override
	public PaginationCalc pagination(BoardVO vo) throws Exception {
		PaginationCalc pgCalc = new PaginationCalc();
		// 계산
		pgCalc.setCurrentPageNo(vo.getPageIndex()); // 현재페이지
		pgCalc.setRecordCountPerPage(vo.getPageUnit()); // 페이지당 게시물 수
		pgCalc.setPageSize(vo.getPageSize()); // 페이지 리스트 수

		pgCalc.setTotalRecordCount(selectBoardListCnt(vo)); // 총 게시물 수(조건부)
		return pgCalc;
	}

	/** 게시글 수 조회 */
	@Override
	public int selectBoardListCnt(BoardVO vo) throws Exception {
		return boardDAO.selectBoardListCnt(vo);
	}

	/** 게시글 내용 조회 */
	@Override
	public BoardVO selectBoard(BoardVO vo) throws Exception {
		BoardVO result = null;

		if (vo.getBoardIdNum() == null)
			return result;
		vo.setBoardId(convertNumToBoardId(vo.getBoardIdNum()));

		result = boardDAO.selectBoard(vo);

		if (result == null) // 게시글 존재 여부 체크
			return result;
		result.setBoardIdNum(vo.getBoardIdNum());

		// 비밀글 여부 및 접근 권한 확인
		if ("Y".equals(result.getOthbcAt())) {
			// 작성자 정보 저장 및 권한 확인
			vo.setRegisterId(result.getRegisterId());
			roleChk(vo.getRegisterId());
		}
		if ("Y".equals(vo.getTriggerViewCntUp()) && !result.getRegisterId().equals(vo.getUserId())) {
			// 조회수 키 생성(게시글 아이디, 유저 아이디, 유저 아이피)
			String keyForId = "ID:" + vo.getUserId() + ":view:" + vo.getBoardId();
			String keyForIp = "IP:" + vo.getUserIp() + ":view:" + vo.getBoardId();
			// 레디스 서버로부터 조회수 키 조회 및 검증
			Boolean isViewedByUser = (Boolean) redisTemplate.opsForValue().get(keyForId);
			Boolean isViewedByIp = (Boolean) redisTemplate.opsForValue().get(keyForIp);
			if ((isViewedByUser == null || !isViewedByUser) && (isViewedByIp == null || !isViewedByIp)) { // 아이디 및 아이피 이력이 없을 때
				boardDAO.updateViewCnt(vo); // 조회수 증가
				result.setInqireCo(result.getInqireCo() + 1);// 뷰에 조회수 +1 갱신
				// Redis에 조회 정보 저장 (5분 유지)
				redisTemplate.opsForValue().set(keyForIp, true, 5, TimeUnit.MINUTES);
				if (vo.getUserId() != null) // 비로그인 유저 저장 방지
					redisTemplate.opsForValue().set(keyForId, true, 5, TimeUnit.MINUTES);
			}
		}
		return result;
	}

	/** 게시글 작성 */
	@Override
	public int insertBoard(BoardVO vo) throws Exception {
		if (vo.getIsNotice() == null)
			vo.setIsNotice("N");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		vo.setRegisterId(authentication.getName());
		// 게시글 번호 생성 로직
		String lastBoardId = boardDAO.selectLastBoardId();
		int nextIdNum = 1;
		if (lastBoardId != null && lastBoardId.startsWith("BOARD_")) { // null 및 문자열 "BOARD_" 시작 체크
			nextIdNum = Integer.parseInt(lastBoardId.substring("BOARD_".length())) + 1;
		}
		String nextId = String.format("BOARD_%09d", nextIdNum);
		vo.setBoardId(nextId);
		FileVO fvo = new FileVO();
		fvo.setBoardCn(vo.getBoardCn());
		String fileId = fileService.updateFileUseAt(fvo); // 첨부 최종 등록
		vo.setAtchFileId(fileId);
		boardDAO.insertBoard(vo);

		return nextIdNum;
	}

	/** 게시글 수정 */
	@Override
	public void updateBoard(BoardVO vo) throws Exception {
		// 작성자 or 관리자 인증
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String role = roleChk(vo.getRegisterId());
		if ("admin".equals(role))
			vo.setMngAt("Y");
		else if ("owner".equals(role))
			vo.setUserId(authentication.getName());
		FileVO fvo = new FileVO();
		fvo.setBoardCn(vo.getBoardCn());
		fvo.setAtchFileId(vo.getAtchFileId());
		String fileId = fileService.updateFileUseAt(fvo); // 첨부 수정
		vo.setAtchFileId(fileId);
		boardDAO.updateBoard(vo);
	}

	/** 게시글 삭제 */
	@Override
	public void deleteBoard(BoardVO vo) throws Exception {
		vo.setBoardId(convertNumToBoardId(vo.getBoardIdNum()));
		// 작성자 or 관리자(매니저 포함) 인증
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String role = roleChk(vo.getRegisterId());
		if ("admin".equals(role) || "manager".equals(role))
			vo.setMngAt("Y");
		else if ("owner".equals(role))
			vo.setUserId(authentication.getName());
		FileVO fvo = new FileVO();
		BoardVO resultBoard = boardDAO.selectBoard(vo);
		fvo.setBoardCn(resultBoard.getBoardCn());
		fvo.setAtchFileId(resultBoard.getAtchFileId());
		fvo.setUseAtBoard("N");
		fileService.updateFileUseAt(fvo); // 첨부 삭제
		boardDAO.deleteBoard(vo);
	}

	/** 게시글 추천 */
	@Override
	public int updateBoardRecCnt(BoardVO vo) throws Exception {
		vo.setBoardId(convertNumToBoardId(vo.getBoardIdNum()));
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
	
	/** 게시글 상태 변경 */
	@Override
	public void udtStatusBoard(BoardVO vo) throws Exception {
		vo.setBoardId(convertNumToBoardId(vo.getBoardIdNum()));
		// 관리자(매니저 포함) 인증
		String role = roleChk(vo.getRegisterId());
		if ("admin".equals(role) || "manager".equals(role))
			vo.setMngAt("Y");
		boardDAO.udtStatusBoard(vo);
	}

}
