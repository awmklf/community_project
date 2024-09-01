package community.cmm.web;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import community.user.service.UserVO;

@Controller
public class HeaderFooterController {
	@RequestMapping(value = "/header")
	public String header(ModelMap model, HttpSession session) throws Exception {

		// 로그인 여부 확인
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			UserVO user = (UserVO) authentication.getPrincipal();
			session.setAttribute("userId", user.getUsername());
			session.setAttribute("role", user.getRole());
			session.setAttribute("nickname", user.getNickname());
		}
		return "cmm/Header";
	}

	@RequestMapping(value = "/footer")
	public String footer() throws Exception {
		return "cmm/Footer";
	}

	/** 스마트 에디터를 위한 시큐리티 csrf 발행 */
	@ResponseBody
	@GetMapping("/csrf-token")
	public Map<String, String> getCsrfToken(HttpServletRequest request) throws Exception {
		Map<String, String> tokenMap = new HashMap<>();
		CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
		tokenMap.put("token", csrfToken.getToken());
		tokenMap.put("headerName", csrfToken.getHeaderName());
		return tokenMap;
	}

	/** 이미지 업로드를 위한 임시아이디 */
	@ResponseBody
	@GetMapping("/temp-id")
	public HashMap<String, Object> getTempImageId() throws Exception {
		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("tempImageId", UUID.randomUUID().toString());

		return map;
	}

}
