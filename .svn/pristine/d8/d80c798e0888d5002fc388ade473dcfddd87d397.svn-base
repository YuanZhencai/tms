/** * ReportBase.java 
* Created on 2011-11-25 上午10:05:18 
*/

package com.wcs.report.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXhtmlExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.util.JSFUtils;
import com.wcs.report.test.WebappDataSource;


/** 
* <p>Project: btcbase</p> 
* <p>Title: ReportBase.java</p> 
* <p>Description: </p> 
* <p>Copyright: Copyright 2011.All rights reserved.</p> 
* <p>Company: wcs.com</p> 
* @author <a href="mailto:yangshiyun@wcs-global.com">Yang Shiyun</a> 
*/

@SuppressWarnings("serial")
public abstract class ReportBase implements Serializable {
    private DataSource ds;
    /** 模板页 */
    private String templateFile = null;
    /** 编译后的模板页 */
    private String compiledFile = null;
    private String reportContent = "";
    private String printFile;
	protected String pageValue;
	protected String pageAll;
	
	private Log log = LogFactory.getLog(ReportBase.class);
	
    public ReportBase() {

    }

    /**
     * Description: 将模板页进行编译
     */
    public void initComplier() {
        try {
            if (StringUtils.isEmpty(this.getTemplateFile())) { return; }
            JasperCompileManager.compileReportToFile(this.getTemplateFile());
        } catch (JRException e) {
        	log.error("initComplier方法 将模板页进行编译异常", e);
        }
    }

    /**
     * Description:初始化模板页和编译文件路径
     * @param template
     * @param compiled
     */
    public void setupPage(String template, String compiled, String print) {
        if (!StringUtils.isEmpty(template)) {
        	this.templateFile = this.getProjectPath().concat(template);
        }
        if (!StringUtils.isEmpty(compiled)) {
        	this.compiledFile = this.getProjectPath().concat(compiled);
        }
        if (!StringUtils.isEmpty(compiled)) {
            this.printFile = this.getProjectPath().concat(print);
        }
    }

    /**
     * 
     * <p>Description: 生成报表</p>
     * @param parameters
     * @throws JRException
     */
    public void genneralReport(Map<String, Object> parameters) throws JRException {
        File reportFile = new File(this.getCompiledFile());
        if (!reportFile.exists()) {
            this.initComplier();
        }
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportFile.getPath());
        JasperPrint jasperPrint = null;
        try {
            JasperFillManager.fillReport(jasperReport, parameters, new WebappDataSource());
            jasperPrint = loadReportPrint();
        } catch (Exception e) {
            log.error("genneralReport方法 生成报表异常", e);
        }
        int pageIndex = 1;
		int lastPageIndex = 1;
		if (jasperPrint.getPages() != null)
		{
			lastPageIndex = jasperPrint.getPages().size();
		}
		if ("".equals(this.pageValue)){
			this.pageValue="1";
		}
		pageIndex = Integer.parseInt(this.pageValue);
		
		if (pageIndex < 1)
		{
			pageIndex = 1;
		}

		if (pageIndex > lastPageIndex)
		{
			pageIndex = lastPageIndex;
		}
		this.pageAll = String.valueOf(lastPageIndex);
		this.pageValue = String.valueOf(pageIndex);
		String rootPath = JSFUtils.getRequest().getRealPath( "/" );
		JRXhtmlExporter exporter = new JRXhtmlExporter();
		StringBuffer sbuffer = new StringBuffer();
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, jasperPrint);
		exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, rootPath+"images\\");
		
		exporter.setParameter(JRExporterParameter.OUTPUT_STRING_BUFFER, sbuffer);
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
		exporter.setParameter(JRExporterParameter.PAGE_INDEX, pageIndex-1);
		exporter.setParameter(JRHtmlExporterParameter.HTML_HEADER, "");
		exporter.setParameter(JRHtmlExporterParameter.BETWEEN_PAGES_HTML, "");
		exporter.setParameter(JRHtmlExporterParameter.HTML_FOOTER, "");
		
		exporter.exportReport();
        this.setReportContent(sbuffer.toString());
    }

    public String exportPdf(HttpServletResponse response) throws JRException, IOException {
        String destFile = JasperExportManager.exportReportToPdfFile(this.getPrintFile());
        File file = new File(destFile);
        if (file.exists()) {
            FileInputStream ins = new FileInputStream(file);
            this.fileExport(response, ins, file.getName());
            FacesContext.getCurrentInstance().responseComplete();
        }
        return destFile;
    }

    /**
     * 
     * <p>Description:导出PDF </p>
     * @param response
     * @throws JRException
     * @throws IOException
     */
    public void exportToPdf(HttpServletResponse response) throws JRException, IOException {
        JRPdfExporter exporter = new JRPdfExporter();
        reportfileexport(response, exporter, ".pdf");
    }

    /**
     * 
     * <p>Description: 导出Excel2003</p>
     * @param response
     * @throws JRException
     * @throws IOException
     */
    public void exportToExcel(HttpServletResponse response) throws JRException, IOException {
        JRXlsExporter exporter = new JRXlsExporter();
        reportfileexport(response, exporter, ".xls");
    }

    /**
     * 
     * <p>Description: 导出Excel2007</p>
     * @param response
     * @throws JRException
     * @throws IOException
     */
    public void exportToExcels(HttpServletResponse response) throws JRException, IOException {
        JRXlsxExporter exporter = new JRXlsxExporter();
        reportfileexport(response, exporter, ".xlsx");
    }
    
    /**
     * 
     * <p>Description: 导出Xhtml</p>
     * @param response
     * @throws JRException
     * @throws IOException 
     */
    public void exportToXHtml(HttpServletResponse response) throws JRException, IOException {
        JRXhtmlExporter exporter = new JRXhtmlExporter();
        reportfileexport(response, exporter, ".xhtml");
    }

    /**
     * 
     * <p>Description: 导出Word2007</p>
     * @param response
     * @throws JRException
     * @throws IOException
     */
    public void exportToDocx(HttpServletResponse response) throws JRException, IOException{
        JRDocxExporter exporter = new JRDocxExporter();
        reportfileexport(response, exporter, ".docx");
    }
    
    /**
     * 
     * <p>Description: 得到JasperPrint对象</p>
     * @return
     * @throws JRException
     */
    private JasperPrint loadReportPrint() throws JRException {
        return (JasperPrint) JRLoader.loadObject(this.getPrintFile());
    }

    /**
     * 
     * <p>Description: 通过输出流向客户端写文件</p>
     * @param response
     * @param jasperPrint
     * @param bytes
     * @param fileName
     * @throws IOException
     * @throws JRException 
     */
    private void reportfileexport(HttpServletResponse response, JRAbstractExporter exporter, String type) throws IOException,
            JRException {
        JasperPrint jasperPrint = loadReportPrint();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
        exporter.exportReport();
        byte[] bytes = baos.toByteArray();
        String fileName = jasperPrint.getName().concat(type);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentType("application/x-msdownload;charset=UTF-8");
        ServletOutputStream servletOutputStream = response.getOutputStream();
        servletOutputStream.write(bytes, 0, bytes.length);
        servletOutputStream.flush();
        servletOutputStream.close();
        FacesContext.getCurrentInstance().responseComplete();
    }

    /**
     * 
     * <p>Description:得到文件输入流 </p>
     * @param filePath
     * @return
     * @throws FileNotFoundException
     */
    private FileInputStream createInputStream(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        FileInputStream ins = null;
        if (file.exists()) {
            ins = new FileInputStream(file);
            return ins;
        }
        return ins;
    }

    /**
     * 
     * <p>Description: 通过输入输出流向客户端写文件</p>
     * @param response
     * @param fis
     * @param fileName
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void fileExport(HttpServletResponse response, FileInputStream fis, String fileName) throws FileNotFoundException,
            IOException {
        fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
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
     * 获得连接conn
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private DataSource getDataSource() throws NamingException {
        if (ds == null) {
            javax.naming.Context ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("reportdb");

        }
        return ds;
    }

    private Connection getConnection() throws SQLException, NamingException {
        return getDataSource().getConnection();
    }

    /**
     * 获得项目路径
     * @return projectPath
     */
    protected String getProjectPath() {
    	
    	
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = ( HttpServletRequest ) context.getExternalContext().getRequest();
		String projectPath = request.getRealPath( "/" )  +"jasperreports/";

        return projectPath;
    }

    // -------------------- setter & getter --------------------//

    public String getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(String templateFile) {
        this.templateFile = templateFile;
    }

    public String getCompiledFile() {
        return compiledFile;
    }

    public void setCompiledFile(String compiledFile) {
        this.compiledFile = compiledFile;
    }

    public String getReportContent() {
        return reportContent;
    }

    public void setReportContent(String reportContent) {
        this.reportContent = reportContent;
    }

    public String getPrintFile() {
        return printFile;
    }

    public void setPrintFile(String printFile) {
        this.printFile = printFile;
    }
    
	public String getPageAll() {
		return pageAll;
	}

	public void setPageAll(String pageAll) {
		this.pageAll = pageAll;
	}

	public String getPageValue() {
		return pageValue;
	}

	public void setPageValue(String pageValue) {
		this.pageValue = pageValue;
	}

}
