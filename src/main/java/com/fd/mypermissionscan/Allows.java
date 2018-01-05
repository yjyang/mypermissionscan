package com.fd.mypermissionscan;

/**
 * 可以使用的权限
 * 
 * @author 符冬
 *
 */
public class Allows {
	/**
	 * 模块值
	 */
	private String module;
	/**
	 * 权限值
	 */
	private String permission;

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((module == null) ? 0 : module.hashCode());
		result = prime * result + ((permission == null) ? 0 : permission.hashCode());
		return result;
	}

	public Allows() {
		super();
	}

	public Allows(String module, String permission) {
		super();
		this.module = module;
		this.permission = permission;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Allows other = (Allows) obj;
		if (module == null) {
			if (other.module != null)
				return false;
		} else if (!module.equals(other.module))
			return false;
		if (permission == null) {
			if (other.permission != null)
				return false;
		} else if (!permission.equals(other.permission))
			return false;
		return true;
	}

}
