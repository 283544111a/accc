package com.jt.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jt.pojo.User;
import com.jt.service.DubboUserService;
import com.jt.service.UserService;
import com.jt.vo.SysResult;

import redis.clients.jedis.JedisCluster;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private JedisCluster jedisCluster;
	
	//导入dubbo的用户接口
	@Reference(timeout=3000,check=false)
	private DubboUserService userService;
	
	/*
	 * @Autowired private UserService userService;
	 */
	
	@RequestMapping("/{moduleName}")
	public String index(@PathVariable String moduleName) {
		
		return moduleName;
	}
	
	//使用dubbo形式实现业务调用
	@RequestMapping("/doRegister")
	@ResponseBody
	public SysResult saveUser(User user) {
		try {
			userService.saveUser(user);
			return SysResult.ok();
		} catch (Exception e) {
			e.printStackTrace();
			return SysResult.fail();
		}
	}
	
	/**
	 * 	实现用户登录
	 * 
	 * 说明:利用Response对象将Cookie数据写入客户端
	 * cookie生命周期:
	 * 	cookie.setMaxAge(0);  立即删除
	 *  cookie.setMaxAge(值>0) 能够存活多久  单位/秒
	 *  cookie.setMaxAge(-1)     当会话结束后删除
	 * 
	 * cookie的权限 一般都写"/"
	 * cookie.setPath("/");    
	 * 
	 *    页面位置: 
	 *    www.jd.com/abc/a.html
	 * 	  www.jd.com/b.html   		
	 * @param user
	 * @return
	 */
	@RequestMapping("/doLogin")
	@ResponseBody
	public SysResult login(User user,HttpServletResponse response) {
		try {
			//调用sso系统获取秘钥
			String token = 
					userService.findUserByUP(user);
			//如果数据不为null时,将数据保存到Cookie中
			//cookie中的key固定值 JT_TICKET
			if(!StringUtils.isEmpty(token)) {
				Cookie cookie = 
						new Cookie("JT_TICKET", token);
				cookie.setMaxAge(7*24*3600);//生命周期
				cookie.setDomain("jt.com"); //实现数据共享
				cookie.setPath("/");
				response.addCookie(cookie);
				return SysResult.ok();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return SysResult.fail();
	}
	
	/**
	 * 实现用户登出操作
	 * 1.删除redis  request对象~~cookie中~~~JT_TICKET~~key
	 * 2.删除cookie
	 * @return
	 */
	
	@RequestMapping("/logout")
	public String logout(HttpServletRequest request,HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		if(cookies.length !=0) {
			String token = null;
			for (Cookie cookie : cookies) {
				if("JT_TICKET".equals(cookie.getName())) {
					token = cookie.getValue();
					break;
				}
			}
			
			//判断token数据有值  删除redis/删除cookie
			if(!StringUtils.isEmpty(token)) {
				jedisCluster.del(token);
				Cookie cookie = new Cookie("JT_TICKET","");
				cookie.setMaxAge(0);//立即删除cookie
				cookie.setPath("/");
				cookie.setDomain("jt.com");
				response.addCookie(cookie);
			}
		}
		//当用户登出时,页面重定向到系统首页
		return "redirect:/";
	}
	
	
	
	
	
	
	
	
	
	
	
}
