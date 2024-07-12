package community.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import community.user.service.UserDAO;
import community.user.service.UserService;
import community.user.service.UserVO;

@Service("userService")
public class UserServiceImpl implements UserService {

	/**
	 * userDAO DI 
	 */
	@Autowired
	private UserDAO userDAO;
	
	/**
	 * bCryptPasswordEncoder DI
	 */
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	/** 회원가입 */
	@Override
	public void insert(UserVO vo) throws Exception {
		String encodePwd = bCryptPasswordEncoder.encode(vo.getPassword());
		vo.setPassword(encodePwd);
		userDAO.insert(vo);
	}

	/** 회원가입 유효성 검증(id) */
	@Override
	public UserVO checkId(String userId) throws Exception {
		return userDAO.checkId(userId);
	}

	/** 회원가입 유효성 검증(Nickname) */
	@Override
	public UserVO checkNickname(String nickname) throws Exception {
		return userDAO.checkNickname(nickname);
	}

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
	
	/** 비밀번호 질문 가져오기 */
	@Override
	public UserVO getPwdHint(UserVO vo) throws Exception {
		return userDAO.getPwdHint(vo);
	}
	
	/** 비밀번호 질문, 답 검증 */
	@Override
	public int pwdCnsrVaild(UserVO vo) throws Exception {
		return userDAO.pwdCnsrVaild(vo);
	}
	
	/** 비밀번호 변경 */
	@Override
	public void changePwd(UserVO vo) throws Exception {
		String encodePwd = bCryptPasswordEncoder.encode(vo.getPassword());
		vo.setPassword(encodePwd);
		userDAO.changePwd(vo);
	}

}
