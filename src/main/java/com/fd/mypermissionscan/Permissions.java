package com.fd.mypermissionscan;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限标识注解
 * 
 * @author 符冬
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Permissions {
	/**
	 * 权限值
	 * 
	 * @return
	 */
	String value();

	/**
	 * 权限名称
	 * 
	 * @return
	 */
	String name();

}
