package community.board.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import community.board.service.BoardService;
import community.board.service.BoardVO;
import community.cmm.pagination.PaginationCalc;

//@Slf4j
@Controller
public class BoardController {

	/** boardService DI */
	@Autowired
	BoardService boardService;

	/** 게시글 목록 조회 */
	@RequestMapping("/board/list")
	public String boardSelectList(@ModelAttribute("searchVO") BoardVO vo, HttpServletRequest req, ModelMap model) throws Exception {
		// 공지 영역 글
		vo.setIsNotice("Y");
		List<BoardVO> noticeResultList = boardService.selectBoardList(vo);
		model.addAttribute("noticeResultList", noticeResultList);

		// 페이징
		PaginationCalc pgCalc = new PaginationCalc();
		pgCalc.setCurrentPageNo(vo.getPageIndex());
		pgCalc.setRecordCountPerPage(vo.getPageUnit());
		pgCalc.setPageSize(vo.getPageSize());

		vo.setFirstIndex(pgCalc.getFirstRecordIndex());
		vo.setLastIndex(pgCalc.getLastRecordIndex());
		vo.setRecordCountPerPage(pgCalc.getRecordCountPerPage());

		// 일반 게시글
		vo.setIsNotice("N");
		List<BoardVO> resultList = boardService.selectBoardList(vo);
		model.addAttribute("resultList", resultList);

		// 페이징
		int totCnt = boardService.selectBoardListCnt(vo);
		pgCalc.setTotalRecordCount(totCnt);
		model.addAttribute("paginationInfo", pgCalc);

		// 현재날짜
		Date currentDate = new Date();
		model.addAttribute("currentDate", currentDate);

		return "board/BoardSelectList";
	}

	/** 게시글 내용 조회 */
	@GetMapping("/board/view")
	public String boardSelect(@ModelAttribute("searchVO") BoardVO vo, HttpServletRequest request, ModelMap model, HttpSession session) throws Exception {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		vo.setUserId(authentication.getName());
		vo.setUserIp(request.getRemoteAddr());
		
		// 비밀글 확인
		vo.setTriggerViewCntUp("N");
		if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
				|| authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
			vo.setMngAt("Y");
		} else {
			vo.setMngAt("N");
		}
		
		if (boardService.othbcChk(vo)) {
			session.setAttribute("message", "게시글 작성자 또는 관리자만 확인 가능합니다.");
			String referer = request.getHeader("Referer");
			return "redirect:"+ (referer != null ? referer : "/");
		}
		
		vo.setTriggerViewCntUp("Y");  // 조회수 증가 허용
		BoardVO result = boardService.selectBoard(vo);
		model.addAttribute("result", result);

		return "board/BoardView";
	}

	/** 게시글 등록 및 수정 폼 */
	@GetMapping("/board/post")
	public String boardRegist(@ModelAttribute("searchVO") BoardVO vo, ModelMap model, HttpSession session) throws Exception {
		// 로그인 여부 체크
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof AnonymousAuthenticationToken) {
			session.setAttribute("message", "로그인 후 사용가능합니다.");
			return "redirect:/";
		}
		BoardVO result = new BoardVO();
		// 게시글 작성 or 수정폼 체크
		if (StringUtils.hasText(vo.getBoardId())) {
			// 게시글 작성자 또는 관리자 인증
			result = boardService.selectBoard(vo);
			String currentUserId = authentication.getName();
			boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
			if (!currentUserId.equals(result.getRegisterId()) && !isAdmin) {
				session.setAttribute("message", "허용되지 않는 접근입니다.");
				return "/user/login";
			}
			result = boardService.selectBoard(vo);
		}
		model.addAttribute("result", result);
		
		return "board/BoardPost";
	}

	/** 게시글 등록 */
	@PostMapping("/board/insert")
	public String insert(@ModelAttribute("searchVO") BoardVO vo, HttpServletRequest request, HttpSession session) throws Exception {
		// 로그인 여부 체크
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof AnonymousAuthenticationToken) {
			session.setAttribute("message", "로그인 후 사용가능합니다.");
			return "/user/login";
		}
		vo.setRegisterId(authentication.getName());
		vo.setCreatIp(request.getRemoteAddr());
		if (vo.getIsNotice() == null)
			vo.setIsNotice("N");
		String resultBoardId = boardService.insertBoard(vo);
		return "redirect:/board/view?boardId=" + resultBoardId;
	}
	
	/** 게시글 수정 */
	@PostMapping("/board/update")
	public String update(@ModelAttribute("searchVO") BoardVO vo, HttpServletRequest request, HttpSession session) throws Exception {
		// 로그인 여부 체크
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof AnonymousAuthenticationToken) {
			session.setAttribute("message", "로그인 후 사용가능합니다.");
			return "redirect:/";
		}
		// 게시글 작성자 or 관리자 인증
		if (StringUtils.hasText(vo.getBoardId())) {
			String currentUserId = authentication.getName();
			boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
			if (isAdmin) {
				vo.setMngAt("Y");
			} else if (currentUserId.equals(vo.getRegisterId())) {
				vo.setUserId(currentUserId);
			} else {
				session.setAttribute("message", "허용되지 않는 접근입니다.");
				return "redirect:/";
			}
			boardService.updateBoard(vo);
		}
		
		return "redirect:/board/view?boardId=" + vo.getBoardId();
	}
	
	
	/** 게시글 삭제 */
	@RequestMapping("/board/delete")
	public String deleteBoard(@ModelAttribute("searchVO") BoardVO vo, HttpServletRequest request, HttpSession session) throws Exception {
		// 로그인 여부 체크
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof AnonymousAuthenticationToken) {
			session.setAttribute("message", "로그인 후 사용가능합니다.");
			return "redirect:/";
		}
		// 게시글 작성자 or 관리자 인증
		if (StringUtils.hasText(vo.getBoardId())) {
			String currentUserId = authentication.getName();
			boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
			boolean isManager = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));
			if (isAdmin || isManager) {
				vo.setMngAt("Y");
			} else if (currentUserId.equals(vo.getRegisterId())) {
				vo.setUserId(currentUserId);
			} else {
				session.setAttribute("message", "허용되지 않는 접근입니다.");
				return "redirect:/";
			}
			boardService.deleteBoard(vo);
		}
		
		return "redirect:/";
	}
	
	/** 게시글 추천 */
	@ResponseBody
	@PostMapping("/board/recommend")
	public Map<String, Object> recommend(@ModelAttribute("searchVO") BoardVO vo, HttpServletRequest request, HttpSession session) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		// 로그인 여부 체크
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof AnonymousAuthenticationToken) {
			map.put("message", "로그인 후 이용가능합니다.");
			return map;
		}
		// 게시글 추천 여부 확인
		vo.setUserId(authentication.getName());
		int recCnt = boardService.updateBoardRecCnt(vo);
		if (recCnt == -1) {
			map.put("message", "이미 추천하셨습니다. 24시간 이후에 추천이 가능합니다.");
			return map;
		}
		
		map.put("recCnt", recCnt);
		return map;
	}

}
