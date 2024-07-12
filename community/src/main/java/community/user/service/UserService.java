package community.user.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {

	/** 회원가입 */
	public void insert(UserVO vo) throws Exception;

	/** 회원가입 유효성 검증(id) */
	public UserVO checkId(String userId) throws Exception;

	/** 회원가입 유효성 검증(Nickname) */
	public UserVO checkNickname(String nickname) throws Exception;

	/** 로그인(스프링 시큐리티) */
	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException;

	/** 유저 정보 불러오기 */
	public UserVO selectUserInfo(UserVO vo) throws Exception;
	
	/** 비밀번호 힌트 가져오기 */
	public UserVO getPwdHint(UserVO vo) throws Exception;
	
	/** 비밀번호 힌트, 답 검증 */
	public int pwdCnsrVaild(UserVO vo) throws Exception;
	
	/** 비밀번호 변경 */
	public void changePwd(UserVO vo) throws Exception;
	
}
