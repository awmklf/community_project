package community.cmm.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import community.cmm.service.FileDAO;
import community.cmm.service.FileService;
import community.cmm.service.FileVO;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

	/** FileDAO DI */
	@Autowired
	FileDAO fileDAO;
	
	
	/** 업로드 파일 DB 등록 */
	@Override
	public Integer insertFile(FileVO vo) throws Exception {
		// 파일 순번 생성 로직
		Integer checkFileId = fileDAO.selectLastFileSn(vo);
		Integer nextIdNum = 1;
		if (checkFileId != null) {
			nextIdNum += checkFileId;
		}
		vo.setFileSn(nextIdNum.toString());
		fileDAO.insertFile(vo);

		return nextIdNum;
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

	
	/** 업로드 파일 DB정보 갱신 */
	@Override
	public String updateFileUseAt(FileVO vo) throws Exception {
		List<String> extractImageIds = extractImageIds(vo.getBoardCn()); // 작성 또는 수정 게시글 내용에 포함된 이미지 정보 리스트
		int updateUseAt = 0;
		
		// 게시글에 포함된 이미지 상태정보를 DB에 최종 갱신 및 썸네일 생성
		boolean isFirst = true;
		for (String imageId : extractImageIds) {
			String[] imageIdParts = imageId.split("_(?=[^_]+$)");
			if (imageIdParts.length == 2) {
				log.info("게시글 내용으로부터 아이디 추출 및 분리 성공");
				vo.setAtchFileId(imageIdParts[0]);
				vo.setFileSn(imageIdParts[1]);
				
				// 임시 상태의 이미지를 사용으로 갱신
				if (!"N".equals(vo.getUseAtBoard())) { // 게시글이 삭제되지 않은 경우
					vo.setUseAt("Y");
					updateUseAt += fileDAO.updateUseAt(vo);
					
					// 썸네일 생성 작업
					if (isFirst) { // 글내용의 첫 이미지인 경우에만 동작
						log.info("썸네일 작업 시작");
						log.info("썸네일 작업을 위한 vo 정보", vo.toString());
						FileVO thumbnail = thumbnail(vo); // 썸네일 생성
						updateThumbnail(thumbnail); // 썸네일 정보 등록 or 갱신
					}
				}
				isFirst = false;
			}
		}
		
		// 썸네일 삭제
		if (isFirst || "N".equals(vo.getUseAtBoard())) { // 게시글 내용에 이미지가 없거나 게시글이 삭제된 경우
			log.info("썸네일 제거 체크 : {}", vo.getAtchFileId());
			if (StringUtils.hasText(vo.getAtchFileId())) {
				vo.setFileSn("0");
				FileVO resultFile = fileDAO.selectFile(vo); // 썸네일 정보 불러오기
				if (resultFile != null) {
					log.info("썸네일 제거 시작");
					File file = new File(resultFile.getFileStreCours() + resultFile.getStreFileNm()+ "." + resultFile.getFileExtsn());
					if (file.exists()) {
						file.delete();
					}
					resultFile.setUseAt("N");
					log.info("썸네일 삭제 : {}", resultFile.getFileStreCours() + resultFile.getStreFileNm());
					fileDAO.updateUseAt(resultFile);
				}
			}
		}
		
		vo.setUseAt("!N");
		vo.setFileSn("");
		List<FileVO> resultFileList = fileDAO.selectFileList(vo); // db에 등록된 이미지 정보 리스트
		
		// db에 등록된 이미지정보와 실제 게시글에 포함된 이미지 정보 비교 후 실제 게시글에 이미지가 없으면 이미지 상태 변경
		for (FileVO file : resultFileList) {
			String resultImageId = file.getAtchFileId() + "_" + file.getFileSn();
			log.info("게시글에 포함되었는지 체크할 db 데이터 : {}", resultImageId);
			if (!extractImageIds.contains(resultImageId) || "N".equals(vo.getUseAtBoard())) { //게시글에서 이미지를 삭제한 경우 or 게시글을 삭제한 경우
				log.info("실제 게시글에서 삭제된 이미지 : {}", file.getOrignlFileNm());
				file.setUseAt("N");
				updateUseAt += fileDAO.updateUseAt(file);
			}
		}
		
		log.info("update : {}", updateUseAt);
		return vo.getAtchFileId();
	}
	
	
	
	
	/** 썸네일 작업 */
	@Override
	public FileVO thumbnail(FileVO vo) throws Exception {
		log.info("썸네일 작업 시작");
		
		// 썸네일로 만들 이미지 정보 가져오기
		FileVO preFile = fileDAO.selectFile(vo);
		
		log.info("썸네일 원본 이미지 정보 가져오기 완료");

		// 썸네일 생성할 폴더, 파일명 세팅
		vo.setFileStreCours(preFile.getFileStreCours() + "thumbnail/");
		File file = new File(vo.getFileStreCours());
		if (!file.exists()) {
			file.mkdirs();
		}
		vo.setStreFileNm(preFile.getStreFileNm() + "_thumb");
		
		log.info("썸네일 폴더 및 파일명 세팅 완료");
		
		// 썸네일 생성
		String imageThumbnail = vo.getFileStreCours() + vo.getStreFileNm()  + "." + preFile.getFileExtsn();
		File originalImage = new File(preFile.getFileStreCours() + preFile.getStreFileNm());
		
		BufferedImage readImage = ImageIO.read(originalImage);
		int width = readImage.getWidth();
		int height = readImage.getHeight();
		
		if (width <= 600 && height <= 600) {
			log.info("원본 이미지 크기 600x600 미만");
			Thumbnails.of(originalImage)
			.size(120, 120)
			.keepAspectRatio(true)
			.toFile(new File(imageThumbnail));
			
		} else {
			log.info("원본 이미지 크기 600x600 이상");
			Thumbnails.of(originalImage)
			.sourceRegion(Positions.CENTER, 600, 600)
			.size(120, 120)
			.keepAspectRatio(true)
			.toFile(new File(imageThumbnail));
		}

		log.info("썸네일 생성 완료");
		
		// 생성된 썸네일 파일크기, 확장자 가져오기
		File thumbnailFile = new File(imageThumbnail);
		String length = Long.toString(thumbnailFile.length());
		vo.setFileSize(length);
		String fileName = thumbnailFile.getName();
		String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
		vo.setFileExtsn(fileExtension);
		
		
		// 기존 썸네일 존재여부 체크
		vo.setFileSn("0");
		vo.setUseAt("any");
		log.info("썸네일 정보등록을 위한 vo 정보 {}", vo.toString());
		
		
		return vo;
	}
	
	/** 썸네일 DB 정보 갱신 */
	@Override
	public void updateThumbnail (FileVO vo) throws Exception {
		FileVO resultFile = fileDAO.selectFile(vo);
		log.info("썸네일 정보 셋 완료");
		vo.setUseAt("T");
		// 썸네일 변경(기존썸네일 디스크에서 제거) or 등록 
		if (resultFile != null && StringUtils.hasText(vo.getStreFileNm())) {
			if (!resultFile.getStreFileNm().equals(vo.getStreFileNm())) { // 기존 썸네일과 새로 생성된 썸네일이 같지 않으면 기존 썸네일 삭제
				log.info("썸네일 정보 갱신");
				File oldThumnail = new File(resultFile.getFileStreCours() + resultFile.getStreFileNm()+ "." + resultFile.getFileExtsn());
				// 기존썸네일 삭제
				if (oldThumnail.exists()) {
					oldThumnail.delete();
					log.info("기존 썸네일 삭제 완료 : {}", resultFile.getFileStreCours() + resultFile.getStreFileNm());
				}
				// 새로운 썸네일 DB 정보 갱신
				fileDAO.updateFile(vo);
				log.info("썸네일 정보 갱신 완료");
			}
		} else { // 신규로 썸네일 DB 정보 등록
			fileDAO.insertFile(vo);
			log.info("썸네일 정보 등록 완료");
		}
		
		// 썸네일 상태 정보 갱신
		vo.setUseAt("Y");
		fileDAO.updateUseAt(vo); //썸네일 useAt 갱신
		log.info("썸네일 상태 정보 갱신 완료 {}", vo.getUseAt());
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
		
		// DB 파일정보 삽입(임시파일)
		vo.setUseAt("T");
		Integer fileSn = insertFile(vo);

		// 스마트 에디터에 전송할 파일정보
		String fileInfo = "&bNewLine=true";
		fileInfo += "&sFileName=" + vo.getOrignlFileNm();
		fileInfo += "&sFileURL=/image/" + vo.getStreFileNm();
		fileInfo += "&sImageId=" + vo.getAtchFileId() + "_" + fileSn;

		return fileInfo;
	}

}
