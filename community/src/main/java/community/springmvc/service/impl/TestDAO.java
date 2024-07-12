package community.springmvc.service.impl;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import community.springmvc.service.TestVO;

@Repository("testDAO")
public class TestDAO extends SqlSessionDaoSupport {

	@Override
	@Autowired
	public void setSqlSessionFactory(SqlSessionFactory sqlSession) {
		super.setSqlSessionFactory(sqlSession);
	}

	public TestVO selectTest(TestVO vo) {
		return getSqlSession().selectOne("testDAO.select", vo);
	}

	public void insertTest(TestVO vo) {
		getSqlSession().insert("testDAO.insert", vo);

	}
}
