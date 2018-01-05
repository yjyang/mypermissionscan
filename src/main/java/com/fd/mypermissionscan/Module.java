package com.fd.mypermissionscan;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限功能模块
 * 
 * @author 符冬
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@Documented
public @interface Module {
	/**
	 * 模块值
	 * 
	 * @return
	 */
	String value();

	/**
	 * 模块名称
	 * 
	 * @return
	 */
	String name();
}
