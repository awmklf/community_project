package community.cmm.security;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author JJ
 * @see : 미인증 핸들러
 * 
 */
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	/* 미인증 처리 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
		HttpSession session = request.getSession();
		String referer = request.getHeader("Referer");
		
		// application/json 체크
		if (request.getHeader("Accept").contains("application/json")) {
			response.setContentType("application/json;charset=UTF-8");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

			// Jackson Object Mapper
			ObjectMapper mapper = new ObjectMapper();

			// 에러 메시지
			String json = mapper.writeValueAsString(Collections.singletonMap("message", "로그인이 필요합니다."));
			response.getWriter().write(json);
		} else {
			session.setAttribute("message", "로그인이 필요합니다.");
			response.sendRedirect(referer != null ? referer : "/");
		}

	}
}
