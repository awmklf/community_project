package community.board.web;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import community.board.service.BoardService;
import community.board.service.BoardVO;
import community.cmm.pagination.PaginationCalc;

//@Slf4j
@Controller
public class BoardController {

	/** boardService DI */
	@Autowired
	BoardService boardService;

	/** 게시글 리스트 */
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

	/** 게시글 내용 */
	@GetMapping("/board/view")
	public String boardSelect(@ModelAttribute("searchVO") BoardVO vo, ModelMap model) throws Exception {

		BoardVO result = boardService.selectBoard(vo);
		model.addAttribute("result", result);

		return "board/BoardView";
	}

	/** 게시글 등록 및 수정 폼 */
	@GetMapping("/board/post")
	public String boardRegist(@ModelAttribute("searchVO") BoardVO vo, ModelMap model) throws Exception {
		// 로그인 여부 체크
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			BoardVO result = new BoardVO();
			// 게시글 수정폼 변경 여부 체크
			if (StringUtils.hasText(vo.getBoardId())) {
				result = boardService.selectBoard(vo);
				// 게시글 작성자 또는 관리자 인증
				String currentUserId = authentication.getName();
				boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
				if (!currentUserId.equals(result.getRegisterId()) && !isAdmin) {
					model.addAttribute("message", "허용되지 않는 접근입니다.");
					return "redirect:/board/list";
				}
			}
			model.addAttribute("result", result);

		} else {
			model.addAttribute("message", "로그인 후 사용가능합니다.");
			return "redirect:/board/list";
		}
		return "board/BoardPost";
	}

	/** 게시글 등록 */
	@RequestMapping(value = "/board/insert")
	public String insert(@ModelAttribute("searchVO") BoardVO vo, HttpServletRequest request, ModelMap model) throws Exception {
		// 로그인 여부 체크
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			vo.setRegisterId(authentication.getName());
			vo.setCreatIp(request.getRemoteAddr());
			if (vo.getIsNotice() == null)
				vo.setIsNotice("N");
			String resultBoardId = boardService.insertBoard(vo);
			return "redirect:/board/view?boardId=" + resultBoardId;
		} else {
			model.addAttribute("message", "로그인 후 사용가능합니다.");
			return "redirect:/board/list";
		}
	}

}
