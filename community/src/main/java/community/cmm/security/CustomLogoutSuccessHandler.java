package community.cmm.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * 
 * @author JJ
 * @see : 로그아웃 성공 핸들러
 * 
 */
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

	/** 로그아웃 성공 처리 */
	@Override
	public void onLogoutSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication auth) throws IOException, ServletException {
		HttpSession session = req.getSession(false);
		if (session !=null) {
			session.invalidate();
		}
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("JSESSIONID")) {
					cookie.setMaxAge(0);
					resp.addCookie(cookie);
				}
			}
		}
		String prevPage = req.getHeader("Referer");
		if (prevPage != null) {
			// 로그아웃 페이지의 이전 페이지 주소 여부 확인
			resp.sendRedirect(prevPage); // 이전 페이지로 리다이렉트
		} else {
			resp.sendRedirect("/");
		}
	}
}
