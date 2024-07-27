package community.cmm.pagination;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author JJ
 * @see : 페이징을 위한 공통 VO클래스
 * 
 */
@Getter
@Setter
public class ComPageVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/** 검색조건 */
    private String searchCondition = "";

    /** 검색 키워드 */
    private String searchKeyword = "";

    /** 현재페이지 */
    private int pageIndex = 1;

    /** 페이지 내 게시글 수 */
    private int pageUnit = 10;

    /** 페이지 리스트 내 페이지 개수 */
    private int pageSize = 2; // 테스트를 위해 2로 설정

    /** firstIndex(쿼리용) */
    private int firstIndex = 1;

    /** lastIndex */
    private int lastIndex = 1;

    /** recordCountPerPage(쿼리용) */
    private int recordCountPerPage = 10;

}
