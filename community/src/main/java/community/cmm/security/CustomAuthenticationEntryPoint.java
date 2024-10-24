package community.cmm.security;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
	public void commence(HttpServletRequest req, HttpServletResponse resp, AuthenticationException authException) throws IOException, ServletException {

		// application/json 체크
		if (req.getHeader("Accept").contains("application/json")) {
			resp.setContentType("application/json;charset=UTF-8");
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

			// Jackson Object Mapper
			ObjectMapper mapper = new ObjectMapper();

			// 에러 메시지
			String json = mapper.writeValueAsString(Collections.singletonMap("message", "로그인이 필요합니다."));
			resp.getWriter().write(json);
		} else {
			HttpSession session = req.getSession();
			session.setAttribute("message", "로그인이 필요합니다.");
			String currentUrl = req.getRequestURL().toString();
			String queryString = req.getQueryString();
			try {
				URI uri = new URI(currentUrl);
				String path = uri.getPath();
				String parentPath = path.substring(0, path.lastIndexOf('/'));
				if (queryString != null) {
					parentPath += "?" + queryString;
				}
				resp.sendRedirect(parentPath);
			} catch (URISyntaxException e) {
				e.printStackTrace();
				resp.sendRedirect("/");
			}
		}

	}
}
