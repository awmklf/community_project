package community.board.web;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.ResponseBody;

import community.board.service.BoardService;
import community.board.service.BoardVO;
import community.cmm.service.CommonService;
import community.cmm.service.FileService;
import community.cmm.service.FileVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class BoardController {

	/** boardService DI */
	@Autowired
	private BoardService boardService;
	
	/** commService DI */
	@Autowired
	private CommonService cmmService;
	
	/** commService DI */
	@Autowired
	private FileService fileService;
	

	/** 게시글 목록 조회 */
	@GetMapping("/board")
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
	
	/** 스마트에디터 이미지 업로드 */
	@PostMapping("/board/uploadImage")
	public void uploadImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String fileName = URLDecoder.decode(request.getHeader("file-name"), StandardCharsets.UTF_8.toString());
		String fileNameSuffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
		String fileSize = request.getHeader("file-size");
		String fileType = request.getHeader("file-Type");
		String defaultPath = request.getSession().getServletContext().getRealPath("/");
		String filePath = defaultPath + "image" + File.separator;

		FileVO vo = new FileVO();
		vo.setInputStream(request.getInputStream());
		vo.setOrignlFileNm(fileName); // 파일 이름
		vo.setFileExtsn(fileNameSuffix); // 파일 확장자
		vo.setFileSize(fileSize); // 파일 사이즈
		vo.setFileStreCours(filePath); // 파일 경로
		
		log.info("fileName : {}", vo.getOrignlFileNm());
		log.info("fileType : {}", fileType);
		log.info("fileNmSuff : {}", vo.getFileExtsn());
		log.info("fileSize : {}", vo.getFileSize());
		log.info("filePath : {}", vo.getFileStreCours());		

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("tempUniqueVal".equals(cookie.getName())) {
					vo.setAtchFileId(cookie.getValue());
					log.info("Temporary Unique Value: " + vo.getAtchFileId());
					
					String fileInfo = fileService.uploadImage(vo);
					
					// 파일 정보 반환
					response.setCharacterEncoding("UTF-8");
					response.getWriter().println(fileInfo);
					log.info("fileInfo : {}", fileInfo);

				}
			}
		}

	}

}
