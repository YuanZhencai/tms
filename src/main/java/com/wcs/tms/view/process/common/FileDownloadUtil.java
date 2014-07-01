package com.wcs.tms.view.process.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.faces.context.FacesContext;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileDownloadUtil {
	private static final Log log = LogFactory.getLog(FileDownloadUtil.class);
	
    /**
     * 
     * <p>Description: 文件下载</p>
     * @param url
     * @param fileName
     * @param fis
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void fileDonwload(String fileName, InputStream fis) throws FileNotFoundException, IOException {
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
                .getResponse();
        HttpServletRequest quest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        fileDonwload(response, quest, fis, fileName);
        FacesContext.getCurrentInstance().responseComplete();
    }

    /**
     * 
     * <p>Description:文件下载利用Response产生输出流将字节写到客户端 </p>
     * @param response
     * @param fis
     * @param fileName
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void fileDonwload(HttpServletResponse response, HttpServletRequest request, InputStream fis, String fileName)
            throws FileNotFoundException, IOException {
        // fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
        fileName = encodeFilename(request, fileName);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentType("application/x-msdownload;charset=UTF-8");
        ServletOutputStream servletOutputStream = response.getOutputStream();
        byte[] b = new byte[1024];
        int i = 0;
        while ((i = fis.read(b)) > 0) {
            servletOutputStream.write(b, 0, i);
        }
        servletOutputStream.flush();
        servletOutputStream.close();
    }

    /**
     * 
     * <p>Description: 解决火狐浏览器下载文件名乱码</p>
     * @param quest
     * @param fileName
     * @return
     */
    private static String encodeFilename(HttpServletRequest quest, String fileName) {
        String agent = quest.getHeader("USER-AGENT");
        String nfileName = fileName;
        try {
            // IE
            if (null != agent && -1 != agent.indexOf("MSIE")) {
                nfileName = URLEncoder.encode(fileName, "UTF8");
                // Firefox
            } else if (null != agent && -1 != agent.indexOf("Mozilla")) {
                // fileName = "=?UTF-8?B?" + (new String(Base64.encodeBase64(fileName.getBytes("UTF-8")))) + "?=";
                nfileName = MimeUtility.encodeText(fileName, "UTF8", "B");
            }
        } catch (UnsupportedEncodingException e) {
            try {
                nfileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            } catch (UnsupportedEncodingException e1) {
            	log.error("encodeFilename方法 解决火狐浏览器下载文件名乱码 出现异常：",e1);
            }
            log.error("encodeFilename方法 解决火狐浏览器下载文件名乱码 出现异常：",e);
        }
        return nfileName;
    }
}
