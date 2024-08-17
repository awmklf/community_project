package community.reply.service.impl;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import community.cmm.CommonService;
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
	public List<ReplyVO> selectReplyList(ReplyVO vo) throws Exception {
		vo.setBoardId(convertNumToBoardId(vo.getBoardIdNum()));
		List<ReplyVO> resultReplyList = replyDAO.selectReplyList(vo);
		Iterator<ReplyVO> iterator = resultReplyList.iterator();
		while (iterator.hasNext()) {
		    ReplyVO resultReply = iterator.next();
		    if (!"Y".equals(resultReply.getUseAt())) { // 삭제 여부 확인
		        if ("Y".equals(resultReply.getHasChildRep())) { //자식 덧글 여부 확인
		            resultReply.setReplyCn("삭제된 덧글");
		        } else {
		            iterator.remove();
		        }
		    }
		}
		return resultReplyList;
	}
	
	/** 덧글 개수 조회 */
	@Override
	public int selectReplyListCnt(ReplyVO vo) throws Exception {
		String boardId = convertNumToBoardId(vo.getBoardIdNum());
		return replyDAO.selectReplyListCnt(boardId);
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
