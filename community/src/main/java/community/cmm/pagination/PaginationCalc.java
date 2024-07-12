package community.cmm.pagination;

import lombok.Getter;
import lombok.Setter;

@Getter
public class PaginationCalc {
	
	@Setter
	/** 현재 페이지 번호 */
	private int currentPageNo;
	
	@Setter
	/** 페이지당 게시물 개수 */
	private int recordCountPerPage;
	
	@Setter
	/** 페이지 리스트 내 페이지 개수 */
	private int pageSize;
	
	@Setter
	/** 전체 게시물 수 */
	private int totalRecordCount;
	
	/** 전체 페이지 수 */
	private int totalPageCount;

	/** 페이지 리스트 내 첫 페이지 번호 */
	private int firstPageNoOnPageList;
	
	/** 페이지 리스트 내 마지막 페이지 번호 */
	private int lastPageNoOnPageList;
	
	/** SQL의 조건절에 사용되는 시작 rownum */
	private int firstRecordIndex;
	
	/** SQL의 조건절에 사용되는 마지막 rownum */
	private int lastRecordIndex;
	
	
	public int getTotalPageCount() {
		totalPageCount = ((getTotalRecordCount() - 1) / getRecordCountPerPage()) + 1;
		return totalPageCount;
	}
	
	public int getFirstPageNo() {
		return 1;
	}
	
	public int getLastPageNo() {
		return getTotalPageCount();
	}

	public int getFirstPageNoOnPageList() {
		firstPageNoOnPageList = ((getCurrentPageNo() - 1) / getPageSize()) * getPageSize() + 1;
		return firstPageNoOnPageList;
	}

	public int getLastPageNoOnPageList() {
		lastPageNoOnPageList = getFirstPageNoOnPageList() + getPageSize() - 1;
		if (lastPageNoOnPageList > getTotalPageCount()) {
			lastPageNoOnPageList = getTotalPageCount();
		}
		return lastPageNoOnPageList;
	}

	public int getFirstRecordIndex() {
		firstRecordIndex = (getCurrentPageNo() - 1) * getRecordCountPerPage();
		return firstRecordIndex;
	}

	public int getLastRecordIndex() {
		lastRecordIndex = getCurrentPageNo() * getRecordCountPerPage();
		return lastRecordIndex;
	}
	
	
}

