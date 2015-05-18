package com.xkx.yjxm.bean;

public class MacInfo {
	private String macname;
	private Float power;
	private Float distince;
	
	public MacInfo(String macname,Float power,Float distince)
	{
		this.macname = macname;
		this.power = power;
		this.distince = distince;
		
	}

	public String getMacname() {
		return macname;
	}

	public void setMacname(String macname) {
		this.macname = macname;
	}

	public Float getPower() {
		return power;
	}

	public void setPower(Float power) {
		this.power = power;
	}

	public Float getDistince() {
		return distince;
	}

	public void setDistince(Float distince) {
		this.distince = distince;
	}
}
