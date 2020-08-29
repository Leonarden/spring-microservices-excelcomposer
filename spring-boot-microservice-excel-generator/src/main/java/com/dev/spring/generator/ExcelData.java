package com.dev.spring.generator;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class ExcelData {
  
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Long id;
  
  private String uri;
  
  private String header;
  
  private String footer;
  
  private int maxdocs;
  
  
  private String description;
  
 @OneToMany(mappedBy="excelData")
 List<FileData> fileDataList;
 
  private int port;
  
    public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

public Long getId() {
	return id;
}

public void setId(Long id) {
	this.id = id;
}

public String getUri() {
	return uri;
}

public void setUri(String uri) {
	this.uri = uri;
}

public String getDescription() {
	return description;
}

public void setDescription(String description) {
	this.description = description;
}

public List<FileData> getFileDataList() {
	return fileDataList;
}

public void setFileDataList(List<FileData> fileDataList) {
	this.fileDataList = fileDataList;
}

public String getHeader() {
	return header;
}

public void setHeader(String header) {
	this.header = header;
}

public String getFooter() {
	return footer;
}

public void setFooter(String footer) {
	this.footer = footer;
}

public int getMaxdocs() {
	return maxdocs;
}

public void setMaxdocs(int maxdocs) {
	this.maxdocs = maxdocs;
}
 
  
}