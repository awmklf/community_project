package community.board.web;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import community.board.service.BoardService;
import community.board.service.BoardVO;
import community.cmm.CommonService;

@Controller
public class BoardController {

	/** boardService DI */
	@Autowired
	BoardService boardService;
	
	/** commService DI */
	@Autowired
	CommonService cmmService;

	/** 게시글 목록 조회 */
	@RequestMapping("/board")
	public String boardSelectList(@ModelAttribute("searchVO") BoardVO vo, HttpServletRequest req, ModelMap model) throws Exception {
		// 공지 영역 게시글
		vo.setIsNotice("Y");
		Map<String, Object> noticeResultList = boardService.selectBoardList(vo);
		model.addAttribute("noticeResultList", noticeResultList.get("resultList"));

		// 일반 게시글
		vo.setIsNotice("N");
		Map<String, Object> resultList = boardService.selectBoardList(vo);
		model.addAttribute("resultList", resultList.get("resultList"));

		// 페이징
		model.addAttribute("paginationInfo", resultList.get("pagination"));

		// 현재날짜
		Date currentDate = new Date();
		model.addAttribute("currentDate", currentDate);

		return "board/BoardSelectList";
	}

	/** 게시글 내용 조회 */
	@GetMapping("/board/{boardIdNum}")
	public String boardSelect(@ModelAttribute("searchVO") BoardVO vo, HttpServletRequest request, ModelMap model, HttpSession session) throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser"))
			vo.setUserId(auth.getName()); // 아이디 저장(로그인 유저의 경우)
		vo.setUserIp(request.getRemoteAddr()); // 아이피 저장
		vo.setTriggerViewCntUp("Y"); // 조회수 증가 허용

		BoardVO result = boardService.selectBoard(vo);

		if (result == null)
			return "cmm/error/404";

		model.addAttribute("result", result);
		return "board/BoardView";
	}

	/** 게시글 작성 및 수정 폼 */
	@GetMapping({"/board/write", "/board/{boardIdNum}/edit"})
	@PreAuthorize("isAuthenticated()")
	public String boardRegist(@ModelAttribute("searchVO") BoardVO vo, ModelMap model) throws Exception {
		BoardVO result = new BoardVO();
		// 수정 폼 여부 확인
		if (vo.getBoardIdNum() != null) {
			result = boardService.selectBoard(vo);
			if (result != null) { // 게시글 존재 여부 체크
				String roleChk = cmmService.roleChk(result.getRegisterId());
				if ("manager".equals(roleChk)) // 매니저 접근 불가
					return "cmm/error/403";
			}
		}
		model.addAttribute("result", result);
		return "board/BoardPost";
	}

	/** 게시글 작성 */
	@PostMapping("/board/insert")
	@PreAuthorize("isAuthenticated()")
	public String insert(@ModelAttribute("searchVO") BoardVO vo, HttpServletRequest request) throws Exception {
		vo.setCreatIp(request.getRemoteAddr());
		int resultBoardIdNum = boardService.insertBoard(vo);
		return "redirect:/board/" + resultBoardIdNum;
	}

	/** 게시글 수정 */
	@PostMapping("/board/{boardIdNum}/update")
	@PreAuthorize("isAuthenticated()")
	public String update(@ModelAttribute("searchVO") BoardVO vo) throws Exception {
		boardService.updateBoard(vo);
		return "redirect:/board/" + vo.getBoardIdNum();
	}

	/** 게시글 삭제 */
	@PostMapping("/board/{boardIdNum}/delete")
	@PreAuthorize("isAuthenticated()")
	public String deleteBoard(@ModelAttribute("searchVO") BoardVO vo) throws Exception {
		boardService.deleteBoard(vo);
		return "redirect:/";
	}

	/** 게시글 추천 */
	@ResponseBody
	@PostMapping("/board/{boardIdNum}/recommend")
	@PreAuthorize("isAuthenticated()")
	public Map<String, Object> recommend(@ModelAttribute("searchVO") BoardVO vo) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		vo.setUserId(authentication.getName());
		// 게시글 추천 여부 확인
		int recCnt = boardService.updateBoardRecCnt(vo);
		if (recCnt == -1) {
			map.put("message", "이미 추천하셨습니다. 24시간 이후에 추천이 가능합니다.");
			return map;
		}
		map.put("recCnt", recCnt);
		return map;
	}

}
