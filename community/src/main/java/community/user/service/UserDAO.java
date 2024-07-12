package community.user.service;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDAO {

	/** 회원가입 */
	public void insert(UserVO vo) throws Exception;

	/** 회원가입 유효성 검증(id) */
	public UserVO checkId(String userId) throws Exception;

	/** 회원가입 유효성 검증(Nickname) */
	public UserVO checkNickname(String nickname) throws Exception;

	/** 로그인(스프링 시큐리티) */
	public UserVO login(String userId);

	/** 유저 정보 불러오기 */
	public UserVO selectUserInfo(UserVO vo) throws Exception;
	
	/** 비밀번호 질문 가져오기 */
	public UserVO getPwdHint(UserVO vo) throws Exception;
	
	/** 비밀번호 질문, 답 검증 */
	public int pwdCnsrVaild(UserVO vo) throws Exception;
	
	/** 비밀번호 변경 */
	public int changePwd(UserVO vo) throws Exception;
	
}
