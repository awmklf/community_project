package community.cmm;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import community.cmm.service.FileDAO;
import community.cmm.service.FileVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableScheduling
public class SchedulingConfig {

	/** FileDAO DI */
	@Autowired
	FileDAO fileDAO;

	// 파일 정리 스케줄
	@Scheduled(fixedRate = 3600000) // 1시간 3600.000 간격으로 실행 
	public void cleanUpFiles() throws Exception {
		FileVO vo = new FileVO();
		vo.setUseAt("schedule");
		vo.setAtchFileId("!Y");
		log.info("UseAt: {}", vo.getUseAt());
		List<FileVO> resultList = fileDAO.selectFileList(vo);
		for (FileVO result : resultList) {
			File file = new File(result.getFileStreCours() + result.getStreFileNm());
			LocalDateTime creatDt = result.getCreatDt();
			LocalDateTime now = LocalDateTime.now();
			Duration duration = Duration.between(creatDt, now);
			
			if ("N".equals(result.getUseAt())) {
				if (duration.toHours() >= 24) {
					if (file.exists()) {
						file.delete();
						log.info("삭제 파일 존재 확인 : {}",result.getFileStreCours() + result.getStreFileNm());
					}
				}
			} else if ("T".equals(result.getUseAt())) {
				if (duration.toHours() >= 1) { // 1시간 이상 차이나는 경우
					if (file.exists()) {
						file.delete();
						log.info("임시 파일 존재 확인 : {}", result.getFileStreCours() + result.getStreFileNm());
					}
					result.setUseAt("N");
					fileDAO.updateUseAt(result);
				}
			}
		}
	}
}
