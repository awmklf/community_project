package community.reply.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import community.reply.service.ReplyService;
import community.reply.service.ReplyVO;

@Controller
public class ReplyController {

	/** replyService DI */
	@Autowired
	ReplyService replyService;

	/** 덧글 목록 조회 */
	@ResponseBody
	@RequestMapping("/reply/list/{boardIdNum}")
	public Map<String, Object> selectReplyList (ReplyVO vo, ModelMap model) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<ReplyVO> resultList = replyService.selectReplyList(vo);
		map.put("RepList", resultList);
		int totRepListCnt = replyService.selectReplyListCnt(vo);
		map.put("totRepListCnt", totRepListCnt);
		return map;
	}
	
	/** 덧글 작성 */
	@ResponseBody
	@RequestMapping("/reply/{boardIdNum}/add")
	@PreAuthorize("isAuthenticated()")
	public Map<String, Object> addReply (ReplyVO vo, HttpServletRequest request) throws Exception {
		vo.setCreatIp(request.getRemoteAddr());
		Map<String, Object> map = new HashMap<String, Object>();
		int addReply = replyService.addReply(vo);
		map.put("addRepCnt", addReply);
		return map;
	}
	
	/** 덧글 수정 */
	@ResponseBody
	@RequestMapping("/reply/{boardIdNum}/edit")
	@PreAuthorize("isAuthenticated()")
	public Map<String, Object> editReply (ReplyVO vo) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		int editReply = replyService.editReply(vo);
		map.put("editReplyCnt", editReply);
		return map;
	}
	
	/** 덧글 삭제 */
	@ResponseBody
	@RequestMapping("/reply/{boardIdNum}/delete")
	@PreAuthorize("isAuthenticated()")
	public Map<String, Object> delReply (ReplyVO vo) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		int delReply = replyService.delReply(vo);
		map.put("delReplyCnt", delReply);
		return map;
	}
}
