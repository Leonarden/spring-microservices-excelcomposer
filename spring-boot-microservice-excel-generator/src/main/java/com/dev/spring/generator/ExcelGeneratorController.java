package com.dev.spring.generator;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExcelGeneratorController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());


	@Autowired
	private ExcelGeneratorService excelGeneratorService;

	@PostMapping("/excel-generator/filedata")
	public List<URI> generateExcelFiles (@RequestBody Map<String,List<ExcelDataBean>> rBody){
		List<URI> filelocation = null;
		List<Long> entIds = new ArrayList<Long>();
		try {

			entIds = excelGeneratorService.mapBeansToEntities(rBody);

			excelGeneratorService.downloadAndStoreFiles(entIds);

			filelocation = excelGeneratorService.generateExcelFiles(entIds);
			logger.debug("Sucessful call to service");


		}catch(Exception ex) {
			ex.printStackTrace();
			logger.debug("generateExcelFiles exception: " + ex.getLocalizedMessage());
		}
		return filelocation;
	}





}