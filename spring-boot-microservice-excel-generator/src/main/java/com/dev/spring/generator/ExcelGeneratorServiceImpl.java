package com.dev.spring.generator;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;

import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.record.chart.FontBasisRecord;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ExcelGeneratorServiceImpl implements ExcelGeneratorService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private int MAXFILESPERDOC = 5;
	private static String baseExcelpath = "/home/david/.wrk/devenv-A/git/spring-microservices-files/spring-boot-microservice-excel-generator/doc/excel";   
	private static String baseFilePath= "/home/david/.wrk/devenv-A/git/spring-microservices-files/spring-boot-microservice-excel-generator/doc/files";
	@Autowired
	private FileDataRepository fileRepository;

	@Autowired
	private ExcelDataRepository excelRepository;


	public ExcelGeneratorServiceImpl() {}


	/**
	 * 
	 */

	@Override
	public List<Long> mapBeansToEntities(Map<String,List<ExcelDataBean>> data) throws Exception {
		FileData fileEnt = null;
		ExcelData excelEnt = null;
		List<ExcelDataBean> excelBeansList = (List) data.get("exceldata");
		//
		ExcelDataBean excelBean = excelBeansList.get(0); //Fixme.
		List<FileDataBean> fileDataBeans = excelBean.getFileDataList();
		List<Long> excelIds = new LinkedList<Long>();
		List<FileData> fileList = new LinkedList<FileData> ();
		int max = (excelBean.getMaxdocsperexcel()<=MAXFILESPERDOC)? excelBean.getMaxdocsperexcel():MAXFILESPERDOC;
		int count = 0; 
		excelEnt = new ExcelData();

		excelEnt = excelRepository.save(excelEnt);

		for(FileDataBean fdm:fileDataBeans) {
			fileEnt = new FileData();
			fileEnt.setUri(fdm.getUrl());
			fileEnt.setDescription(fdm.getDescription());
			fileEnt.setText(fdm.getText());
			fileEnt.setExcelData(excelEnt);
			fileEnt = fileRepository.save(fileEnt);
			fileList.add(fileEnt);
			count++;
			if(count==max || count==fileDataBeans.size()) {
				//
				excelEnt.setHeader(excelBean.getHeader());
				excelEnt.setFooter(excelBean.getFooter());
				excelEnt.setMaxdocs(max);
				excelEnt.setUri(Paths.get(baseExcelpath, excelBean.getPrefix()+ "-" + System.currentTimeMillis()+".xlsx").toString());
				excelEnt.setFileDataList(fileList);
				excelEnt = excelRepository.save(excelEnt);
				excelIds.add(excelEnt.getId());
				excelEnt = new ExcelData();
				fileList = new LinkedList<FileData>();
				count = 0;
				
			} 
			
		} 
			
		return excelIds;
	}

	/**
	 * 
	 * @param maxFilesPerDoc
	 */


	public void setMaxFilesPerDoc(int maxFilesPerDoc) {
		if(maxFilesPerDoc>0)
			MAXFILESPERDOC = maxFilesPerDoc;
	}

	/**
	 * 
	 */
	public List<URI> generateExcelFiles(List<Long> entityIds) throws Exception {
		List<URI> uris = new ArrayList<URI>();
		for(int i=0;i<entityIds.size();i++) { 
			
			URI u = generateExcel(entityIds.get(i));
			uris.add(u);

		}

		return uris;
	}

	/**    
	 * 
	 */

	@Override
	public  void downloadAndStoreFiles(List<Long> entityIds) throws Exception {
		
		List<Long> fileIds = new LinkedList<Long>();
		List<FileData> fileEntityList = null;
	    ExcelData excelData = null;
		for(Long exId:entityIds) {
		Optional<ExcelData> opt1 = excelRepository.findById(exId);
	    if(!opt1.isPresent())
	    	break;
		excelData = opt1.get();
		fileEntityList = excelData.getFileDataList();
		for(FileData fid:fileEntityList) {
			if(!fid.getUri().isEmpty())
				fid.setLocalUri(this.downloadFile(fid.getUri()));
			fid.setMimeType(MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(fid.getLocalUri()));
			fid = fileRepository.save(fid);
			fileIds.add(fid.getId());
		}

	   }
	

	}

	/**
	 * 
	 * @param entityId
	 * @return
	 * @throws Exception
	 */
	
	public synchronized URI generateExcel(Long entityId) throws Exception {

			ExcelData excEnt = null;
			List<FileData> fdList = new LinkedList<FileData>();
		    XSSFWorkbook wwb = null;
	        XSSFSheet ws = null;
            int r1,r2,c1,c2=0;
	        String header = "";
	        String footer = "";
	      short idx1=0,idx2 =0;
	        File excelFile = null;
	        Picture picture = null;
	        boolean img = false;
	        try {
	        
	        excEnt = excelRepository.getOne(entityId);
	        
	        if(excEnt==null || excEnt.getId()==null)
	        	throw new Exception("NO input Excel entity");
	        
	        wwb = new XSSFWorkbook();
		        
	        ws = wwb.createSheet("sheet0");
	
		  //  ws = createHeader(ws);
	        XSSFRow row = ws.getRow(1);
	        if(row==null)
	        	row = ws.createRow(1);
	        XSSFCell cell = row.createCell(4);
	        CellStyle csl = wwb.createCellStyle();
	        Font f = wwb.getFontAt(idx1);
	        f.setBold(true);
	        csl.setFont(f);
	        cell.setCellStyle(csl);
	        cell.setCellValue(excEnt.getHeader());
	        
	        String ext = "";
		    r1=2;c1=4; c2=8; r2=6; 
		    row = ws.createRow(r1); 
		    cell = row.createCell(c1);
		    row = ws.createRow(r2);
		    cell = row.createCell(c2);
		    for(FileData fid:excEnt.getFileDataList()) {
		   
		    	if(fid.getMimeType().contains("image")){
		    		int ptype = 0;	
		    		img = true;

		    		if(fid.getMimeType().contains("png")) {
		    			ptype = Workbook.PICTURE_TYPE_PNG;
		    			ext = "png";
		    		}else if(fid.getMimeType().contains("jpg") || fid.getMimeType().contains("jpeg")) {
		    			ptype = Workbook.PICTURE_TYPE_JPEG;
		    			ext = "jpg";
		    		}else if(fid.getMimeType().contains("dib")) {
		    			ptype = Workbook.PICTURE_TYPE_DIB;
		    			ext = "dib";
		    		}

		    		BufferedImage image = ImageIO.read(new File(fid.getLocalUri()));
		    		ByteArrayOutputStream baps = new ByteArrayOutputStream();
		    		ImageIO.write(image,ext,baps);
                    
		    	
		    	
		    		int pictureIdx = wwb.addPicture(baps.toByteArray(), ptype);

		    		fid.setFileExcelId(pictureIdx);
		    	
		    		baps.close();
		    		
		    		logger.debug("PicureId for excel generated: "+ pictureIdx);

		    		XSSFDrawing drawing = ws.createDrawingPatriarch();
		    		XSSFCreationHelper helper = wwb.getCreationHelper();
		    		XSSFClientAnchor anchor = helper.createClientAnchor();
		    		anchor.setCol1(c1);
		    		anchor.setRow1(r1);
		    		anchor.setAnchorType(AnchorType.MOVE_DONT_RESIZE);
		    		anchor.setCol2(c2);
		    		anchor.setRow2(r2);


		    		picture = drawing.createPicture(anchor, pictureIdx);
		    	}else {//storage of an object
		    	}

		    	//insert description
		    	r2 = r2 +1;
		    	row  = ws.getRow(r2);
		    	if(row==null)
		    		row = ws.createRow(r2);
		    	cell = row.getCell(c1);
		    	if(cell==null)
		    		cell = row.createCell(c1);
		    	
		    	f.setBold(false);
		    	csl.setFont(f);
		    	cell.setCellStyle(csl);
		    	cell.setCellValue(fid.getDescription());
		    	
		    	r2 = r2 + 1;
		    	row  = ws.getRow(r2);
		    	if(row==null)
		    		row = ws.createRow(r2);
		    	cell = row.getCell(c1);
		    	if(cell==null)
		    		cell = row.createCell(c1);
		    	f.setBold(false);
		    	csl.setFont(f);
		    	cell.setCellStyle(csl);
		    	cell.setCellValue(fid.getText());
		    	r1 = r2 + 2; //for image insertion 
		    	r2 = r1 + 2;

		    	if(img)
		    		picture.resize();
		    	else {
		    		//insert zip file
		    	}



		    	fid = fileRepository.save(fid);

		    	row = ws.createRow(r1); 
		    	cell = row.createCell(c1);
		    	row = ws.createRow(r2);
		    	cell = row.createCell(c2);


		    	fdList.add(fid);	


		    }
		   
		   // ws = createFooter(ws);
		 
		    
		    row = ws.getRow(r2);
		    if(row==null)
		    	row = ws.createRow(r2);
	        cell = row.createCell(1);
	        csl = wwb.createCellStyle();
	        f = wwb.getFontAt(idx2);
	        f.setBold(true);
	       
	        csl.setFont(f);
	        cell.setCellStyle(csl);
	        cell.setCellValue(excEnt.getFooter());
		       
	        
		    excelFile = new File(excEnt.getUri());
	        OutputStream ops = new FileOutputStream(excelFile);
	        wwb.write(ops);
	        wwb.close();
	        ops.close();
	        
	        excEnt.setFileDataList(fdList);
	        excEnt = excelRepository.save(excEnt);
	        
		
	        return excelFile.toURI();
	        
	        }catch(Exception ex) {
	        	logger.debug("Exception in excel generation for excelEntity Id: " + entityId);
	        	//throw new Exception(ex);
	        }

	        return null;

	}
    /**
     * 
     * @param uri
     * @return
     * @throws Exception
     */
	public synchronized String downloadFile(String uri) throws Exception {
		File f1 = null;
		URL url = null;
		String localUri = null;
		InputStream is2 = null;
		try {

			f1 = new File(uri);
			if(f1.exists() && f1.isFile()) {
				localUri = f1.getAbsolutePath();
			}
			
			if(localUri==null)
				throw new Exception("File didnt work for:" + uri);


		}catch(Exception ex1) {
			ex1.printStackTrace();
			try {
				url = new URL(uri);
				is2 = url.openConnection().getInputStream();
				localUri = saveStreamToFile(uri,is2);

				if(localUri==null)
					throw new Exception("URLConnection didnt work for:"+ uri);



			}catch(Exception ex2) {
				ex2.printStackTrace();
			}finally {
				if(is2!=null)
					is2.close();
			}

		}

		return localUri;
	}


	String saveStreamToFile(String originalUri,InputStream in) throws Exception {
		String luri = null;
		String sep = FileSystems.getDefault().getSeparator();
		String fname = originalUri.substring(originalUri.lastIndexOf(sep));
		Path fpath = Paths.get(baseFilePath, fname);
		String fname2 = "";
		if(fpath.toString().equals(originalUri) )
			throw new Exception("LocalUri and originalUri are the same");
		if(Files.exists(fpath)) {
			logger.debug("File already exists");//throw new Exception("File already exists");
			String numericpart = "";
			//Generating indexed name
			String[] ptrs = fname.split("\\.");
			String filePreName = null;
			if(ptrs[0].indexOf("#")>0) {
			filePreName = ptrs[0].substring(0,ptrs[0].lastIndexOf("#"));
			numericpart = ptrs[0].substring(ptrs[0].lastIndexOf("#")+1);
			}else {
				filePreName = ptrs[0];
			}
			
			long num = 1;
			try {
				num = Long.valueOf(numericpart).longValue();
				num++;
				
			}catch(NumberFormatException ne) {
				logger.debug("No numeric indexed name file so creating first indexed");
			}
			numericpart = "" + num;
		
			fname2 = filePreName +"#" + numericpart + "." + ptrs[ptrs.length-1];
			
			fpath = Paths.get(baseFilePath,fname2);
		}
		
		long nb =  Files.copy(in,fpath, StandardCopyOption.REPLACE_EXISTING);

		if(nb>0)
			luri = fpath.toString();

		return luri;


	}



}
