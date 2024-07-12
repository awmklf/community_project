package community.springmvc.web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import community.springmvc.service.TestService;
import community.springmvc.service.TestVO;
import lombok.extern.slf4j.Slf4j;

/**
 * 테스트 전용 MVC 패턴 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("/test")
public class TsetController {
	
	@Autowired
	TestService testService;
	
	@RequestMapping("/select")
	public String testSelect(TestVO vo, ModelMap model) throws Exception {
		TestVO result = testService.selectTest(vo);
		model.addAttribute("result", result);
		return "test/home";
	}
	
	@RequestMapping("/log")
	public String log(TestVO vo) throws Exception {
		log.debug("#### 테스트 {}", vo.getTestVal());
		return "test/home";
	}
	
}
