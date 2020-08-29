package com.dev.spring.filedata;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileDataFacadeController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ExcelGeneratorServiceProxy proxy;

	@PostMapping("/filedata-facade/filedata")
	public List<URI> imageFacade(@RequestBody Map<String, Object>  data) {
		List<URI> locations=null;
		List<FileDataBean> fDataBeanList = new ArrayList<FileDataBean>();
		FileDataBean fdb = null;
		ExcelDataBean excelDataBean = null;
		try {
		
			List<ExcelDataBean> excelDBeans = new LinkedList<ExcelDataBean>();
		Map<String,List<ExcelDataBean>> rBody = new HashMap<String,List<ExcelDataBean>>();
		
		Map<String,Object> edata = (Map<String,Object>) data.get("exceldata");
		excelDataBean = new ExcelDataBean();
		excelDataBean.setHeader((String)edata.get("header"));
		excelDataBean.setFooter((String) edata.get("footer"));
		excelDataBean.setPrefix((String) edata.get("prefix"));
		try {
			excelDataBean.setMaxdocsperexcel((int) edata.get("maxdocsperexcel"));
		}catch(Exception nfe) {
			excelDataBean.setMaxdocsperexcel(0);
		}
		
		
		 List<Map<String,Object>> fdata = (List) data.get("filedata");
		
		for(int i=0;i<fdata.size();i++) {
			Map<String,Object> map1 = fdata.get(i);
			fdb = new FileDataBean();
			fdb.setUrl((String)map1.get("url"));
			fdb.setDescription((String)map1.get("description"));
			fdb.setText((String)map1.get("text"));
			fDataBeanList.add(fdb);
		}
		excelDataBean.setFileDataList(fDataBeanList);
		
		excelDBeans.add(excelDataBean);

		
		rBody.put("exceldata", excelDBeans);
		
		locations = proxy.generateExcelFiles(rBody);
		
		logger.info("{FileFacadeController after proxy call OK }");
		
		
		
		}catch(Exception ex) {
			logger.info("{Exception in filedatafacadeController:}"+ ex.getMessage());
			ex.printStackTrace();

		}
		
		return locations;
	}

}