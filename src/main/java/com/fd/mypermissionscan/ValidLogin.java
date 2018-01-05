package com.fd.mypermissionscan;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 登录验证
 * 
 * @author 符冬
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@Documented
public @interface ValidLogin {
	/**
	 * 
	 * 
	 * REQUIRED:表示需要登录之后才能访问
	 * 
	 * @return
	 */
	ValidPropagation value() default ValidPropagation.REQUIRED;
}
