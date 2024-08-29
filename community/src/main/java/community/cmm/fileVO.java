package community.cmm;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class fileVO {

	/** 첨부파일ID */
	private String ATCH_FILE_ID;
	/** 첨부파일 생성일 */
	private int CREAT_DT;
	/** 삭제일시 */
	private Date DELETED_AT;
	/** 삭제여부 */
	private String IS_DELETED;
	
	/** 첨부파일 순번 */
	private int FILE_SN;
	/** 첨부파일 확장자 */
	private String FILE_EXTSN;
	/** 첨부파일 크기 */
	private String FILE_SIZE;
	/** 첨부파일 저장 경로 */
	private String FILE_STRE_COURS;
	/** 첨부파일 원본 이름 */
	private String ORIGNL_FILE_NM;
	/** 첨부파일 저장이름 */
	private Date STRE_FILE_NM;

	/** 게시글 ID번호 */
	private Integer boardIdNum;
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
	/** 덧글 수 */
	private int replyCnt;

}
