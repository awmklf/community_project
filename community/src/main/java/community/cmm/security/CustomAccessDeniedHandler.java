package community.cmm.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * 
 * @author JJ
 * @see : 접근 거부 핸들러
 * 
 */
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	/** 접근 거부 처리 */
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

		HttpSession session = request.getSession();
		session.setAttribute("message", accessDeniedException.getMessage());

		String referer = request.getHeader("Referer");
		response.sendRedirect(referer != null ? referer : "/");
	}

}
