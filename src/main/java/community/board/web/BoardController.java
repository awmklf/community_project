package community.board.web;

import java.io.BufferedInputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import community.board.service.BoardService;
import community.board.service.BoardVO;
import community.cmm.MimeTypeMapper;
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

	/** mimeTypeMapper DI */
	@Autowired
	private MimeTypeMapper mimeTypeMapper;

	@Value("${Globals.ImagePath}")
	private String uploadDir;

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
	public String boardSelect(@ModelAttribute("searchVO") BoardVO vo, HttpServletRequest request, ModelMap model) throws Exception {
		// 조회수 어뷰징 방지를 위한 유저 ID, IP 정보 담기
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser"))
			vo.setUserId(auth.getName()); // 아이디 저장(로그인 유저의 경우)
		vo.setUserIp(request.getRemoteAddr()); // 아이피 저장
		
		vo.setTriggerViewCntUp("Y"); // 조회수 증가 트리거

		BoardVO result = boardService.selectBoard(vo);

		// 존재하지 않는 게시글의 경우
		if (result == null)
			return "cmm/error/404";

		model.addAttribute("result", result);
		return "board/BoardView";
	}

	/** 게시글 작성 및 수정 폼 */
	@GetMapping({ "/board/write", "/board/{boardIdNum}/edit" })
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
	public String insert(@ModelAttribute("searchVO") BoardVO vo, HttpServletRequest request, HttpSession session) throws Exception {
		if (!StringUtils.hasText(vo.getBoardSj().trim()) || !StringUtils.hasText(vo.getBoardCn().trim())) {
			session.setAttribute("message", "게시글 작성중 문제가 발생하였습니다.");
			return "redirect:/";
		}
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

	/** 게시글 상태 변경 */
	@PostMapping("/board/{boardIdNum}/update-status")
	@PreAuthorize("isAuthenticated()")
	public String updateStatus(@ModelAttribute("searchVO") BoardVO vo, HttpServletRequest req) throws Exception {
		boardService.udtStatusBoard(vo);
		return "redirect:" + req.getHeader("Referer");
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

	/** 이미지 업로드를 위한 임시아이디 */
	@ResponseBody
	@GetMapping("/temp-id")
	public HashMap<String, Object> getTempImageId() throws Exception {
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("tempImageId", UUID.randomUUID().toString());

		return map;
	}

	/** 스마트에디터 멀티 이미지 업로드 */
	@PostMapping("/board/uploadImage")
	public void uploadImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 파일 정보 가져오기
		String fileFullName = URLDecoder.decode(request.getHeader("file-name"), StandardCharsets.UTF_8.toString());
		String fileName = fileFullName.substring(0, fileFullName.lastIndexOf("."));
		String fileSize = request.getHeader("file-size");
		String filePath = uploadDir;
		BufferedInputStream bufferedInputStream = new BufferedInputStream(request.getInputStream());
		bufferedInputStream.mark(Integer.MAX_VALUE); // 스트림 유지를 위한 마킹
		Tika tika = new Tika();
		String detectMIME = tika.detect(bufferedInputStream); // MIME 타입 확인
		String extension = mimeTypeMapper.getExtension(detectMIME); // MIME에 따른 확장자 셋
		bufferedInputStream.reset(); // 스트림 마킹지점 재설정

		// vo에 담기
		FileVO vo = new FileVO();
		vo.setInputStream(bufferedInputStream);
		vo.setOrignlFileNm(fileName); // 파일 이름
		vo.setFileExtsn(extension); // 파일 확장자
		vo.setFileSize(fileSize); // 파일 사이즈
		vo.setFileStreCours(filePath); // 파일 경로

		// 이미지 업로드 진행
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("tempUniqueVal".equals(cookie.getName())) {

					// 이미지 사전 업로드 파일 아이디를 위한 쿠키
					vo.setAtchFileId(cookie.getValue());
					log.info("Temporary FileId: " + vo.getAtchFileId());

					String fileInfo = fileService.uploadImage(vo); // 업로드

					// 파일 정보 반환
					response.setCharacterEncoding("UTF-8");
					response.getWriter().println(fileInfo);
					log.info("fileInfo : {}", fileInfo);

				}
			}
		}

	}

	/** 스마트에디터 싱글 이미지 업로드 */
	@ResponseBody
	@PostMapping("/board/uploadSingleImage")
	public Map<String, String> handleFileUpload(FileVO vo, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		HashMap<String, String> map = new HashMap<String, String>();

		// 파일이 존재하면
		if (!vo.getFile().isEmpty()) {
			
			// 파일 정보 가져오기
			String fileFullName = vo.getFile().getOriginalFilename(); // 파일이름
			String fileName = fileFullName.substring(0, fileFullName.lastIndexOf("."));
			Long fileSize = vo.getFile().getSize();
			String filePath = uploadDir;
			BufferedInputStream bufferedInputStream = new BufferedInputStream(vo.getFile().getInputStream());
			bufferedInputStream.mark(Integer.MAX_VALUE); // 스트림 유지를 위한 마킹
			Tika tika = new Tika();
			String detectMIME = tika.detect(bufferedInputStream); // MIME 타입 확인
			String extension = mimeTypeMapper.getExtension(detectMIME); // MIME에 따른 확장자 셋
			bufferedInputStream.reset(); // 스트림 마킹지점 재설정

			// vo에 담기
			vo.setInputStream(bufferedInputStream);
			vo.setOrignlFileNm(fileName); // 파일 이름
			vo.setFileExtsn(extension); // 파일 확장자
			vo.setFileSize(fileSize.toString()); // 파일 사이즈
			vo.setFileStreCours(filePath); // 파일 경로

			// 이미지 업로드 진행
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if ("tempUniqueVal".equals(cookie.getName())) {

						// 이미지 사전 업로드 파일 아이디를 위한 쿠키
						vo.setAtchFileId(cookie.getValue());
						log.info("Temporary FileId: " + vo.getAtchFileId());

						String fileInfo = fileService.uploadImage(vo); // 업로드

						// 파일 정보 반환
						log.info("fileInfo : {}", fileInfo);
						map.put("responseText", fileInfo);
						return map;
					}
				}
			}
		}
		map.put("e", "업로드 중 오류가 발생했습니다.");
		return map;
	}
}
