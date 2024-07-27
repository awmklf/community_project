package community.board.service;

import java.io.Serializable;
import java.util.Date;

import community.cmm.pagination.ComPageVO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardVO extends ComPageVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/** 게시글 ID */
	private String boardId;
	/** 게시글 카테고리 */
	private int category;
	/** 게시글 제목 */
	private String boardSj;
	/** 게시글 내용 */
	private String boardCn;
	/** 게시글 조회수 */
	private int inqireCo;
	/** 게시글 작성 IP */
	private String creatIp;
	/** 게시글 비밀글 여부 */
	private String othbcAt;
	/** 게시글 사용(Soft delete) 여부 */
	private String useAt;
	/** 게시글 첨부파일ID */
	private String atchFileId;
	/** 게시글 작성일 */
	private Date frstRegistPnttm;
	/** 게시글 작성자 */
	private String registerId;
	/** 게시글 수정일 */
	private Date lastUpdtPnttm;
	/** 게시글 추천수 */
	private int recommendCnt;
	/** 게시글 공지영역 등록여부 */
	private String isNotice;

	/** 게시글 작성자 닉네임 */
	private String nickname;
	/** 현재 유저 아이디 */
	private String userId;
	/** 현재 유저 아이피 */
	private String userIp;
	/** 관리자 여부 */
	private String mngAt;
	/** 조회수 증가 여부 */
	private String triggerViewCntUp;
	/** 유저의 게시글 추천 수 */
	private int numRecommendations;

	public void setCreatIp(String creatIp) {
		this.creatIp = creatIp;
	}

}
