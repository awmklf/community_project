package community.reply.service;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import community.cmm.pagination.ComPageVO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReplyVO extends ComPageVO {
	private static final long serialVersionUID = 1L;
	
	/** 덧글ID */
	private String replyId;
	/** 부모덧글ID */
	private String parentReplyId;
	/** 게시글ID */
	private String boardId;
	/** 작성자ID */
	private String registerId;
	/** 덧글내용 */
	private String replyCn;
	/** 생성IP */
	private String creatIp;
	/** 사용여부 */
	private String useAt;
	/** 작성일 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss",  timezone = "Asia/Seoul")
	private Date frstRegistPnttm;
	/** 수정일 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss",  timezone = "Asia/Seoul")
	private Date lastUpdtPnttm;

	/** 게시글 ID번호 */
	private Integer boardIdNum;
	/** 덧글 작성자 닉네임 */
	private String nickname;
	/** 부모덧글 작성자 닉네임 */
	private String parentNickname;
	/** 자식덧글 여부 */
	private String hasChildRep;
	/** 덧글 계층 */
	private int depth;
	/** 실제 덧글 수 */
	private int replyListCnt;
	/** 표시 덧글 수 */
	private int replyViewCnt;
	/** 현재 유저 아이디 */
	private String userId;
	/** 현재 유저 아이피 */
	private String userIp;
	/** 관리자 여부 */
	private String mngAt;
}
