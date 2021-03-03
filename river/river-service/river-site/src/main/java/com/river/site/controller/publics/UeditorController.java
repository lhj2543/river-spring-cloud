package com.river.site.controller.publics;

import com.baidu.ueditor.ActionEnter;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 用于处理关于ueditor插件相关的请求
 * @author river
 */
@CrossOrigin
@RestController
@RequestMapping("/public/ueditor")
@Api(value = "UeditorController",tags = "用于处理关于ueditor插件相关的请求")
public class UeditorController {

	Logger logger = LoggerFactory.getLogger(getClass());

	@RequestMapping(value = "/exec")
	public String exec2(HttpServletRequest request, HttpServletResponse response) {

		logger.info("ueditor插件相关的请求开始");
		try {
			request.setCharacterEncoding("utf-8");

			//String rootPath = request.getRealPath("/");
			String rootPath = request.getSession().getServletContext().getRealPath("/");
			String exec = new ActionEnter(request, rootPath).exec();
			System.err.println(exec);
			return  exec;

		} catch(Exception e) {
			logger.error("ueditor插件相关的请求开始异常",e);
			e.printStackTrace();
		}
		logger.info("ueditor插件相关的请求开始结束");

		return "ueditor插件相关的请求开始异常";
	}

}
