package community.cmm.pagination;

import lombok.Getter;
import lombok.Setter;

@Getter
public class PaginationCalc {

	/** 현재 페이지 번호 */
	@Setter
	private int currentPageNo;

	/** 페이지당 게시물 개수 */
	@Setter
	private int recordCountPerPage;

	/** 페이지 리스트 내 페이지 개수 */
	@Setter
	private int pageSize;

	/** 전체 게시물 수 */
	@Setter
	private int totalRecordCount;

	/** 전체 페이지 수 */
	private int totalPageCount;

	/** 페이지 리스트 내 첫 페이지 번호 */
	private int firstPageNoOnPageList;

	/** 페이지 리스트 내 마지막 페이지 번호 */
	private int lastPageNoOnPageList;

	/** 이전 페이지 */
	private int prevPage;

	/** 다음 페이지 */
	private int nextPage;

	/** 쿼리 페이지 시작 rownum */
	private int firstRecordIndex;

	/** 쿼리 페이지 마지막 rownum */
	private int lastRecordIndex;
	
	/** 실제 덧글 수 (덧글에서사용) */
	@Setter
	private int replyListCnt;

	/** 맨 처음 페이지 */
	public int getFirstPageNo() {
		return 1;
	}

	/** 맨 마지막 페이지 */
	public int getTotalPageCount() {
		totalPageCount = ((getTotalRecordCount() - 1) / getRecordCountPerPage()) + 1;
		return totalPageCount;
	}

	// 현재 페이지의 첫 페이지 번호
	public int getFirstPageNoOnPageList() {
		firstPageNoOnPageList = ((getCurrentPageNo() - 1) / getPageSize()) * getPageSize() + 1;
		return firstPageNoOnPageList;
	}

	// 현재 페이지의 마지막 페이지 번호
	public int getLastPageNoOnPageList() {
		lastPageNoOnPageList = getFirstPageNoOnPageList() + getPageSize() - 1;
		if (lastPageNoOnPageList > getTotalPageCount())
			return getTotalPageCount();
		return lastPageNoOnPageList;
	}

	/** 이전 페이지 */
	public int getPrevPage() {
		prevPage = getFirstPageNoOnPageList() - 1;
		if (prevPage < 1)
			return 1;
		return prevPage;
	}

	/** 다음 페이지 */
	public int getNextPage() {
		nextPage = getLastPageNoOnPageList() + 1;
		if (nextPage > getTotalPageCount())
			return getTotalPageCount();
		return nextPage;
	}

	/** 쿼리 시작 rownum */
	public int getFirstRecordIndex() {
		firstRecordIndex = (getCurrentPageNo() - 1) * getRecordCountPerPage();
		return firstRecordIndex;
	}

	/** 쿼리 마지막 rownum */
	public int getLastRecordIndex() {
		lastRecordIndex = getCurrentPageNo() * getRecordCountPerPage();
		return lastRecordIndex;
	}

}
