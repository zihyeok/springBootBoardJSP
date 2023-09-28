package com.spring.boot;

import javax.sql.DataSource;


import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBootBoardJspApplication {
	
	@Autowired
	ApplicationContext applicationContext;
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootBoardJspApplication.class, args);
	}
	
	//mybatis 셋팅
	@Bean//따로 사용하지않아도 내부적으로 알아서 씀 
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception{
		
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		
		sessionFactory.setDataSource(dataSource);//의존성 주입 -servlet-context에서 해주는 작업
		
		sessionFactory.setMapperLocations(
				applicationContext.getResources("classpath:mybatis/mapper/*.xml"));
		//*를써서 여러개xml을 받아서 배열씀
		
		return sessionFactory.getObject();
		//.xml 형태의 모든 파일을 받기때문에 배열로 받는다. 
		//만약 한가지만 받으면 배열로[] 받을 필요가 없다.
	}
	
}
