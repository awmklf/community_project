package community.springmvc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import community.springmvc.service.TestService;
import community.springmvc.service.TestVO;

@Service("testService")
public class TestServiceImpl implements TestService {
	
	@Autowired
	private TestDAO testDAO;

	@Override
	public TestVO selectTest(TestVO vo) throws Exception {
		return testDAO.selectTest(vo);
	}

	@Override
	public void insertTest(TestVO vo) throws Exception {
		testDAO.insertTest(vo);
	}

}
