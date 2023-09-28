package com.spring.boot.controller;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.spring.boot.dto.BoardDTO;
import com.spring.boot.service.BoardService;
import com.spring.boot.util.MyUtil;

@Controller
public class BoardController {

	@Resource
	private BoardService boardService;//얘를 호출하면 BoardServiceImpl이 딸려들어옴

	@Autowired
	MyUtil myUtil; //@Service로 구현된 MyUtil을 불러온것
	
	
	//@RequestMapping(value = "/")
	@GetMapping("/")
	public ModelAndView index() throws Exception{

		ModelAndView mav = new ModelAndView();

		mav.setViewName("index");
		//jsp(html)로 갈때는 setViewName /class로 갈때는 setView
		
		return mav;

	}

	//@RequestMapping(value = "/created.action",method = RequestMethod.GET)
	@GetMapping("/created.action")
	public ModelAndView created() throws Exception{

		ModelAndView mav = new ModelAndView();

		mav.setViewName("bbs/created");
		 //jsp(html)로 갈때는 setViewName /class로 갈때는 setView
		
		return mav;
	}
	
	//@RequestMapping(value = "/created.action",method = RequestMethod.POST)
	@PostMapping("/created.action")
	public ModelAndView created_ok(BoardDTO dto,HttpServletRequest request) throws Exception{

		ModelAndView mav = new ModelAndView();
		
		int maxNum = boardService.maxNum();
		
		dto.setNum(maxNum+1);
		dto.setIpAddr(request.getRemoteAddr());
		
		boardService.insertData(dto);

		mav.setViewName("redirect:/list.action");

		return mav;
	}
	
	//@RequestMapping(value = "/list.action",method = {RequestMethod.GET,RequestMethod.POST})
	@GetMapping("/list.action")
	public ModelAndView list(HttpServletRequest request) throws Exception{
		
		
		String pageNum = request.getParameter("pageNum");//문자만 따온건가?
		
		int currentPage = 1;
		
		if(pageNum!=null) {
			currentPage = Integer.parseInt(pageNum);
		}
		
		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");
		
		if(searchValue==null) {
			searchKey = "subject";
			searchValue = "";
		}else {
			if(request.getMethod().equalsIgnoreCase("GET")) {
				searchValue = URLDecoder.decode(searchValue,"utf-8");
			}
		}
		
		int dataCount = boardService.getDataCount(searchKey, searchValue);
		
		int numPerPage = 5;
		int totalPage = myUtil.getPageCount(numPerPage, dataCount);
		
		if(currentPage>totalPage) {
			currentPage=totalPage;
		}
		
		int start = (currentPage-1)*numPerPage+1;
		int end = currentPage*numPerPage;
		
		List<BoardDTO> lists = boardService.getLists(start, end, searchKey, searchValue);
		
		String param = "";
		if(searchValue!=null&&!searchValue.equals("")) {
			param = "searchKey=" + searchKey;
			param+= "&searchValue=" + URLEncoder.encode(searchValue,"utf-8");
		}
		
		String listUrl = "/list.action";
		if(!param.equals("")) {
			
			listUrl += "?" + param;
		}
		
		String pageIndexList = 
				myUtil.pageIndexList(currentPage, totalPage, listUrl);
		
		String articleUrl = "/article.action?pageNum=" + currentPage;
		if(!param.equals("")) {
			articleUrl += "&" + param;
		}
		
		//ModelAndView로 전송
		ModelAndView mav = new ModelAndView();
		
		//포워딩할 데이터
		mav.addObject("lists", lists);
		mav.addObject("pageIndexList", pageIndexList);
		mav.addObject("dataCount", dataCount);
		mav.addObject("articleUrl", articleUrl);
		//mav.addObject("pageNum", currentPage);//3번째 방법시 같이넘겨야함
		

		mav.setViewName("bbs/list");

		return mav;
	}
	
	//@RequestMapping(value = "/article.action",method = {RequestMethod.GET,RequestMethod.POST})
	@GetMapping("/article.action")
	public ModelAndView article(HttpServletRequest request) throws Exception{
	
		int num = Integer.parseInt(request.getParameter("num"));
		String pageNum = request.getParameter("pageNum");
		
		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");
		
		if(searchValue!=null) {
			searchValue = URLDecoder.decode(searchValue,"utf-8");
		}
		
		boardService.updateHitCount(num);
		
		BoardDTO dto = boardService.getReadData(num);
		
		if(dto==null) {
			
			ModelAndView mav = new ModelAndView();
			mav.setViewName("redirect:list.action?pageNum=" + pageNum);
			
			return mav;
		}
		
		int lineSu = dto.getContent().split("\n").length;
		
		//dto.setContent(dto.getContent().replaceAll("\r\n", "<br/>"));
		
		String param = "pageNum=" + pageNum;
		
		if(searchValue!=null && !searchValue.equals("")) {
			param += "&searchKey=" + searchKey;
			param += "&searchValue=" + URLEncoder.encode(searchValue, "utf-8");
		}
		
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("dto", dto);
		mav.addObject("params", param);
		mav.addObject("lineSu", lineSu);
		mav.addObject("pageNum", pageNum);
		
		mav.setViewName("bbs/article");
		
		return mav;
	}
	
	//@RequestMapping(value = "/updated.action",method = {RequestMethod.GET,RequestMethod.POST})
	@GetMapping("/updated.action")
	public ModelAndView updated(HttpServletRequest request) throws Exception{
	
		int num = Integer.parseInt(request.getParameter("num"));
		String pageNum = request.getParameter("pageNum");
		
		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");
		
		if(searchValue!=null) {
			searchValue = URLDecoder.decode(searchValue,"utf-8");
		}
	
		BoardDTO dto = boardService.getReadData(num);
		
		if(dto==null) {
			ModelAndView mav = new ModelAndView();
			mav.setViewName("redirect:list.action?pageNum=" + pageNum);
			
			return mav;
		}
		
		String param = "pageNum=" + pageNum;
		
		if(searchValue!=null && !searchValue.equals("")) {
			param += "&searchKey=" + searchKey;
			param += "&searchValue=" + URLEncoder.encode(searchValue, "utf-8");
		}
		
		ModelAndView mav = new ModelAndView();
		
		
		mav.addObject("dto", dto);
		mav.addObject("pageNum", pageNum);
		mav.addObject("params", param);
		mav.addObject("searchKey", searchKey);
		mav.addObject("searchValue", searchValue);
		
		mav.setViewName("bbs/updated");
		
		return mav;
	}
	
	@RequestMapping(value = "/updated_ok.action",method = {RequestMethod.GET,RequestMethod.POST})
	//@PostMapping("/updated_ok.action")
	public ModelAndView updated_ok(BoardDTO dto,HttpServletRequest request) throws Exception{
	
		String pageNum = request.getParameter("pageNum");
		
		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");
		
		if(searchValue!=null) {
			searchValue = URLDecoder.decode(searchValue,"utf-8");
		}
		
		boardService.updateData(dto);
		
		String param = "pageNum=" + pageNum;
		
		if(searchValue!=null && !searchValue.equals("")) {
			param += "&searchKey=" + searchKey;
			param += "&searchValue=" + URLEncoder.encode(searchValue, "utf-8");
		}
		
		ModelAndView mav = new ModelAndView();
		
		mav.setViewName("redirect:/list.action?" + param);
		
		return mav;
	}
	
	//@RequestMapping(value = "/deleted_ok.action",method = {RequestMethod.GET,RequestMethod.POST})
	@GetMapping("/deleted_ok.action")
	public ModelAndView deleted_ok(HttpServletRequest request) throws Exception{
		
		int num = Integer.parseInt(request.getParameter("num"));
		String pageNum = request.getParameter("pageNum");
		
		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");
		
		if(searchValue!=null) {
			searchValue = URLDecoder.decode(searchValue,"utf-8");
		}
		
		boardService.deleteData(num);
		
		String param = "pageNum=" + pageNum;
		
		if(searchValue!=null && !searchValue.equals("")) {
			param += "&searchKey=" + searchKey;
			param += "&searchValue=" + URLEncoder.encode(searchValue, "utf-8");
		}
		
		ModelAndView mav = new ModelAndView();
		
		mav.setViewName("redirect:/list.action?" + param);
		
		return mav;
	}
		
}
