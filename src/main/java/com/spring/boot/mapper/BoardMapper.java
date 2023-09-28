package com.spring.boot.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.spring.boot.dto.BoardDTO;


// 정의해놓은 sql과 개발할 때 사용하는 메소드를 연결하고 
// 결과 값을 정의해놓은 타입으로 매핑 시켜주는 것
@Mapper//Mapper로 등록 시킨다.
public interface BoardMapper {
	
	public int maxNum() throws Exception;
	
	public void insertData(BoardDTO dto) throws Exception;
	
	public int getDataCount(String searchKey,String searchValue) throws Exception;
	
	public List<BoardDTO> getLists(int start,int end,String searchKey,String searchValue) throws Exception;
	
	public BoardDTO getReadData(int num) throws Exception;
	
	public void updateHitCount(int num) throws Exception;
	
	public void updateData(BoardDTO dto) throws Exception;
	
	public void deleteData(int num) throws Exception;
	
}
