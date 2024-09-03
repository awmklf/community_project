package community.cmm.service;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileDAO {
	
	/** 첨부파일리스트 조회 */
	public List<FileVO> selectFileList(FileVO vo) throws Exception;
	
	/** 첨부파일 조회 */
	public FileVO selectFile(FileVO vo) throws Exception;
	
	/** 첨부파일 마지막 번호 조회 */
	public Integer selectLastFileSn(FileVO vo) throws Exception;
	
	/** 파일 등록 */
	public void insertFile(FileVO vo) throws Exception;
	
	/** 파일 수정 */
	public void updateFile(FileVO vo) throws Exception;
	
	/** 파일 상태 변경 */
	public int updateUseAt(FileVO vo) throws Exception;
}
