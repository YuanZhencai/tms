/**
 * MyShrioCasFilter.java
 * Created: 2012-1-11 下午03:23:55
 */
package com.wcs.base.service;

import java.io.IOException;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import com.wcs.common.filenet.env.Configuration;
import com.wcs.common.filenet.env.EnvInit;
import com.wcs.common.filenet.pe.PEManager;

/**
 * <p>
 * Project: btcbase
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright (c) 2012 Wilmar Consultancy Services
 * </p>
 * <p>
 * All Rights Reserved.
 * </p>
 * 
 * @author <a href="mailto:chengchao@wcs-global.com">ChengChao</a>
 */
public class BaseShrioCasFilter implements Filter {

	private static Log logger = LogFactory.getLog(BaseShrioCasFilter.class);

	public static final String NOT_APP_USER_ERROR_PAGE = "/tms/error.jsp";

	private static final String DEFAULT_PASSWORD = "";

	@EJB
	private LoginService loginService;

	@Inject
	private EnvInit envInit;
	@Inject
	private PEManager peManager;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		// modify by liushengbin
		// 当前环境指向的配置文件(如：生产：prd_config；开发测试：fn_config)，改成应用启动时赋值
		// 详见ConfigListener.java
		logger.debug("当前所调用系统包的fn配置文件名（生产：prd_config；开发测试：fn_config）:"
				+ Configuration.BUNDLE_NAME);
		logger.debug("当前所调用fn url:" + Configuration.getParameter("CE.CEURL"));
		logger.debug("客户端ip:" + request.getRemoteHost());

		logger.debug(BaseShrioCasFilter.class.getName() + " doFilter："
				+ ((HttpServletRequest) request).getUserPrincipal());
		Object userPrincipal = ((HttpServletRequest) request)
				.getUserPrincipal();
		String userId = request.getParameter("userAccount");
		String op = request.getParameter("op");
		HttpSession session = ((HttpServletRequest) request).getSession(true);
		if (userPrincipal != null && userId == null) {
			String userId1 = userPrincipal == null ? null : userPrincipal
					.toString();
			if (session.getAttribute(LoginService.SESSION_KEY_CURRENTUSR) == null
					&& userId1 != null) {
				session.setAttribute(LoginService.SESSION_KEY_CURRENTUSR,
						doLogin(userId1, response));
				//add by liushengbin 2014-06-18 把用户的sapCode放入session
				setCurrentSapCode();
				
				envInit.initEnv(session);
				peManager.initPE();

			}
		} else {
			if ((op != null && "log".equals(op) && userId != null)
					|| (session
							.getAttribute(LoginService.SESSION_KEY_CURRENTUSR) == null && userId != null)) {
				session.setAttribute(LoginService.SESSION_KEY_CURRENTUSR,
						doLogin(userId, response));
				//add by liushengbin 2014-06-18 把用户的sapCode放入session
				setCurrentSapCode();
				if (!"".equals(userId) && (!"shenbo".equals(userId))) {
					envInit.logoffPE(session);
					envInit.initEnv(session);
					peManager.initPE();
				}
			}
		}
		logger.debug("session:" + session.getId() + ",currentUser is "
				+ session.getAttribute(LoginService.SESSION_KEY_CURRENTUSR));
		filterChain.doFilter(request, response);
	}
	
	private void setCurrentSapCode(){
		//add by liushengbin 2014-06-18 把用户的sapCode放入session
		Subject currentUser = SecurityUtils.getSubject();
		currentUser.getSession().setAttribute(LoginService.SESSION_KEY_SAPCODE, loginService.findSapCodeByAccount(""));
	}

	private Subject doLogin(String userId, ServletResponse response)
			throws IOException {
		try {
			Subject currentUser = SecurityUtils.getSubject();
			// not login
			if (userId == null) {
				logger.debug(BaseShrioCasFilter.class.getName()
						+ " currentUser.logout()");
				currentUser.logout();
				return null;
			} else {
				logger.debug("MyShrioCasFilter currentUser.login(" + userId
						+ ")");
				currentUser.login(new UsernamePasswordToken(userId,
						DEFAULT_PASSWORD));
				// judge is btcbase user?
				if (loginService.isAppUser(userId)) {
					logger.debug(userId + " is validated user for the app!");
					return currentUser;
				} else {
					logger.debug(userId + " is invalidated user for the app!");
					((HttpServletResponse) response)
							.sendRedirect(NOT_APP_USER_ERROR_PAGE);
					return null;
				}
			}
		} catch (UnknownAccountException uae) {
			logger.error("doLogin方法 认证出现异常", uae);
		} catch (IncorrectCredentialsException ice) {
			logger.error("doLogin方法 认证出现异常", ice);
		} catch (LockedAccountException lae) {
			logger.error("doLogin方法 认证出现异常", lae);
		} catch (ExcessiveAttemptsException eae) {
			logger.error("doLogin方法 认证出现异常", eae);
		} catch (AuthenticationException ae) {
			logger.error("doLogin方法 认证出现异常", ae);
		}
		return null;
	}

	/**
	 * 注销
	 */
	private void logout() {
		try {
			Subject currentUser = SecurityUtils.getSubject();
			currentUser.logout();
			logger.debug(BaseShrioCasFilter.class.getName()
					+ " currentUser.logout()");
		} catch (Exception e) {
			return;
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

}
