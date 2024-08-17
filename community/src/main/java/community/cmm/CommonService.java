package community.cmm;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author JJ
 * @see : 공통으로 사용하는 로직
 *
 */
@Slf4j
@Service
public class CommonService {

	/** 게시글 번호 치환 */
	public String convertNumToBoardId(int boardIdNum) throws Exception {
		return String.format("BOARD_%09d", boardIdNum);
	}

	/** 접근 권한 확인 */
	@PreAuthorize("isAuthenticated()")
	public String roleChk(String registerId) throws AccessDeniedException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
		boolean isManager = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));
		boolean isOwner = authentication.getName().equals(registerId);
		log.debug("Register ID : {}",registerId);
		log.debug("isAdmin : {}, isManager : {}, isOwner : {}",isAdmin, isManager, isOwner);
		if (isAdmin)
			return "admin";
		else if (isManager) {
			if (isOwner)
				return "manager-owner";
			return "manager";
		} else if (isOwner)
			return "owner";
		else
			throw new AccessDeniedException("접근 권한이 없습니다. 관리자 또는 본인만 접근 가능합니다.");
	}
}
