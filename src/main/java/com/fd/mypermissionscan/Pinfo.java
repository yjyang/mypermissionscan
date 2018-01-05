package com.fd.mypermissionscan;

/**
 * 权限
 * 
 * @author 符冬
 *
 */
public class Pinfo {
	private String value;
	private String name;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Pinfo(String value, String name) {
		super();
		this.value = value;
		this.name = name;
	}

	public Pinfo() {
		super();
	}

}
