package community.user.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import community.user.service.UserService;
import community.user.service.UserVO;

@Controller
public class UserController {

	/** userService DI */
	@Autowired
	private UserService userService;

	/** 회원가입 폼 */
	@GetMapping("/user/signup")
	public String createAccountForm() throws Exception {
		return "user/SignUp";
	}

	/** 회원가입 제출 */
	@PostMapping("/user/signup")
	public String createAccount(UserVO vo, HttpSession session) throws Exception {

		boolean result = userService.createAccount(vo);
		if (result)
			session.setAttribute("message", "회원가입이 완료되었습니다. 로그인을 해주세요.");
		else
			session.setAttribute("message", "유효하지 않습니다.");
		return "redirect:/";
	}

	/** 아이디 유효성 체크 */
	@ResponseBody
	@PostMapping("/user/checkId")
	public Map<String, String> checkId(UserVO vo) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("idStatus", userService.checkId(vo));
		return map;
	}

	/** 닉네임 유효성 체크 */
	@ResponseBody
	@PostMapping("/user/checkNickname")
	public Map<String, String> checkNickname(UserVO vo) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("nicknameStatus", userService.checkNickname(vo)); // 닉네임 미입력
		return map;
	}

	/** 비밀번호 유효성 체크 */
	@ResponseBody
	@PostMapping("/user/checkPwd")
	public Map<String, String> checkPassword(UserVO vo) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("pwdStatus", userService.checkPassword(vo)); // 비밀번호를 입력
		return map;
	}

	/** 비밀번호 질문, 답변 유효성 체크 */
	@ResponseBody
	@PostMapping("/user/checkPwdQnA")
	public Map<String, String> checkpwdQnA(UserVO vo) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		// 비밀번호 질문
		map.put("pwdHintStatus", userService.checkPwdQuestion(vo));
		// 비밀번호 답변 빈칸 확인
		map.put("pwdCnsrStatus", userService.checkPwdAnswer(vo)); // 사용가능
		return map;
	}

	/** 로그인 */
	@GetMapping("/user/login")
	public String loginForm(HttpServletRequest request, ModelMap model) throws Exception {
		String referrer = request.getHeader("Referer");
		request.getSession().setAttribute("prevPage", referrer);
		return "user/Login";
	}

	/** 로그아웃 */
	@PostMapping("/user/logout")
	public void logout() throws Exception {
	}
	
	/** 세션 연장 */
	@GetMapping("/keep-alive")
    public void keepAlive() {
    }

	/** 비밀번호 찾기 페이지 */
	@GetMapping("/user/recoverPwd")
	public String recoverPwd(HttpServletRequest request, ModelMap model) throws Exception {
		return "user/RecoverPwd";
	}

	/** 비밀번호 질문 가져오기 */
	@ResponseBody
	@PostMapping(value = "/user/getPwdHint", produces = "application/json; charset=UTF-8")
	public Map<String, String> getPwdHint(UserVO vo, HttpServletRequest request, ModelMap model) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("passwordHint", userService.getPwdHint(vo));
		return map;
	}

	/** 비밀번호 답 검증 */
	@ResponseBody
	@PostMapping("/user/getPwdCnsr")
	public Map<String, String> getPwdCnsr(UserVO vo, HttpServletRequest request, ModelMap model) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("check", userService.checkPwdCnsr(vo));
		return map;
	}

	/** 비밀번호 변경 */
	@PostMapping("/user/changePwd")
	public String changePwd(@RequestParam String allowPwdChange, UserVO vo, HttpSession session) throws Exception {
		if ("Y".equals(allowPwdChange)) {
			String changePwd = userService.changePwd(vo);
			session.setAttribute("message", changePwd);
			return "redirect:/";
		}
		session.setAttribute("message", "잘못된 접근입니다.");
		return "redirect:/";
	}

}
