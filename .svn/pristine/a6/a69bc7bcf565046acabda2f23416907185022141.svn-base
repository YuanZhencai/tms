package com.wcs.common.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.wcs.common.controller.helper.MsgProducer;

@ManagedBean(name = "fileUploadBean")
@ViewScoped
public class FileUploadBean {

	private UploadedFile file;
	private final int BUFFER_SIZE = 100 * 1024;

	private Log log = LogFactory.getLog(FileUploadBean.class);
	
	@EJB
	private MsgProducer producer;

	private String path = ((ServletContext) FacesContext.getCurrentInstance()
			.getExternalContext().getContext()).getRealPath("");

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	/**
	 * 上传
	 * */
	public void singleUpload() {
		
	}

	public void handleFileUpload(FileUploadEvent event) {

		
	}

	public void copyFile(UploadedFile file, File dst) {
		try {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = file.getInputstream();
				out = new BufferedOutputStream(new FileOutputStream(dst),
						BUFFER_SIZE);
				byte[] buffer = new byte[BUFFER_SIZE];
				while (in.read(buffer) > 0) {
					out.write(buffer);
				}
			} finally {
				if (null != in) {
					in.close();
				}
				if (null != out) {
					out.close();
				}
			}
		} catch (Exception e) {
			log.error("上传附件 copyFile方法出现异常", e);
		}
	}

}
