package com.wcs.common.filenet.env;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Configuration {
    // UAT环境
	//$NON-NLS-1$
    //    public static  String BUNDLE_NAME = "uat_config"; 
	//$NON-NLS-1$
    public static String BUNDLE_NAME = "fn_config"; 
    //$NON-NLS-1$
    //public static  String BUNDLE_NAME = "qosfn_config"; 

    public Configuration() {
    }

    public static String getParameter(String key) {
        try {
            ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
