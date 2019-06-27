package com.jt.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import redis.clients.jedis.JedisCluster;

@Component //将拦截器交给spring管理
public class UserInterceptor implements HandlerInterceptor {
	/**
	 * 返回值结果：
	 * true：拦截放心
	 * false：请求拦截，重定向到登录页面
	 * 业务逻辑：
	 * 1.获取Cookie数据
	 * 2.从cookie中获取token（）
	 */
	@Autowired
	private JedisCluster jedisCluster;
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//1.获取cookie信息
		String token = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
				if("JT_TICKET".equals(cookie.getName())) {
					token = cookie.getValue();
					break;
				}
		}
		//2.判断token是否有效
		if(!StringUtils.isEmpty(token)) {
			//4.判断redis中师傅有数据
			String userJSON = jedisCluster.get(token);
			if(!StringUtils.isEmpty(userJSON)) {
				//redis中有用户数据
				return true;
			}
		}
		//3.重定向到用户登录页面
		response.sendRedirect("/user/login.html");
		return false; //表示拦截
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}

}
