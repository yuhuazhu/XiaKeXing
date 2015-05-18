package com.xkx.yjxm.bean;

public class ResInfo {

	private String title;
	private String content;
	private String bgname;
	private String musicname;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getBgname() {
		return bgname;
	}

	public void setBgname(String bgname) {
		this.bgname = bgname;
	}

	public String getMusicname() {
		return musicname;
	}

	public void setMusicname(String musicname) {
		this.musicname = musicname;
	}

	public ResInfo(String title,String content,String bgname,String musicname)
	{
		this.title = title;
		this.content = content;
		this.bgname = bgname;
		this.musicname = musicname;
		
	}
}
