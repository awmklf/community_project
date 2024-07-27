package community.cmm.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
/**
 * 
 * @author JJ
 * @see : 로그인 성공 핸들러
 * 
 */
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	/** 로그인 성공 처리 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
	}
	

}
