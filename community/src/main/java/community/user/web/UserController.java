package community.user.web;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import community.user.service.UserService;
import community.user.service.UserVO;

/**
 * 
 * @author JJ
 *
 */
@Controller
public class UserController {

	/** userService DI */
	@Autowired
	private UserService userService;
	

	/** 회원가입 폼 */
	@GetMapping("/user/signup")
	public String userAddForm() throws Exception {
		return "user/SignUp";
	}

	/** 회원가입 제출 */
	@PostMapping("/user/signup")
	public String userAdd(UserVO vo, HttpSession session) throws Exception {
		if (StringUtils.hasText(vo.getUserId())
				&& StringUtils.hasText(vo.getNickname())
				&& StringUtils.hasText(vo.getPassword())
				&& StringUtils.hasText(vo.getPasswordHint())
				&& StringUtils.hasText(vo.getPasswordCnsr())) {
			boolean idValidate = 5 <= vo.getUserId().length() && vo.getUserId().length() <= 20 && vo.getUserId().matches("^[a-zA-Z0-9]*$") ? true : false;
			boolean nickValidate = 2 <= vo.getNickname().length() && vo.getNickname().length() <= 10 && vo.getNickname().matches("^[가-힣a-zA-Z0-9]*$") ? true : false;
			boolean pwdValidate = 8 <= vo.getPassword().length() && !Pattern.compile("(.)\\1{3,}").matcher(vo.getPassword()).find() ? true : false;
			
			if (idValidate && nickValidate && pwdValidate) {
				userService.insert(vo);
				session.setAttribute("message", "회원가입이 완료되었습니다. 로그인을 해주세요.");
				
			} else session.setAttribute("message", "잘못된 접근으로 회원가입을 완료하지 못했습니다.");
			
		} else session.setAttribute("message", "잘못된 접근으로 회원가입을 완료하지 못했습니다.");
		return "redirect:/";
	}

	/** 아이디 유효성 체크 */
	@ResponseBody
	@PostMapping("/user/idCheck")
	public Map<String, String> idCheck(UserVO vo) throws Exception {

		Map<String, String> map = new HashMap<String, String>();
		// 필드 빈칸 확인
		if (StringUtils.hasText(vo.getUserId())) {

			// 길이 및 영문, 숫자 확인
			if (5 <= vo.getUserId().length() && vo.getUserId().length() <= 20 && vo.getUserId().matches("^[a-zA-Z0-9]*$")) {
				UserVO checkId = userService.checkId(vo.getUserId());

				// 중복확인
				if (checkId == null) {
					map.put("idStatus", "green"); // 사용가능
					
				} else map.put("idStatus", "dupl"); // 중복된 아이디
				
			} else map.put("idStatus", "valid"); // 5~20자리 알파벳, 숫자 조건 미충족
			
		} else map.put("idStatus", "null"); // 아이디 미입력
		
		return map;
	}

	/** 닉네임 유효성 체크 */
	@ResponseBody
	@PostMapping("/user/nicknameCheck")
	public Map<String, String> nicknameCheck(UserVO vo) throws Exception {

		Map<String, String> map = new HashMap<String, String>();
		// 필드 빈칸 확인
		if (StringUtils.hasText(vo.getNickname())) {

			// 길이 및 한글, 영문, 숫자 확인
			if (2 <= vo.getNickname().length() && vo.getNickname().length() <= 10 && vo.getNickname().matches("^[가-힣a-zA-Z0-9]*$")) {
				UserVO checkNickname = userService.checkNickname(vo.getNickname());

				// 중복확인
				if (checkNickname == null) {
					map.put("nicknameStatus", "green"); // 사용가능
					
				} else map.put("nicknameStatus", "dupl"); // 중복된 닉네임

			} else map.put("nicknameStatus", "valid"); // 2~10자의 한글, 영문, 숫자 조건 미충족
		
		} else map.put("nicknameStatus", "null"); // 닉네임 미입력

		return map;
	}
	
	/** 비밀번호 유효성 체크 */
	@ResponseBody
	@PostMapping("/user/pwdCheck")
	public Map<String, String> pwdCheck(UserVO vo) throws Exception {

		Map<String, String> map = new HashMap<String, String>();
		// 필드 빈칸 확인
		if (StringUtils.hasText(vo.getPassword())) {

			Pattern pattern = Pattern.compile("(.)\\1{3,}");
			Matcher matcher = pattern.matcher(vo.getPassword());
			
			// 길이 및 같은 문자, 숫자 연속 사용 확인
			if (8 <= vo.getPassword().length() && !matcher.find()) {
				map.put("pwdStatus", "green"); // 사용가능

			} else map.put("pwdStatus", "valid"); // 8자리 이상 조건 미충족 또는 연속된 동일 문자 4자 이상 사용
		
		} else map.put("pwdStatus", "null"); // 비밀번호를 입력

		return map;
	}
	
	/** 비밀번호 질문, 답변 유효성 체크 */
	@ResponseBody
	@PostMapping("/user/pwdQnACheck")
	public Map<String, String> pwdQnACheck(UserVO vo) throws Exception {

		Map<String, String> map = new HashMap<String, String>();
		// 비밀번호 질문 빈칸 확인
		if (StringUtils.hasText(vo.getPasswordHint())) {
			map.put("pwdHintStatus", "green"); // 사용가능
		
		} else map.put("pwdHintStatus", "null"); //  비밀번호 질문 미입력
		
		// 비밀번호 답변 빈칸 확인
		if (StringUtils.hasText(vo.getPasswordCnsr())) {
			map.put("pwdCnsrStatus", "green"); // 사용가능
		
		} else map.put("pwdCnsrStatus", "null"); //  비밀번호 질문 미입력

		return map;
	}
	

	/** 로그인 */
	@GetMapping("/user/login")
	public String loginForm(HttpServletRequest request, ModelMap model) throws Exception {
		return "user/Login";
	}

	/** 로그아웃 */
	@PostMapping("/user/logout")
	public void logout() throws Exception {
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
		if (StringUtils.hasText(vo.getUserId())) { // 아이디 검증
			UserVO pwdHint = userService.getPwdHint(vo);
			if (pwdHint != null && StringUtils.hasText(pwdHint.getPasswordHint())) { // 비밀번호 질문 검증
				String passwordHint = pwdHint.getPasswordHint();
				map.put("passwordHint", passwordHint); // 비밀번호 질문
			}
		} else map.put("blankField", "null"); // 아이디 미입력
		return map;
	}
	
	/** 비밀번호 답 검증 */
	@ResponseBody
	@PostMapping("/user/getPwdCnsr")
	public Map<String, String> getPwdCnsr(UserVO vo, HttpServletRequest request, ModelMap model) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		
		if (StringUtils.hasText(vo.getUserId()) && StringUtils.hasText(vo.getPasswordHint())) { // 아이디, 비밀번호 질문 검증
			if (StringUtils.hasText(vo.getPasswordCnsr())) { // 비밀번호 답 검증
				int pwdCnsrVaild = userService.pwdCnsrVaild(vo);
				if (pwdCnsrVaild == 1) { // 비밀번호 답 검증
					map.put("check", "Y");
				} else map.put("check", "N");
			} else map.put("check", "null");
		}
		return map;
	}
	
	/** 비밀번호 변경 */
	@PostMapping("/user/changePwd")
	public String changePwd(@RequestParam String allowPwdChange, UserVO vo, HttpSession session) throws Exception {
		if (allowPwdChange.equals("Y") && StringUtils.hasText(vo.getUserId()) && StringUtils.hasText(vo.getPassword())) {
			UserVO checkId = userService.checkId(vo.getUserId());
			if (checkId.getUserId().equals(vo.getUserId())) {
				userService.changePwd(vo);
				session.setAttribute("message", "패스워드 변경이 완료되었습니다.");
				return "redirect:/";
			}
		}
		session.setAttribute("message", "잘못된 접근입니다.");
		return "redirect:/";
	}

}
