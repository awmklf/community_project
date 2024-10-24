package community.cmm.security;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author JJ
 * @see : 접근 거부 핸들러
 * 
 */
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	/** 접근 거부 처리 */
	@Override
	public void handle(HttpServletRequest req, HttpServletResponse resp, AccessDeniedException accessDeniedException) throws IOException, ServletException {

		// application/json 체크
		if (req.getHeader("Accept").contains("application/json")) {
			resp.setContentType("application/json;charset=UTF-8");
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

			// Jackson Object Mapper
			ObjectMapper mapper = new ObjectMapper();

			// 에러 메시지
			String json = mapper.writeValueAsString(Collections.singletonMap("message", accessDeniedException.getMessage()));
			resp.getWriter().write(json);
		} else {
			HttpSession session = req.getSession();
			session.setAttribute("message", accessDeniedException.getMessage());
			
			String referer = req.getHeader("Referer");
			resp.sendRedirect(referer != null ? referer : "/");
		}
		
	}

}
