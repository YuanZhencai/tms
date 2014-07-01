/**
 * MyAuthorizingRealm.java
 * Created: 2012-1-10 上午09:47:53
 */
package com.wcs.base.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.wcs.common.model.Resourcemstr;
import com.wcs.common.model.Rolemstr;

/**
 * <p>Project: btcbase</p>
 * <p>Description: </p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chengchao@wcs-global.com">ChengChao</a>
 */
@SuppressWarnings("serial")
public class BaseAuthorizingRealm extends AuthorizingRealm implements Serializable {


    private static Log log = LogFactory.getLog(BaseAuthorizingRealm.class.getName());

    /**
     * 鉴权
     * @see org.apache.shiro.realm.AuthorizingRealm#doGetAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection)
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("doGetAuthorizationInfo...principals:" + principals.toString());
        List<String> p = getPermissions();
        List<String> r = getRoles();
        if (p != null || r != null) {
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            if (p != null) {
                info.addStringPermissions(p);
            }
            if (r != null) {
                info.addRoles(r);
            }
            return info;
        }
        return null;
    }

    private List<String> getPermissions() {
        Object obj_rs = SecurityUtils.getSubject().getSession().getAttribute(LoginService.SESSION_KEY_RESOURCES);
        if (obj_rs != null && ((List<Resourcemstr>) obj_rs).size() > 0) {
            List<String> p = new ArrayList<String>();
            List<Resourcemstr> resources = (List<Resourcemstr>) obj_rs;
            for (Resourcemstr rs : resources) {
                if (rs.getCode() != null && !rs.getCode().isEmpty()) {
                    p.add(rs.getCode());
                }
            }
            if (!p.isEmpty()) { 
            	return p; 
            }
        }
        return null;
    }

    private List<String> getRoles() {
        Object obj_ro = SecurityUtils.getSubject().getSession().getAttribute(LoginService.SESSION_KEY_ROLES);
        if (obj_ro != null && ((List<Rolemstr>) obj_ro).size() > 0) {
            List<String> r = new ArrayList<String>();
            List<Rolemstr> roles = (List<Rolemstr>) obj_ro;
            for (Rolemstr rs : roles) {
                if (rs.getCode() != null) {
                	r.add(rs.getCode());
                }
            }
            if (!r.isEmpty()) { 
            	return r; 
            }
        }
        return null;
    }

    /**
     * 授权
     * @see org.apache.shiro.realm.AuthenticatingRealm#doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken)
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        log.info("doGetAuthenticationInfo...token.getPrincipal:" + authcToken.getPrincipal().toString());
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        log.info("doGetAuthenticationInfo...token.getUsername:" + token.getUsername());
        return new SimpleAuthenticationInfo(token.getUsername(), "", getName());
    }

}
