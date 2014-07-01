package com.wcs.tms.service.process.common;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.Validate;

import com.wcs.base.service.EntityService;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.ProcessFile;

@Stateless
public class FileUploadService implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Inject
    private EntityService entityService;

    /**
     * 
     * <p>Description: 删除流程实例下的文件记录</p>
     * @param processId
     * @throws RuntimeException
     */
    public void deleteProcessFile(String processId) throws ServiceException {
        Validate.notNull(processId, "流程实例Id为空");
        try {
            // 删除该流程实例下之前的文件记录
            StringBuilder bulder = new StringBuilder("delete from ProcessFile f where f.procInstId=?1 ");
            entityService.batchExecute(bulder.toString(), processId);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
    
    /**
     * 
     * <p>Description: 删除一个流程下的具体文档</p>
     * @param processId
     * @param ceDocId
     * @throws ServiceException
     */
    public void deleteProcessFile(String processId, String ceDocId) throws ServiceException {
        Validate.notNull(processId, "流程实例Id为空");
        Validate.notNull(ceDocId, "文件Id为空");
        try {
            // 删除该流程实例下之前的文件记录
            StringBuilder bulder = new StringBuilder("delete from ProcessFile f where f.procInstId=?1 and f.ceDocId=?2");
            entityService.batchExecute(bulder.toString(), processId, ceDocId);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 查询该流程的附件</p>
     * @param processId
     * @return
     */
    public List<ProcessFile> findProceeFileList(String processId) {
        Validate.notNull(processId, "流程实例Id为空");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select p from ProcessFile p where p.procInstId=?1 ");
            return this.entityService.find(bulder.toString(), processId);
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
