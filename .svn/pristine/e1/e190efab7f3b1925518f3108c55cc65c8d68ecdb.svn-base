package com.wcs.common.filenet.env;

import java.io.File;
import java.net.URL;
import java.util.MissingResourceException;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.security.auth.Subject;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.filenet.api.core.Connection;
import com.filenet.api.core.Factory;
import com.filenet.api.util.UserContext;
import com.wcs.base.service.LoginService;
import com.wcs.common.filenet.pe.SessionHelper;

import filenet.vw.api.VWSession;

@SessionScoped
@Stateful(name = "EnvInit")
public class EnvInit {
    private static Logger log = Logger.getLogger(EnvInit.class);

    private TmsEnv env;

    // @PostConstruct
    public TmsEnv initEnv(HttpSession session) {
        env = new TmsEnv();
        initCE();
        initPE();
        if (session != null) {
            session.setAttribute("env", env);
        }
        return env;
    }

    @Inject
    private LoginService loginService;

    private void initCE() {
        setJVMParameters();
        String ceUrl = SysCfg.getStrConfig("CE.CEURL");
        Connection ceConn = Factory.Connection.getConnection(ceUrl);
        env.setCeConn(ceConn);
        String ceAdminUser = "";
        String ceAdminPassword = "";
        ceAdminUser = loginService.getCurrentUserName();
        if(ceAdminUser != null){
        	ceAdminPassword = SysCfg.getStrConfig("CE.filenetCeUserPasswd");
        	if(ceAdminUser.equals(SysCfg.getStrConfig("CE.filenetCeAdminUserName"))){
        		ceAdminPassword = SysCfg.getStrConfig("CE.filenetCeAdminUserPasswd");
        	}
        }else{
        	ceAdminUser = SysCfg.getStrConfig("CE.filenetCeAdminUserName");
        	ceAdminPassword = SysCfg.getStrConfig("CE.filenetCeAdminUserPasswd");
        }
        log.info(ceAdminUser);
        log.info(ceAdminPassword);
        Subject subject = UserContext.createSubject(ceConn, ceAdminUser, ceAdminPassword, "FileNetP8WSI");
        UserContext uc = UserContext.get();
        uc.pushSubject(subject);
    }

    private void initPE() {
        log.info("1.取登录用户信息完成...");
        log.info("2.登录PE...");
        String peAdminUser = "";
        String peAdminPassword = "";
        peAdminUser = loginService.getCurrentUserName();
        if(peAdminUser != null){
        	peAdminPassword = SysCfg.getStrConfig("PE.FilenetPeUserPasswd");
        	if(peAdminUser.equals(SysCfg.getStrConfig("PE.FilenetPeAdminUserName"))){
        		peAdminPassword = SysCfg.getStrConfig("PE.FilenetPeAdminUserPasswd");
        	}
        }else{
        	peAdminUser = SysCfg.getStrConfig("PE.FilenetPeAdminUserName");
        	peAdminPassword = SysCfg.getStrConfig("PE.FilenetPeAdminUserPasswd");
        }
        VWSession vwSession = SessionHelper.logon(peAdminUser, peAdminPassword);
        log.info("3.登录PE成功");
        env.setVWSess(vwSession);
    }
    
    /**
     * PE注销
     * @param session
     */
    public void logoffPE(HttpSession session){
    	TmsEnv sessionEnv = new TmsEnv();
    	try {
    		sessionEnv = (TmsEnv) session.getAttribute("env");
    		if(sessionEnv != null){
    			VWSession vwSession = sessionEnv.getVWSession();
    			String peUserName = vwSession.fetchCurrentUserInfo().getName();
    			SessionHelper.logoff(vwSession) ;
    			log.info(peUserName+"流程服务器注销成功！");
    		}
    		return;
		} catch (Exception e) {
			return;
		}
    }

    private void initLdap() {

    }

    /**
     * Set JVM parameters for login.
     * 
     * @param configBundle
     *            the login configuration bundle
     * @param ceUrl
     *            Content Engine URL
     * @param jaasConfigFile
     *            JAAS configuration file path.
     */
    public void setJVMParameters() {
        String jaasConfigFile = SysCfg.getStrConfig("CE.JAAS_ConfigFile");
        String ceUrl = SysCfg.getStrConfig("CE.CEURL");
        System.setProperty("java.security.auth.login.config", "/filenetenv/jaas.conf.WSI");
        if (log.isDebugEnabled()) {
        	log.info("Configuration file path:");
        }
        // Only Web Services transport need to set the "wasp.location"
        // parameter.
        try {
            String waspLocation = SysCfg.getStrConfig("CE.WASP_Location");
            if (waspLocation != null && !waspLocation.equals("")) {
                URL waspLocationURL = EnvInit.class.getResource(waspLocation);
                String waspLocationPath = toFilePath(waspLocationURL);

                if (log.isDebugEnabled()) {
                    log.info("Use WebService transport.");
                    log.info("wasp config file = " + waspLocationPath);
                }
                String tmp = System.getProperty("wasp.location");
                if (tmp == null || tmp.length() == 0) {
                    System.setProperty("wasp.location", waspLocationPath);
                }
            }
        } catch (MissingResourceException e) {
            // do nothing if this key not found.
        }

        // Setup jaas configuration parameter.
        URL jaasConfigURL = EnvInit.class.getResource(jaasConfigFile);
        String jaasConfigFilePath = toFilePath(jaasConfigURL);
        if (log.isDebugEnabled()) {
            log.info("jaas config file = " + jaasConfigFilePath);
        }
        String tmp = System.getProperty("java.security.auth.login.config");
        if (tmp == null || tmp.length() == 0) {
            System.setProperty("java.security.auth.login.config", jaasConfigFilePath);
        }
        tmp = System.getProperty("filenet.pe.bootstrap.ceuri");
        if (tmp == null || tmp.length() == 0) {
            // set the CE_URL for authentication.
            System.setProperty("filenet.pe.bootstrap.ceuri", ceUrl);
        }
        if (log.isDebugEnabled()) {
            log.info("Validate logon properties:");
            log.info("CE URL = " + ceUrl);
            log.info("JVM Parameters:");
            log.info("java.security.auth.login.config = " + System.getProperty("java.security.auth.login.config"));
            log.info("wasp.location = " + System.getProperty("wasp.location"));
            log.info("filenet.pe.bootstrap.ceuri = " + System.getProperty("filenet.pe.bootstrap.ceuri"));
            log.info("java.ext.dirs" + System.getProperty("java.ext.dirs"));
            log.info("");
        }
    }

    /**
     * Convert a file url to real file path without encoding.
     * 
     * @param url
     *            the url of the file.
     * @return real file path without encoding.If the url is not a file url,
     *         return <code>null</code>.
     */
    private String toFilePath(URL url) {
        if (url == null || !(url.getProtocol().equals("file") || url.getProtocol().equals("vfs"))) {
            return null;
        } else {
            String filename = url.getFile().replace('/', File.separatorChar);
            int position = 0;
            while ((position = filename.indexOf('%', position)) >= 0) {
                if (position + 2 < filename.length()) {
                    String hexStr = filename.substring(position + 1, position + 3);
                    char ch = (char) Integer.parseInt(hexStr, 16);
                    filename = filename.substring(0, position) + ch + filename.substring(position + 3);
                }
            }
            return new File(filename).getPath();
        }
    }
}
