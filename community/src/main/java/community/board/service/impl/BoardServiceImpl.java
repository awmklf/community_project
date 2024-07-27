package community.board.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import community.board.service.BoardDAO;
import community.board.service.BoardService;
import community.board.service.BoardVO;
import community.cmm.pagination.PaginationCalc;

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
	public Map<String, Object> selectBoardList(BoardVO vo) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		// 일반글 페이지네이션
		if ("N".equals(vo.getIsNotice())) {
			PaginationCalc pgCalc = pagination(vo);

			vo.setFirstIndex(pgCalc.getFirstRecordIndex()); // 쿼리 조회용 현재 페이지의 첫 페이지 번호
			vo.setRecordCountPerPage(pgCalc.getRecordCountPerPage()); // 페이지당 게시물 수

			resultMap.put("pagination", pgCalc);
		}
		resultMap.put("resultList", boardDAO.selectBoardList(vo));
		return resultMap;

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

		// 비밀글 여부 및 접근 권한 확인
		BoardVO result = boardDAO.selectBoard(vo);
		if ("Y".equals(result.getOthbcAt())) {
			// 작성자 정보 저장 및 권한 확인
			vo.setRegisterId(result.getRegisterId());
			roleChk(vo);
		}
		if ("Y".equals(vo.getTriggerViewCntUp())) {
			// 조회수 키 생성(게시글 아이디, 유저 아이디, 유저 아이피)
			String keyForId = "ID:" + vo.getUserId() + ":view:" + vo.getBoardId();
			String keyForIp = "IP:" + vo.getUserIp() + ":view:" + vo.getBoardId();
			// 레디스 서버로부터 조회수 키 조회 및 검증
			Boolean isViewedByUser = (Boolean) redisTemplate.opsForValue().get(keyForId);
			Boolean isViewedByIp = (Boolean) redisTemplate.opsForValue().get(keyForIp);
			if ((isViewedByUser == null || !isViewedByUser) && (isViewedByIp == null || !isViewedByIp)) { // 아이디 및 아이피 이력이 없을 때
				boardDAO.updateViewCnt(vo); // 조회수 증가
				result.setInqireCo(result.getInqireCo()+1);// 뷰에 조회수 +1 갱신
				// Redis에 조회 정보 저장 (5분 유지)
				redisTemplate.opsForValue().set(keyForIp, true, 5, TimeUnit.MINUTES);
				if (vo.getUserId() != null) // 비로그인 유저 저장 방지
					redisTemplate.opsForValue().set(keyForId, true, 5, TimeUnit.MINUTES);
			}
		}
		return result;
	}

	/** 접근 권한 확인 */
	@Override
	public String roleChk(BoardVO vo) throws AccessDeniedException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
		boolean isManager = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));
		boolean isOwner = authentication.getName().equals(vo.getRegisterId());
		if (isAdmin)
			return "admin";
		else if (isManager)
			return "manager";
		else if (isOwner)
			return "owner";
		else
			throw new AccessDeniedException("접근 권한이 없습니다. 관리자 또는 본인만 접근 가능합니다.");
	}

	/** 게시글 작성 */
	@Override
	public String insertBoard(BoardVO vo) throws Exception {
		if (vo.getIsNotice() == null)
			vo.setIsNotice("N");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		vo.setRegisterId(authentication.getName());
		// 게시글 번호 증가 로직
		String lastId = boardDAO.selectLastBoardId();
		int nextIdNum = 1;
		if (lastId != null && lastId.startsWith("BOARD_")) {
			try {
				nextIdNum = Integer.parseInt(lastId.substring("BOARD_".length())) + 1;
			} catch (NumberFormatException e) {
				throw new RuntimeException("Invalid board ID format: " + lastId, e);
			}
		}
		String nextId = String.format("BOARD_%09d", nextIdNum);
		vo.setBoardId(nextId);
		boardDAO.insertBoard(vo);

		return nextId;
	}

	/** 게시글 수정 */
	@Override
	public void updateBoard(BoardVO vo) throws Exception {
		// 작성자 or 관리자 인증
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String role = roleChk(vo);
		if ("admin".equals(role))
			vo.setMngAt("Y");
		else if ("owner".equals(role))
			vo.setUserId(authentication.getName());

		boardDAO.updateBoard(vo);
	}

	/** 게시글 삭제 */
	@Override
	public void deleteBoard(BoardVO vo) throws Exception {
		// 작성자 or 관리자 인증
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String role = roleChk(vo);
		if ("admin".equals(role) || "manager".equals(role))
			vo.setMngAt("Y");
		else if ("owner".equals(role))
			vo.setUserId(authentication.getName());
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
