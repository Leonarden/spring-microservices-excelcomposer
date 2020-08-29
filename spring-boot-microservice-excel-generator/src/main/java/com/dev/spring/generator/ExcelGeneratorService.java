package com.dev.spring.generator;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional
public interface ExcelGeneratorService {
	
	public void setMaxFilesPerDoc(int maxFilesPerDoc);
	
	public List<Long> mapBeansToEntities(Map<String,List<ExcelDataBean>> data) throws Exception;
	
	public List<URI> generateExcelFiles(List<Long> entityIds) throws Exception;

	public  void downloadAndStoreFiles(List<Long> entityIds) throws Exception;

}
