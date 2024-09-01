package community.cmm.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import community.cmm.service.FileDAO;
import community.cmm.service.FileService;
import community.cmm.service.FileVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

	/** FileDAO DI */
	@Autowired
	FileDAO fileDAO;

	
	/** 업로드 파일 상태정보 수정 */
	@Override
	public String updateFileUseAt(FileVO vo) throws Exception {
		List<String> extractImageIds = extractImageIds(vo.getBoardCn()); // 작성 또는 수정 게시글 내용에 포함된 이미지 정보 리스트
		int updateUseAt = 0;
		
		// 게시글에 포함된 이미지 정보를 db에 최종등록
		for (String imageId : extractImageIds) {
			String[] imageIdParts = imageId.split("_(?=[^_]+$)");
			if (imageIdParts.length == 2) {
				log.info("게시글 내용으로부터 아이디 추출 및 분리 성공");
				vo.setAtchFileId(imageIdParts[0]);
				vo.setFileSn(Integer.parseInt(imageIdParts[1]));
				vo.setUseAt("Y");
				updateUseAt += fileDAO.updateUseAt(vo);
			}
		}
		
		vo.setUseAt("T");
		List<FileVO> resultFileList = fileDAO.selectFileList(vo); // db에 등록된 이미지 정보 리스트
		
		// db에 등록된 이미지정보와 실제 게시글에 포함된 이미지 정보 비교 후 실제 게시글에 이미지가 없으면 db 데이터 상태 변경
		for (FileVO file : resultFileList) {
			String resultImageId = file.getAtchFileId() + "_" + file.getFileSn();
			log.info("게시글에 포함되었는지 체크할 db 데이터 : {}", resultImageId);
			if (!extractImageIds.contains(resultImageId)) {
				log.info("실제 게시글에서 삭제된 이미지 : {}", file.getOrignlFileNm());
				file.setUseAt("N");
				updateUseAt += fileDAO.updateUseAt(file);
			}
		}
		
		
		log.info("update : {}", updateUseAt);
		return vo.getAtchFileId();
		
	}
	
	/** 게시글 내용으로부터 업로드된 이미지의 아이디 추출 */
	@Override
	public List<String> extractImageIds(String inputString) throws Exception {
		List<String> resultList = new ArrayList<>();
		Pattern pattern = Pattern.compile("data-image-id=\"([^\"]+)\"");
		Matcher matcher = pattern.matcher(inputString);

		while (matcher.find()) {
			resultList.add(matcher.group(1));
		}

		return resultList;
	}
	

	/** 업로드 파일 DB 등록 */
	@Override
	public Integer insertFile(FileVO vo) throws Exception {
		// 파일 아이디 생성 로직
		Integer checkFileId = fileDAO.selectLastFileSn(vo);
		int nextIdNum = 1;
		if (checkFileId != null) {
			nextIdNum += checkFileId;
		}
		vo.setFileSn(nextIdNum);
		fileDAO.insertFile(vo);

		return nextIdNum;
	}

	/** 스마트 에디터 이미지 업로드 */
	@Override
	public String uploadImage(FileVO vo) throws Exception {

		// 파일 확장자가 허용된 목록에 있는지 확인
		String[] suffixArr = { "jpg", "png", "bmp", "gif" };
		int cnt = 0;
		for (String suffix : suffixArr) {
			if (vo.getFileExtsn().equals(suffix)) {
				cnt++;
				break;
			}
		}
		if (cnt == 0) {
			return "NOTALLOW_" + vo.getOrignlFileNm(); // 허용되지 않은 파일 확장자일 경우 에러 반환
		}

		// 디렉토리가 없으면 생성
		File file = new File(vo.getFileStreCours());
		if (!file.exists()) {
			file.mkdirs();
		}

		// 새로운 파일 이름 생성
		vo.setStreFileNm(UUID.randomUUID().toString());
		String rFileName = vo.getFileStreCours() + vo.getStreFileNm();
		log.info("rFileName : {}", rFileName);

		// 파일을 저장
		try (InputStream is = vo.getInputStream(); OutputStream os = new FileOutputStream(rFileName)) {
			byte[] buffer = new byte[Integer.parseInt(vo.getFileSize())];
			int num;
			while ((num = is.read(buffer)) != -1)
				os.write(buffer, 0, num);
		}
		vo.setUseAt("T"); // 임시저장
		Integer fileSn = insertFile(vo);

		// 스마트 에디터에 전송할 파일정보
		String fileInfo = "&bNewLine=true";
		fileInfo += "&sFileName=" + vo.getOrignlFileNm();
		fileInfo += "&sFileURL=/image/" + vo.getStreFileNm();
		fileInfo += "&sImageId=" + vo.getAtchFileId() + "_" + fileSn;

		return fileInfo;
	}

}
