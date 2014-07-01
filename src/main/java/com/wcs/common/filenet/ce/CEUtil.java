package com.wcs.common.filenet.ce;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.filenet.api.collection.AccessPermissionList;
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.EngineCollection;
import com.filenet.api.collection.FolderSet;
import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.ComponentRelationshipType;
import com.filenet.api.constants.CompoundDocumentState;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.constants.VersionBindType;
import com.filenet.api.core.ComponentRelationship;
import com.filenet.api.core.Containable;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.CustomObject;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.property.Properties;
import com.filenet.api.property.Property;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.security.AccessPermission;
import com.filenet.api.util.Id;
import com.filenet.apiimpl.core.FolderImpl;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: </p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
public class CEUtil {
	
	private static Log log = LogFactory.getLog(CEUtil.class);
	
	private CEUtil() {
		
	}
	
    /**
     * 
     * <p>Description: 通过CE下载文档</p>
     * @param doc
     * @param path
     */
    public static void writeDocContentToFile(Document doc, String path) {
        String fileName = doc.get_Name();
        File f = new File(path, fileName);
        InputStream is = doc.accessContentStream(0);
        int c = 0;
        try {
            FileOutputStream out = new FileOutputStream(f);
            c = is.read();
            while (c != -1) {
                out.write(c);
                c = is.read();
            }
            is.close();
            out.close();
        } catch (IOException e) {
        	log.error("writeDocContentToFile方法 通过CE下载文档出现异常", e);
        }
    }

    /**
     * 
     * <p>Description: 将文件内容读取到字节数组</p>
     * @param f
     * @return
     */
    public static byte[] readDocContentFromFile(File f) {
        FileInputStream is;
        byte[] b = null;
        int fileLength = (int) f.length();
        if (fileLength != 0) {
            try {
                is = new FileInputStream(f);
                b = new byte[fileLength];
                is.read(b);
                is.close();
            } catch (FileNotFoundException e) {
            	log.error("readDocContentFromFile方法 将文件内容读取到字节数组出现异常", e);
            } catch (IOException e) {
            	log.error("readDocContentFromFile方法 将文件内容读取到字节数组出现异常", e);
            }
        }
        return b;
    }

    /**
     * 
     * <p>Description:创建 Transfer</p>
     * @param by
     * @param fileName
     * @return
     */
    public static ContentTransfer createContentTransfer(byte[] by, String fileName) {
        ContentTransfer ctNew = null;
        if (by != null) {
            ctNew = Factory.ContentTransfer.createInstance();
            ByteArrayInputStream is = new ByteArrayInputStream(by);
            ctNew.setCaptureSource(is);
            ctNew.set_RetrievalName(fileName);
        }
        return ctNew;
    }

    /*
     * Creates the ContentElementList from ContentTransfer object.
     */
    public static ContentElementList createContentElements(byte[] by, String fileName) {
        ContentElementList cel = null;
        if (createContentTransfer(by, fileName) != null) {
            cel = Factory.ContentElement.createList();
            ContentTransfer ctNew = createContentTransfer(by, fileName);
            cel.add(ctNew);
        }
        return cel;
    }

    /**
     * 
     * <p>Description:创建有内容的文档 </p>
     * @param by 文件字节数组
     * @param fileName 文件名
     * @param mimeType 文件类型
     * @param os 
     * @param docClass 
     * @return
     */
    public static Document createDocWithContent(byte[] by, String fileName, String mimeType, ObjectStore os, String docClass) {
        Document doc = null;
        if (docClass.equals("")) {
        	doc = Factory.Document.createInstance(os, null);
        }else  {
        	doc = Factory.Document.createInstance(os, docClass);
        }
        doc.getProperties().putValue("DocumentTitle", fileName);
        doc.set_MimeType(mimeType);
        ContentElementList cel = CEUtil.createContentElements(by, fileName);
        if (cel != null) {
        	doc.set_ContentElements(cel);
        }
        return doc;
    }

    /**
     * 
     * <p>Description: 创建无内容文档</p>
     * @param mimeType
     * @param os
     * @param docName
     * @param docClass
     * @return
     */
    public static Document createDocNoContent(String mimeType, ObjectStore os, String docName, String docClass) {
        Document doc = null;
        if (docClass.equals("")) {
        	doc = Factory.Document.createInstance(os, null);
        }else {
        	doc = Factory.Document.createInstance(os, docClass);
        }
        doc.getProperties().putValue("DocumentTitle", docName);
        doc.set_MimeType(mimeType);
        return doc;
    }

    /**
     * 
     * <p>Description: 通过路径读取文档</p>
     * @param os
     * @param path
     * @return
     */
    public static Document fetchDocByPath(ObjectStore os, String path) {
        return Factory.Document.fetchInstance(os, path, null);
    }

    /**
     * 
     * <p>Description:通过ID获取文档</p>
     * @param os
     * @param id
     * @return
     */
    public static Document fetchDocById(ObjectStore os, String id) {
        Id id1 = new Id(id);
        return Factory.Document.fetchInstance(os, id1, null);
    }

    /**
     * 
     * <p>Description: 检查文档</p>
     * @param doc
     */
    public static void checkinDoc(Document doc) {
        doc.checkin(AutoClassify.AUTO_CLASSIFY, CheckinType.MINOR_VERSION);
        doc.save(RefreshMode.REFRESH);
        doc.refresh();
    }

    /**
     * 
     * <p>Description: 创建CustomObject</p>
     * @param os
     * @param className
     * @return
     */
    public static CustomObject createCustomObject(ObjectStore os, String className) {
        CustomObject co = null;
        if (className.equals("")) {
        	co = Factory.CustomObject.createInstance(os, null);
        }else{
        	co = Factory.CustomObject.createInstance(os, className);
        }
        return co;
    }

    /**
     * 
     * <p>Description: 通过路径得到CustomObject</p>
     * @param os
     * @param path
     * @return
     */
    public static CustomObject fetchCustomObjectByPath(ObjectStore os, String path) {
        return Factory.CustomObject.fetchInstance(os, path, null);
    }

    /**
     * 
     * <p>Description:通过ID抓取CustomObject </p>
     * @param os
     * @param id
     * @return
     */
    public static CustomObject fetchCustomObjectById(ObjectStore os, String id) {
        Id id1 = new Id(id);
        return Factory.CustomObject.fetchInstance(os, id1, null);
    }

    /**
     * 
     * <p>Description: 将文档内容填充到指定的文件夹</p>
     * @param os
     * @param o
     * @param folderPath
     * @return
     */
    public static ReferentialContainmentRelationship fileObject(ObjectStore os, Containable o, String folderPath) {
        Folder fo = Factory.Folder.fetchInstance(os, folderPath, null);
        ReferentialContainmentRelationship rcr;
        if (o instanceof Document) {
        	rcr = fo.file((Document) o, AutoUniqueName.AUTO_UNIQUE, ((Document) o).get_Name(),
        			DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
        }else {
        	rcr = fo.file((CustomObject) o, AutoUniqueName.AUTO_UNIQUE, ((CustomObject) o).get_Name(),
                DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
        }
        return rcr;
    }

    /**
     * 
     * <p>Description: 在指定文件夹路径下创建指定的文件夹名称</p>
     * @param os
     * @param fPath
     * @param fName
     * @param className
     */
    public static void createFolder(ObjectStore os, String fPath, String fName, String className) {
        Folder f = Factory.Folder.fetchInstance(os, fPath, null);
        Folder nf = null;
        if (className.equals("")) {
        	nf = Factory.Folder.createInstance(os, null);
        }else{
        	nf = Factory.Folder.createInstance(os, className);
        }
        nf.set_Parent(f);
        //拷贝父文件夹的权限和安全策略到子文件夹
        nf.set_Permissions(f.get_Permissions());
        nf.set_SecurityPolicy(f.get_SecurityPolicy());
        nf.set_InheritParentPermissions(true);
        
        nf.set_FolderName(fName);
        nf.save(RefreshMode.REFRESH);
    }

    /**
     * 
     * <p>Description: 判断指定文件夹名称是否存在</p>
     * @param os
     * @param rootPath
     * @param fName
     * @return
     */
    public static boolean existFolder(ObjectStore os, String rootPath, String fName) {
        boolean flag = false;
        Folder f = Factory.Folder.fetchInstance(os, rootPath, null);
        // 得到CE根路径下的所有已经存在的文件夹
        FolderSet set = f.get_SubFolders();
        Iterator<FolderImpl> it = set.iterator();
        // 循环判断文件夹是否已经存在
        while (it.hasNext()) {
            FolderImpl folder = (FolderImpl) it.next();
            log.info("文件夹名字 ---"+folder.get_FolderName());
            if (folder.get_FolderName().equals(fName)) {
                flag = true;
            }
        }
        return flag;
    }

    /*
     * Retrives some of the properties of Containable object (i.e. Document, CustomObject) and stores them in a HashMap, with
     * property name as key and property value as value.
     */
    public static HashMap getContainableObjectProperties(Containable o) {
        HashMap hm = new HashMap();
        hm.put("Id", o.get_Id().toString());
        hm.put("Name", o.get_Name());
        hm.put("Creator", o.get_Creator());
        hm.put("Owner", o.get_Owner());
        hm.put("Date Created", o.get_DateCreated().toString());
        hm.put("Date Last Modified", o.get_DateLastModified().toString());
        return hm;
    }

    /*
     * Creates the CompoundDocument (i.e. ComponentRelationship object).
     */
    public static ComponentRelationship createComponentRelationship(ObjectStore os, String pTitle, String cTitle) {
        ComponentRelationship cr = null;
        Document parentDoc = null;
        Document childDoc = null;

        parentDoc = Factory.Document.createInstance(os, null);
        parentDoc.getProperties().putValue("DocumentTitle", pTitle);
        parentDoc.set_CompoundDocumentState(CompoundDocumentState.COMPOUND_DOCUMENT);
        parentDoc.save(RefreshMode.REFRESH);
        parentDoc.checkin(AutoClassify.AUTO_CLASSIFY, CheckinType.MINOR_VERSION);
        parentDoc.save(RefreshMode.REFRESH);

        childDoc = Factory.Document.createInstance(os, null);
        childDoc.getProperties().putValue("DocumentTitle", cTitle);
        childDoc.set_CompoundDocumentState(CompoundDocumentState.COMPOUND_DOCUMENT);
        childDoc.save(RefreshMode.REFRESH);
        childDoc.checkin(AutoClassify.AUTO_CLASSIFY, CheckinType.MINOR_VERSION);
        childDoc.save(RefreshMode.REFRESH);

        cr = Factory.ComponentRelationship.createInstance(os, null);
        cr.set_ParentComponent(parentDoc);
        cr.set_ChildComponent(childDoc);
        cr.set_ComponentRelationshipType(ComponentRelationshipType.DYNAMIC_CR);
        cr.set_VersionBindType(VersionBindType.LATEST_VERSION);

        return cr;
    }

    /*
     * Retrives the CompoundDocument object using supplied ID.
     */
    public static ComponentRelationship fetchComponentRelationship(ObjectStore os, String id) {
        Id id1 = new Id(id);
        return Factory.ComponentRelationship.fetchInstance(os, id1, null);
    }

    /*
     * Retrives the some of the properties of CompoundDocument (i.e. ComponentRelationship object) and stores them in HashMap,
     * property name as key and property value as value.
     */
    public static HashMap getComponentRelationshipObjectProperties(ComponentRelationship o) {
        HashMap hm = new HashMap();
        hm.put("Id", o.get_Id().toString());
        hm.put("Creator", o.get_Creator());
        hm.put("Date Created", o.get_DateCreated().toString());
        hm.put("Date Last Modified", o.get_DateLastModified().toString());
        hm.put("Child Component", o.get_ChildComponent().get_Name());
        hm.put("Parent Component", o.get_ParentComponent().get_Name());
        return hm;
    }

    /*
     * Retrives the RepositoryRowSet (result of querying Content Engine). Query is constructed from supplied select, from, and
     * where clause using SearchSQL object. Then it creates the SearchScope object using supplied ObjectStore, and queries the
     * Content Engine using fetchRows method of SearchScope object.
     */
    public static RepositoryRowSet fetchResultsRowSet(ObjectStore os, String select, String from, String where, int rows) {
        RepositoryRowSet rrs = null;
        SearchSQL q = new SearchSQL();
        SearchScope ss = new SearchScope(os);
        q.setSelectList(select);
        q.setFromClauseInitialValue(from, null, false);
        if (!where.equals("")) {
            q.setWhereClause(where);
        }
        if (!(rows == 0)) {
            q.setMaxRecords(rows);
        }
        rrs = ss.fetchRows(q, null, null, null);
        return rrs;
    }

    /*
     * Gets column names to display in JTable. It takes RepositoryRow as argument
     */
    public static Vector getResultProperties(RepositoryRow rr) {
        Vector column = new Vector();
        Properties ps = rr.getProperties();
        Iterator it = ps.iterator();

        while (it.hasNext()) {
            Property pt = (Property) it.next();
            String name = pt.getPropertyName();
            column.add(name);
        }
        return column;
    }

    /*
     * Retrives the properties from supplied RepositoryRow, stores them in vector, and returns it.
     */
    public static Vector getResultRow(RepositoryRow rr) {
        Vector row = new Vector();
        Properties ps = rr.getProperties();
        Iterator it = ps.iterator();

        while (it.hasNext()) {
            Property pt = (Property) it.next();
            Object value = pt.getObjectValue();
            if (value == null) {
                row.add("null");
            } else if (value instanceof EngineCollection) {
                row.add("*");
            } else {
                row.add(value.toString());
            }
        }
        return row;
    }

    /*
     * Retrives the access permission list for a Containable object, stores it in Vector, and returns it.
     */
    public static Vector getPermissions(Containable co) {
        Vector permissions = new Vector();
        AccessPermissionList apl = co.get_Permissions();
        Iterator iter = apl.iterator();
        while (iter.hasNext()) {
            AccessPermission ap = (AccessPermission) iter.next();
            permissions.add("GRANTEE_NAME: " + ap.get_GranteeName());
            permissions.add("ACCESS_MASK: " + ap.get_AccessMask().toString());
            permissions.add("ACCESS_TYPE: " + ap.get_AccessType().toString());
        }
        return permissions;
    }
}
