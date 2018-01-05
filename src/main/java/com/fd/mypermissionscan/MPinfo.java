package com.fd.mypermissionscan;

import java.util.ArrayList;
import java.util.List;

/**
 * 模块权限数据映射
 * 
 * @author 符冬
 *
 */
public class MPinfo {
	private Minfo minfo;
	private List<Pinfo> pinfos = new ArrayList<Pinfo>();

	public Minfo getMinfo() {
		return minfo;
	}

	public void setMinfo(Minfo minfo) {
		this.minfo = minfo;
	}

	public List<Pinfo> getPinfos() {
		return pinfos;
	}

	public void setPinfos(List<Pinfo> pinfos) {
		this.pinfos = pinfos;
	}

	public MPinfo(Minfo minfo) {
		super();
		this.minfo = minfo;
	}

	public MPinfo() {
		super();
	}

	public MPinfo(Minfo minfo, List<Pinfo> pinfos) {
		super();
		this.minfo = minfo;
		this.pinfos = pinfos;
	}

}
