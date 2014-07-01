package com.wcs.tms.service.system.sysprocess;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.ProcessDefine;
import com.wcs.tms.model.ProcessNode;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: 流程节点表单绑定Service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@Stateless
public class ProcessNodeService implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Log log = LogFactory.getLog(ProcessNodeService.class);
    @Inject
    private EntityService entityService;

    /**
     * 
     * <p>Description: 得到一个流程下面的所有流程节点</p>
     * @param prcessDefineId
     * @return
     * @throws RuntimeException
     */
    public List<ProcessNode> findProcessNodeList(Long prcessDefineId) throws ServiceException {
        Validate.notNull(prcessDefineId, "流程定义Id为空");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select pn from ProcessNode pn where pn.processDefine.id=?1 and pn.defunctInd = 'N' and pn.status='Y'");
            return this.entityService.find(bulder.toString(), prcessDefineId);
        } catch (Exception e) {
            log.error("findProcessNodeList方法 错误信息：" + e.getMessage());
            throw new ServiceException("findProcessNodeList方法 错误信息：" + e.getMessage());
        }
    }

    /**
     * 
     * <p>Description: 保存流程节点</p>
     * @param list
     * @param processDefineId
     * @throws RuntimeException
     */
    public void batchSaveProcessNode(List<ProcessNode> list, Long processDefineId) throws ServiceException {
        Validate.notNull(processDefineId, "流程定义Id为空");
        try {
            // 判断是否需要删除 通过比较内存的对象与受管对象
            List<ProcessNode> dataList = this.findProcessNodeList(processDefineId);
            if (!dataList.isEmpty()) {
                for (ProcessNode no : dataList) {
                    if (!list.contains(no)) {
                        no.setDefunctInd("Y");
                        no.setStatus("N");
                        this.entityService.update(no);
                    }
                }
            }
            // 保存或者更新现有集合
            if (list.isEmpty()) { 
                return; 
            }
            for (ProcessNode prn : list) {
                if (prn.getId() == null) {
                    ProcessDefine processDefine = this.entityService.find(ProcessDefine.class, processDefineId);
                    prn.setProcessDefine(processDefine);
                    this.entityService.create(prn);
                } else {
                    this.entityService.update(prn);
                }
            }
        } catch (Exception e) {
            log.error("batchSaveProcessNode方法 错误信息：" + e.getMessage());
            throw new ServiceException("batchSaveProcessNode方法 错误信息：" + e.getMessage());
        }
    }
}
