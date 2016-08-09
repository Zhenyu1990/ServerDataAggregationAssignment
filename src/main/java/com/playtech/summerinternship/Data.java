package com.playtech.summerinternship;

public class Data {

	private String path;
	private int value;
	private String timeStamp;
	private String secFilePath;
	private String minFilePath;
	
	public Data() {}

	public Data(String path, int value, String timeStamp, String secFilePath, String minFilePath) {
		super();
		this.path = path;
		this.value = value;
		this.timeStamp = timeStamp;
		this.secFilePath = secFilePath;
		this.minFilePath = minFilePath;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getSecFilePath() {
		return secFilePath;
	}

	public void setSecFilePath(String secFilePath) {
		this.secFilePath = secFilePath;
	}

	public String getMinFilePath() {
		return minFilePath;
	}

	public void setMinFilePath(String minFilePath) {
		this.minFilePath = minFilePath;
	}

}
