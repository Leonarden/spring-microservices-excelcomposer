package com.dev.spring.filedata;

import java.io.Serializable;
import java.util.List;

public class ExcelDataBean implements Serializable {
	
	private String header;
	private String prefix;
	private String footer;
	private int maxdocsperexcel;
	private List<FileDataBean> fileDataList;
	
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getFooter() {
		return footer;
	}
	public void setFooter(String footer) {
		this.footer = footer;
	}
	public int getMaxdocsperexcel() {
		return maxdocsperexcel;
	}
	public void setMaxdocsperexcel(int maxdocsperexcel) {
		this.maxdocsperexcel = maxdocsperexcel;
	}
	public List<FileDataBean> getFileDataList() {
		return fileDataList;
	}
	public void setFileDataList(List<FileDataBean> fileDataList) {
		this.fileDataList = fileDataList;
	}
	
	
	

}
