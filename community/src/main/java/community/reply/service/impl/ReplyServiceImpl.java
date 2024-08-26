package community.reply.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import community.cmm.CommonService;
import community.cmm.pagination.PaginationCalc;
import community.reply.service.ReplyDAO;
import community.reply.service.ReplyService;
import community.reply.service.ReplyVO;

@Service
public class ReplyServiceImpl implements ReplyService {

	/** replyDAO DI */
	@Autowired
	private ReplyDAO replyDAO;

	/** commService DI */
	@Autowired
	CommonService cmmService;

	/** 게시글 번호 치환 */
	public String convertNumToBoardId(int boardIdNum) throws Exception {
		return cmmService.convertNumToBoardId(boardIdNum);
	}

	/** 접근 권한 확인 */
	public String roleChk(String registerId) throws Exception {
		return cmmService.roleChk(registerId);
	}

	/** 덧글 목록 조회 */
	@Override
	public Map<String, Object> selectReplyList(ReplyVO vo) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();

		vo.setBoardId(convertNumToBoardId(vo.getBoardIdNum()));

		Map<String, Integer> selectReplyListCnt = selectReplyCnt(vo);

		vo.setReplyListCnt(selectReplyListCnt.get("selectReplyListCnt")); // 실제 덧글 수
		vo.setReplyViewCnt(selectReplyListCnt.get("selectReplyViewCnt")); // 표시 덧글 수

		PaginationCalc pgCalc = pagination(vo);
		vo.setFirstIndex(pgCalc.getFirstRecordIndex()); // 쿼리 조회용 현재 페이지의 첫 페이지 번호
		vo.setRecordCountPerPage(pgCalc.getRecordCountPerPage()); // 페이지당 게시물 수

		resultMap.put("pagination", pgCalc);

		List<ReplyVO> resultReplyList = replyDAO.selectReplyList(vo);
		Iterator<ReplyVO> iterator = resultReplyList.iterator();
		while (iterator.hasNext()) {
			ReplyVO resultReply = iterator.next();
			if (!"Y".equals(resultReply.getUseAt())) { // 삭제 여부 확인
				if ("Y".equals(resultReply.getHasChildRep())) { // 자식 덧글 여부 확인
					resultReply.setReplyCn("삭제된 덧글");
				}
			}
		}

		resultMap.put("RepList", resultReplyList);

		return resultMap;
	}

	/** 페이지네이션 */
	@Override
	public PaginationCalc pagination(ReplyVO vo) throws Exception {
		PaginationCalc pgCalc = new PaginationCalc();
		// 계산
		pgCalc.setCurrentPageNo(vo.getPageIndex()); // 현재페이지
		pgCalc.setRecordCountPerPage(vo.getPageUnit()); // 페이지당 덧글 수
		pgCalc.setPageSize(vo.getPageSize()); // 페이지 리스트 수

		pgCalc.setTotalRecordCount(vo.getReplyViewCnt()); // 표시 덧글 수
		pgCalc.setReplyListCnt(vo.getReplyListCnt()); // 실제 덧글 수
		return pgCalc;
	}

	/** 덧글 개수 조회 */
	@Override
	public Map<String, Integer> selectReplyCnt(ReplyVO vo) throws Exception {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		String boardId = convertNumToBoardId(vo.getBoardIdNum());
		int selectReplyListCnt = replyDAO.selectReplyListCnt(boardId);
		map.put("selectReplyListCnt", selectReplyListCnt);
		int selectReplyViewCnt = replyDAO.selectReplyViewCnt(boardId);
		map.put("selectReplyViewCnt", selectReplyViewCnt);
		return map;
	}

	/** 덧글 작성 */
	@Override
	public int addReply(ReplyVO vo) throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		vo.setRegisterId(authentication.getName());
		vo.setBoardId(convertNumToBoardId(vo.getBoardIdNum()));
		String lastReplyId = replyDAO.selectLastReplyId(vo);
		int nextIdNum = 1;
		if (lastReplyId != null && lastReplyId.startsWith("REP_")) {
			try {
				nextIdNum = Integer.parseInt(lastReplyId.substring("REP_".length())) + 1;
			} catch (NumberFormatException e) {
				throw new RuntimeException("Invalid reply ID format: " + lastReplyId, e);
			}
		}
		String nextId = String.format("REP_%011d", nextIdNum);
		vo.setReplyId(nextId);
		int resultNum = replyDAO.insertReply(vo);
		return resultNum;
	}

	/** 덧글 수정 */
	@Override
	public int editReply(ReplyVO vo) throws Exception {
		// 작성자 or 관리자 인증
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		ReplyVO result = replyDAO.selectReply(vo);
		String role = roleChk(result.getRegisterId());
		if ("admin".equals(role))
			vo.setMngAt("Y");
		else if ("owner".equals(role))
			vo.setUserId(authentication.getName());

		int updateReply = replyDAO.updateReply(vo);
		return updateReply;
	}

	/** 덧글 삭제 */
	@Override
	public int delReply(ReplyVO vo) throws Exception {
		// 작성자 or 관리자(매니저 포함) 인증
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		ReplyVO result = replyDAO.selectReply(vo);
		String role = roleChk(result.getRegisterId());
		if ("admin".equals(role) || "manager".equals(role))
			vo.setMngAt("Y");
		else if ("owner".equals(role))
			vo.setUserId(authentication.getName());

		int deleteReply = replyDAO.deleteReply(vo);
		return deleteReply;
	}

}
