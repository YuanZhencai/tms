package com.wcs.common.filenet.ce;

import java.util.Iterator;
import java.util.Vector;
import javax.security.auth.Subject;
import com.filenet.api.collection.ObjectStoreSet;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.UserContext;

/**
 * This object represents the connection with
 * the Content Engine. Once connection is established
 * it intializes Domain and ObjectStoreSet with
 * available Domain and ObjectStoreSet.
 * 
 */
public class CEConnection {
    private Connection con;
    private Domain dom;
    private String domainName;
    private ObjectStoreSet ost;
    private Vector osnames;
    private boolean isConnected;
    private UserContext uc;

    /*
     * constructor
     */
    public CEConnection() {
        con = null;
        uc = UserContext.get();
        dom = null;
        domainName = null;
        ost = null;
        osnames = new Vector();
        isConnected = false;
    }

    /**
     * 
     * <p>Description: CE 登录</p>
     * @param userName
     * @param password
     * @param stanza
     * @param uri
     */
    public void establishConnection(String userName, String password, String stanza, String uri) {
        con = Factory.Connection.getConnection(uri);
        Subject sub = UserContext.createSubject(con, userName, password, stanza);
        uc.pushSubject(sub);
        dom = fetchDomain();
        domainName = dom.get_Name();
        ost = getOSSet();
        isConnected = true;
    }

    /*
     * Returns Domain object.
     */
    public Domain fetchDomain() {
        dom = Factory.Domain.fetchInstance(con, null, null);
        return dom;
    }

    /*
     * Returns ObjectStoreSet from Domain
     */
    public ObjectStoreSet getOSSet() {
        ost = dom.get_ObjectStores();
        return ost;
    }

    /*
     * Returns vector containing ObjectStore names from object stores available in ObjectStoreSet.
     */
    public Vector getOSNames() {
        if (osnames.isEmpty()) {
            Iterator it = ost.iterator();
            while (it.hasNext()) {
                ObjectStore os = (ObjectStore) it.next();
                osnames.add(os.get_DisplayName());
            }
        }
        return osnames;
    }

    /**
     * 
     * <p>Description:是否連接 </p>
     * @return
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * 
     * <p>Description: 得到ObjectStore</p>
     * @param name
     * @return
     */
    public ObjectStore fetchOS(String name) {
        return Factory.ObjectStore.fetchInstance(dom, name, null);
    }

    public Domain fetchDomain(Connection ceConn) {
        dom = Factory.Domain.fetchInstance(ceConn, null, null);
        return dom;
    }
    
    public String getDomainName() {
        return domainName;
    }

}
