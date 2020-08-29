package com.dev.spring.filedata;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="excel-generator")
@RibbonClient(name="excel-generator")
public interface ExcelGeneratorServiceProxy {
  @PostMapping("/excel-generator/filedata")
  public List<URI> generateExcelFiles(@RequestBody Map<String,List<ExcelDataBean>> rBody);


}