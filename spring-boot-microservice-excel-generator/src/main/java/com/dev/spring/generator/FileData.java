package com.dev.spring.generator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class FileData {
  
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Long id;
  
  private String uri;
 
  //private byte[] content;
  
  private String localUri;
  
  private String description;
  
  private String text;
  
  @ManyToOne(optional=true)
  private ExcelData excelData;
  
  private int fileExcelId;
  
  private String mimeType;
  
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

public String getLocalUri() {
	return localUri;
}

public void setLocalUri(String localUri) {
	this.localUri = localUri;
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

public ExcelData getExcelData() {
	return excelData;
}

public void setExcelData(ExcelData excelData) {
	this.excelData = excelData;
}

public int getFileExcelId() {
	return fileExcelId;
}

public void setFileExcelId(int fileExcelId) {
	this.fileExcelId = fileExcelId;
}

public String getText() {
	return text;
}

public void setText(String text) {
	this.text = text;
}

public String getMimeType() {
	return mimeType;
}

public void setMimeType(String mimeType) {
	this.mimeType = mimeType;
}
 

  
}