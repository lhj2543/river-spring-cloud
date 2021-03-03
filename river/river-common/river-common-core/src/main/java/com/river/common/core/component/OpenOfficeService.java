package com.river.common.core.component;


import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ConnectException;

@Slf4j
@RefreshScope
@Component
public class OpenOfficeService{

  private String OpenOffice_home = "C:/Program Files (x86)/OpenOffice 4/program/";

  private String OpenOffice_host = "localhost";

  private String OpenOffice_port = "8100";

  public boolean officeConvert(InputStream in, OutputStream out, String inType, String outType){
    Process pro = null;
    OpenOfficeConnection connection = null;
    try {
      if (in == null) {
        return false;
      }

      String command = new StringBuilder().append(this.OpenOffice_home)
              .append("soffice.exe -headless -accept=\"socket,host=").append(this.OpenOffice_host)
              .append(",port=").append(this.OpenOffice_port)
              .append(";\"").toString();

      log.info(new StringBuilder().append("OpenOffice command=").append(command).toString());
      pro = Runtime.getRuntime().exec(command);
      //pro.waitFor();


      connection = new SocketOpenOfficeConnection(this.OpenOffice_host, Integer.parseInt(this.OpenOffice_port));
      connection.connect();

      DocumentConverter converter = new OpenOfficeDocumentConverter(connection);

      DefaultDocumentFormatRegistry formatReg = new DefaultDocumentFormatRegistry();
      DocumentFormat docFormat = formatReg.getFormatByFileExtension(inType);
      DocumentFormat pdfFormat = formatReg.getFormatByFileExtension(outType);
      converter.convert(in, docFormat, out, pdfFormat);

      return true;
    } catch (ConnectException e) {
      log.error("OpenOffice服务监听异常！",e);
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
      log.error("OpenOffice 转换异常！",e);
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
      if (pro != null) {
        pro.destroy();
      }
    }
    return false;

  }
 
  public static void main(String[] args){
	  
	OpenOfficeService view = new OpenOfficeService();

    OutputStream out = null;
    InputStream in = null;
    try {
      //in = new FileInputStream("C:/Users/T440/Desktop/nssol调查/pdf/CREDIT.xlsx");
      //out = new FileOutputStream("C:/Users/T440/Desktop/nssol调查/pdf/CREDIT.pdf");
      String dir = "D:/test-file/";
      in = new FileInputStream(dir+ "test.xlsx");
      out = new FileOutputStream(dir+ "test.pdf");
      
      //in = new FileInputStream("C:/Users/T440/Desktop/nssol调查/pdf/LAW.xlsx");
      //out = new FileOutputStream("C:/Users/T440/Desktop/nssol调查/pdf/LAW.pdf");
      
      ByteArrayOutputStream o = new ByteArrayOutputStream();

      boolean falg = view.officeConvert(in, out, "ppt", "pdf");
      System.err.println(falg);
      
      //System.err.println(o.toByteArray().length);
      
      out.flush();
      
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(in);
    }
  }
}