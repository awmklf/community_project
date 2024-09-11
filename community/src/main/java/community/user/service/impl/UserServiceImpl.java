package community.user.service.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import community.user.service.UserDAO;
import community.user.service.UserService;
import community.user.service.UserVO;

@Service("userService")
public class UserServiceImpl implements UserService {

	/* userDAO DI */
	@Autowired
	private UserDAO userDAO;

	/** bCryptPasswordEncoder DI */
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	/** 로그인(스프링 시큐리티) */
	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

		UserVO userInfo = userDAO.login(userId);
		if (userInfo == null) {
			throw new UsernameNotFoundException("사용자 정보를 찾을 수 없음");
		}
		return userInfo;
	}

	/** 유저 정보 불러오기 */
	@Override
	public UserVO selectUserInfo(UserVO vo) throws Exception {
		return userDAO.selectUserInfo(vo);
	}

	/** 회원가입 제출 */
	@Override
	public boolean createAccount(UserVO vo) throws Exception {
		String checkId = checkId(vo);
		String checkNickname = checkNickname(vo);
		String checkPassword = checkPassword(vo);
		String checkPwdQuestion = checkPwdQuestion(vo);
		String checkPwdAnswer = checkPwdAnswer(vo);
		if ("green".equals(checkId) && "green".equals(checkNickname) && "green".equals(checkPassword) 
				&& "green".equals(checkPwdQuestion) && "green".equals(checkPwdAnswer)) {
			String encodePwd = bCryptPasswordEncoder.encode(vo.getPassword());
			vo.setPassword(encodePwd);
			userDAO.insert(vo);
			return true;
		}
		return false;
	}

	/** 회원가입 유효성 검증(id) */
	@Override
	public String checkId(UserVO vo) throws Exception {
		if (StringUtils.hasText(vo.getUserId())) {
			// 5~20자의 영문 소문자, 숫자 확인
			Pattern pattern = Pattern.compile("^[a-z0-9]{5,20}$");
		    Matcher matcher = pattern.matcher(vo.getUserId());
			if (matcher.find()) {
				UserVO checkId = userDAO.checkId(vo);
				// 중복확인
				if (checkId == null && !vo.getUserId().equals("anonymousUser")) {
					return "green"; // 사용가능
				} else
					return "dupl"; // 중복된 아이디
			} else
				return "valid"; // 5~20자리 알파벳, 숫자 조건 미충족
		} else
			return "null"; // 아이디 미입력
	}

	/** 회원가입 유효성 검증(Nickname) */
	@Override
	public String checkNickname(UserVO vo) throws Exception {
		if (StringUtils.hasText(vo.getNickname())) {
			// 2~10자의 한글, 영문, 숫자 확인
			 Pattern pattern = Pattern.compile("^[가-힣a-zA-Z0-9]{2,10}$");
		     Matcher matcher = pattern.matcher(vo.getNickname());
			if (matcher.find()) {
				UserVO checkNickname = userDAO.checkNickname(vo);
				// 중복확인
				if (checkNickname == null) {
					return "green"; // 사용가능
				} else
					return "dupl"; // 중복된 닉네임
			} else
				return "valid"; // 2~10자의 한글, 영문, 숫자 조건 미충족
		} else
			return "null"; // 닉네임 미입력
	}

	/** 회원가입 유효성 검증(Password) */
	@Override
	public String checkPassword(UserVO vo) throws Exception {
		// 필드 빈칸 확인
		if (StringUtils.hasText(vo.getPassword())) {
			// 8자 이상이면서 영문, 숫자, 특수문자 중 두 종류 이상 포함된 문자열, 동일한 문자가 4개 미만
			Pattern pattern = Pattern.compile("^(?!.*(.)\\1{3,})((?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])|(?=.*[a-zA-Z])(?=.*\\d)|(?=.*[!@#$%^*+=-])(?=.*\\d)|(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*\\d))");
			Matcher matcher = pattern.matcher(vo.getPassword());
			if (matcher.find() && 8 <= vo.getPassword().length()) {
				return "green"; // 사용가능
			} else
				return "valid"; // 8자리 이상 조건 미충족 또는 연속된 동일 문자 4자 이상 사용
		} else
			return "null"; // 비밀번호를 입력
	}
	
	/** 회원가입 유효성 검증(Password Question) */
	@Override
	public String checkPwdQuestion(UserVO vo) throws Exception {
		// 비밀번호 질문 빈칸 확인
		if (StringUtils.hasText(vo.getPasswordHint())) {
			return "green"; // 사용가능
		} else
			return "null";  // 비밀번호 질문 미입력
	}
	
	/** 회원가입 유효성 검증(Password Answer) */
	@Override
	public String checkPwdAnswer(UserVO vo) throws Exception {
		// 비밀번호 답변 빈칸 확인
		if (StringUtils.hasText(vo.getPasswordCnsr())) {
			return "green"; // 사용가능
		} else
			return "null"; // 비밀번호 답변 미입력
	}

	/** 비밀번호 질문 가져오기 */
	@Override
	public String getPwdHint(UserVO vo) throws Exception {
		if (StringUtils.hasText(vo.getUserId())) { // 아이디 검증
			UserVO pwdHint = userDAO.getPwdHint(vo);
			if (pwdHint != null && StringUtils.hasText(pwdHint.getPasswordHint())) { // 비밀번호 질문 검증
				return pwdHint.getPasswordHint(); // 비밀번호 질문
			} else
				return "null"; // 조회결과 없음
		} else
			return "blank"; // 아이디 미입력
	}

	/** 비밀번호 질문, 답 검증 */
	@Override
	public String checkPwdCnsr(UserVO vo) throws Exception {
		if (StringUtils.hasText(vo.getUserId()) 
				&& StringUtils.hasText(vo.getPasswordHint()) 
				&& StringUtils.hasText(vo.getPasswordCnsr())) { // 아이디, 비밀번호 질문, 비밀번호 답 검증
			// 비밀번호 답 검증
			int pwdCnsrVaild = userDAO.pwdCnsrVaild(vo);
			if (pwdCnsrVaild == 1) {
				return "Y";
			} else
				return "N";
		} else
			return "null";
	}

	/** 비밀번호 변경 */
	@Override
	public String changePwd(UserVO vo) throws Exception {
		if (StringUtils.hasText(vo.getUserId()) && StringUtils.hasText(vo.getPassword())) {
			UserVO checkId = userDAO.checkId(vo);
			if (checkId != null && vo.getUserId().equals(checkId.getUserId())) {
				String encodePwd = bCryptPasswordEncoder.encode(vo.getPassword());
				vo.setPassword(encodePwd);
				userDAO.changePwd(vo);
				return "패스워드 변경이 완료되었습니다.";
			}
		}
		return "잘못된 접근입니다.";
	}
}
