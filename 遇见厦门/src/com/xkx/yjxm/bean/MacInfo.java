package com.xkx.yjxm.bean;

public class MacInfo {
	private int  ID;
	private String macname;
	private Float power;
	private Float distince;
	
	public MacInfo(int ID,String macname,Float power,Float distince)
	{
		this.ID = ID;
		this.macname = macname;
		this.power = power;
		this.distince = distince;
		
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
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
