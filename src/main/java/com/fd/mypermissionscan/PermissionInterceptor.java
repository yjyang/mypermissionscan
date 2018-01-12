package com.fd.mypermissionscan;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 权限验证
 * 
 * @author 符冬
 *
 */
public class PermissionInterceptor extends HandlerInterceptorAdapter {

	public static ResourceBundle INIT_RB;
	static {
		if (ClassHelper.getDefaultClassLoader().getResource("init.properties") != null) {
			INIT_RB = ResourceBundle.getBundle("init");
		}
	}
	public static final String LOGIN_USER = "LOGIN_USER";

	/**
	 * 登录 如果是POST请求登录之后返回到request.getHeader("Referer")地址，如果是get请求就返回request.
	 * getRequestURL() + "?" + request.getQueryString()到这个地址，其他情况返回到basePath
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (handler.getClass() != HandlerMethod.class) {
			String basePath = getBasePath(request);
			LoginInfo login = (LoginInfo) request.getSession().getAttribute(LOGIN_USER);
			if (login == null) {
				return tologin(request, response, basePath);
			}
		} else {
			HandlerMethod hm = (HandlerMethod) handler;
			Class<?> clazz = hm.getBeanType();
			if (clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(RestController.class)) {
				String basePath = getBasePath(request);
				LoginInfo login = (LoginInfo) request.getSession().getAttribute(LOGIN_USER);
				Permissions perm = hm.getMethodAnnotation(Permissions.class);
				ValidLogin vl = hm.getMethodAnnotation(ValidLogin.class);
				if (vl == null) {
					vl = clazz.getAnnotation(ValidLogin.class);
				}
				if (auto) {
					if (login == null) {
						if (vl == null || vl.value().equals(ValidPropagation.REQUIRED)) {
							return tologin(request, response, basePath);
						}
					} else if (perm != null) {
						return isAllow(clazz, hm, login, perm);
					}
				} else {
					if (perm != null) {
						if (login == null) {
							return tologin(request, response, basePath);
						} else {
							return isAllow(clazz, hm, login, perm);
						}
					} else if (vl != null && vl.value().equals(ValidPropagation.REQUIRED) && login == null) {
						return tologin(request, response, basePath);
					}
				}
			}
		}
		return true;
	}

	private boolean isAllow(Class<?> clazz, HandlerMethod hm, LoginInfo login, Permissions perm) {
		Module md = hm.getMethodAnnotation(Module.class);
		if (md == null) {
			md = clazz.getAnnotation(Module.class);
		}
		return login.getAllows().contains(new Allows(md.value(), perm.value()));
	}

	public Boolean getAuto() {
		return auto;
	}

	public void setAuto(Boolean auto) {
		this.auto = auto;
	}

	public static String getBasePath(HttpServletRequest request) {
		String basePath = getBasepath(request);
		if (INIT_RB != null && INIT_RB.containsKey("serverSslEnabled")
				&& INIT_RB.getString("serverSslEnabled").trim().equalsIgnoreCase("true")) {
			if (!basePath.startsWith("https://")) {
				basePath = basePath.replaceFirst("http", "https");
				if (basePath.endsWith(":80") || basePath.endsWith(":80/")) {
					basePath = basePath.replaceAll(":80", "");
				}
			}
		}
		return basePath;
	}

	private static String getBasepath(HttpServletRequest request) {
		String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath() + "/";
		return basePath;
	}

	private boolean tologin(HttpServletRequest request, HttpServletResponse response, String basePath)
			throws IOException, UnsupportedEncodingException {
		if ("GET".equalsIgnoreCase(request.getMethod())) {
			String queryString = request.getQueryString();
			basePath = request.getRequestURL() + (queryString == null ? "" : "?" + queryString);
		} else {
			String referer = request.getHeader("Referer");
			if (referer != null && referer.trim().length() > 1) {
				basePath = referer;
			}
		}
		if (INIT_RB != null && INIT_RB.containsKey("serverSslEnabled")
				&& INIT_RB.getString("serverSslEnabled").trim().equalsIgnoreCase("true")) {
			if (!basePath.startsWith("https://")) {
				basePath = basePath.replaceFirst("http", "https");
			}
		}
		response.sendRedirect(
				(loginUrl.startsWith("http") ? "" : request.getContextPath() + (loginUrl.startsWith("/") ? "" : "/"))
						+ loginUrl + "?url=" + URLEncoder.encode(basePath, StandardCharsets.UTF_8.name()));
		return false;
	}

	private String loginUrl = "/login";
	private Boolean auto = true;

	public PermissionInterceptor(Boolean auto) {
		super();
		this.auto = auto;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public PermissionInterceptor() {
		super();
	}

}
