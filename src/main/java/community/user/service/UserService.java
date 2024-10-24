package community.user.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {

	/** 회원가입 */
	public boolean createAccount(UserVO vo) throws Exception;

	/** 회원가입 유효성 검증(id) */
	public String checkId(UserVO vo) throws Exception;

	/** 회원가입 유효성 검증(Nickname) */
	public String checkNickname(UserVO vo) throws Exception;

	/** 회원가입 유효성 검증(Password) */
	public String checkPassword(UserVO vo) throws Exception;

	/** 회원가입 유효성 검증(Password QnA) */
	public String checkPwdQuestion(UserVO vo) throws Exception;
	public String checkPwdAnswer(UserVO vo) throws Exception;

	/** 로그인(스프링 시큐리티) */
	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException;

	/** 유저 정보 불러오기 */
	public UserVO selectUserInfo(UserVO vo) throws Exception;

	/** 비밀번호 힌트 가져오기 */
	public String getPwdHint(UserVO vo) throws Exception;

	/** 비밀번호 힌트, 답 검증 */
	public String checkPwdCnsr(UserVO vo) throws Exception;

	/** 비밀번호 변경 */
	public String changePwd(UserVO vo) throws Exception;

}
