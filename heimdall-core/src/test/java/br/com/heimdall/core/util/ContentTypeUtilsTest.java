package br.com.heimdall.core.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ContentTypeUtilsTest {

     @Test
     public void permitEmptyContentTypeInBlackList() {
          String contentType = "";
          
          String[] types = contentType.split(";");
          
          boolean belongsToBlackList = ContentTypeUtils.belongsToBlackList(types);
          
          assertEquals(false, belongsToBlackList);
     }
     
     @Test
     public void blockPdfContentType() {
          String contentType = "application/pdf;charset=UTF-8";
          
          String[] types = contentType.split(";");
          
          boolean belongsToBlackList = ContentTypeUtils.belongsToBlackList(types);
          
          assertEquals(true, belongsToBlackList);
     }
     
     @Test
     public void permitJSONContentTypeInBlackList() {
          String contentType = "application/json";
          
          String[] types = contentType.split(";");
          
          boolean belongsToBlackList = ContentTypeUtils.belongsToBlackList(types);
          
          assertEquals(false, belongsToBlackList);
     }
     
}
