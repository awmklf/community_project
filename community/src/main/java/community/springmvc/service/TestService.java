package community.springmvc.service;

public interface TestService {

	/** 데이터 선택(불러오기) */
	public TestVO selectTest(TestVO vo) throws Exception;
	
	/** 데이터 삽입 */
	public void insertTest(TestVO vo) throws Exception;
}
