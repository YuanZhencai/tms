package com.wcs.common.filenet.ce;

import java.util.HashMap;

import com.wcs.tms.exception.ServiceException;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: 文件类型</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
public class MimeUtil {
	
	private MimeUtil() {
		
	}
	
    protected static final class PropertyKeys {
        private static HashMap<String, String> hs;
        static {
            hs = new HashMap<String, String>();
            hs.put("doc", "application/msword");
            hs.put("dot", "application/msword");
            hs.put("pdf", "application/pdf");
            hs.put("jpg", "image/jpg");
            hs.put("jpeg", "image/jpeg");
            hs.put("gif", "image/gif");
            hs.put("png", "image/x-png");
            hs.put("txt", "text/plain");
            hs.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            hs.put("xls", "application/vnd.ms-excel");
            hs.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            hs.put("eml", "message/rfc822");
            hs.put("msg", "application/octet-stream");
        }
    }

    public static String getMimeType(String type) {
        String mimetype = (String) PropertyKeys.hs.get(type);
        if (mimetype == null) {
        	throw new ServiceException("type not found : " + type);
        }else{
        	return mimetype;
        }
    }

    public static String getMimeTypeWidhValue(String value) {
        String keyValue = null;
        for (String key : PropertyKeys.hs.keySet()) {
            if (PropertyKeys.hs.get(key).equals(value)) {
                keyValue = key;
            }
        }
        if (keyValue == null) { 
        	throw new ServiceException("type not found : " + value); 
        }
        return keyValue;
    }
}
