package community.user.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserVO implements UserDetails {
	private static final long serialVersionUID = 1L;

	/** ID */
	private String userId;
	/** 유저 권한 */
	private String role;
	/** 닉네임 */
	private String nickname;
	/** 패스워드 */
	private String password;
	/** 패스워드 질문 */
	private String passwordHint;
	/** 패스워드 답변 */
	private String passwordCnsr;
	/** 가입일 */
	private Date registrationDate;
	/** 계정 삭제, 정지 여부 */
	private String IsDeleted;
	/** 유저 전체정보 여부 */
	private String hideSensitiveInfo;

	@Override
	public String getUsername() {
		return this.userId;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_" + this.role));
		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !this.IsDeleted.equals("Y");
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return !this.IsDeleted.equals("Y");
	}

}
