package community.cmm.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * 
 * @author JJ
 * @see : 로그인 성공 핸들러
 * 
 */
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	/** 로그인 성공 처리 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication auth) throws IOException, ServletException {
		HttpSession session = req.getSession();
		if (session != null) {
			// 로그인 페이지의 이전 페이지 주소 여부 확인
			String redirectUrl = (String) session.getAttribute("prevPage"); 
			if (redirectUrl != null) {
				session.removeAttribute("prevPage");
				getRedirectStrategy().sendRedirect(req, resp, redirectUrl); // 이전 페이지로 리다이렉트
			} else {
				super.onAuthenticationSuccess(req, resp, auth);
			}
		} else {
			super.onAuthenticationSuccess(req, resp, auth);
		}
	}

}
