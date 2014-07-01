package com.wcs.tms.view.system.company;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.wcs.base.util.JSFUtils;
import com.wcs.base.view.ViewBaseBean;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.tms.model.ShareHolder;
import com.wcs.tms.service.system.company.ShareHolderService;
import com.wcs.tms.util.MessageUtils;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:股权信息ManageBean</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@ManagedBean
@ViewScoped
public class ShareHolderBean extends ViewBaseBean<ShareHolder> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /** 股权信息Table List*/
    private List<ShareHolder> shareHolderList = Lists.newArrayList();
    /** 股东信息分页模型*/
    private List<ShareHolder> shareHolderModel = Lists.newArrayList();
    /** 公司Id*/
    private Long compnyId;
    /** 资金币种下拉*/
    private List<SelectItem> currencySelect = new ArrayList<SelectItem>();
    /** 股东传输对象*/
    private ShareHolder shareHolder = new ShareHolder();
    /** 用户记录编辑之前的对象引用*/
    private ShareHolder shareHolderVo = new ShareHolder();
    /** 用于记录股东编辑前*/
    private ShareHolder shareHolderCopyEdit = new ShareHolder();
    /** 币别是否不可选*/
    private boolean currencyDisable;
    /** 币别类型(用于控制全局默认选中一个币别)*/
    private String fondsCurrency;
    /** 股东名称编辑时*/
    private String shareNameEdit;
    private final Logger logger = Logger.getLogger(ShareHolderBean.class);

    @Inject
    private ShareHolderService shareHolderService;
    @Inject
    private CommonBean dictBean;

    public ShareHolderBean() {
        // 1 list 界面
        this.setupPage("/faces/system/companyManage/comManage-list.xhtml",
                "/faces/system/companyManage/stockholder/stockHolder-list.xhtml", null);
    }

    @PostConstruct
    public void init() {
        Object obj = JSFUtils.getRequestParam("compId");
        if (obj != null) {
            try {
                compnyId = Long.parseLong(obj.toString());
                // 循环添加到List是OPENPA查询的是只读的集合
                List<ShareHolder> list = shareHolderService.findShareHolderListByCp(compnyId);
                if (!list.isEmpty()) {
                    shareHolderList.clear();
                    for (ShareHolder sh : list) {
                        shareHolderList.add(sh);
                    }
                    // 若数据已经存在股东了,则设置币别并变灰
                    fondsCurrency = list.get(0).getFondsCurrency();
                    getInstance().setFondsCurrency(fondsCurrency);
                    currencyDisable = true;
                }
                shareHolderModel = shareHolderList;
                currencySelect = dictBean.getDictByCode(DictConsts.TMS_TAX_PROJECT_CURRENCY_TYPE);
            } catch (Exception e) {
                logger.error("init方法 错误信息:" + e.getMessage());
            }
        }

    }

    /**
     * 
     * <p>Description: 逻辑添加股东信息到内存</p>
     */
    public void addShareHolderLogic() {
        this.logger.info("逻辑添加股东信息");
        if (fondsCurrency == null || "".equals(fondsCurrency)) {
            fondsCurrency = getInstance().getFondsCurrency();
        }
        // 验证股东名称
        int flag = validaeShareName();
        if (flag == 1) {
            MessageUtils.addErrorMessage("shareholderValidateMsg", "已经存在相同样的股东名称");
            return;
        }
        if (flag == 2) {
            MessageUtils.addErrorMessage("shareholderValidateMsg", "执行过删除股东信息，请先点击保存再添加同样的股东名称");
            return;
        }
        Double fondIn = getInstance().getFondsInPlace();
        if (fondIn == null) {
            fondIn = 0.0;
        }
        // 验证已到位金额是否小于注册金额
        if (getInstance().getFondsTotal() - fondIn < 0) {
            MessageUtils.addErrorMessage("shareholderMsgId", "已到位金额大于注册资本金总额");
            return;
        }
        shareHolderList.add(getInstance());
        // 计算各个股东所占比例 并返回有效的股东信息
        shareHolderList = equityRatioDivision();
        shareHolderModel = shareHolderList;
        currencyDisable = true;
        //add on 2013-12-2 by yan添加操作逻辑添加改为物理添加
        this.addShareHolderPhysical();
        MessageUtils.addSuccessMessage("shareholderMsgId", "添加股东信息成功");
        // 清空添加From信息
        this.setInstance(new ShareHolder());
        this.getInstance().setFondsCurrency(fondsCurrency);
    }

    /**
     * 
     * <p>Description: 验证股东名称是否唯一</p>
     */
    public int validaeShareName() {
        String shareName = getInstance().getShareHolderName();
        boolean flag = this.shareHolderService.findShareHolByName(shareName, compnyId);
        boolean sholderFlag = false;
        if (!shareHolderList.isEmpty()) {
            for (ShareHolder holder : shareHolderList) {
                if (holder.getCompany() != null && holder.getCompany().getId() != null
                        && holder.getCompany().getId().equals(compnyId)) {
                    if (shareName.equals(holder.getShareHolderName())) {
                        sholderFlag = true;
                        return 1;
                    }
                } else {
                    if (shareName.equals(holder.getShareHolderName())) {
                        sholderFlag = true;
                        return 1;
                    }
                }
            }
        }
        if (false == flag && sholderFlag == false) {
            //避免sonar检查
        } else {
            if (flag && sholderFlag == false) {
                for (ShareHolder holder : shareHolderList) {
                    if (holder.getShareHolderName().contains(shareName)) { 
                        return 1; 
                    }
                }
                return 2;
            }
            if (flag || sholderFlag) { 
                return 1; 
            }
        }

        return 0;
    }

    /**
     * 
     * <p>Description: 物理添加保存股東信息</p>
     * @return
     */
    public void addShareHolderPhysical() {
        try {
            this.shareHolderService.batchSaveShareHeold(shareHolderList, compnyId);
        } catch (Exception e) {
            logger.error("保存股东信息异常", e);
            MessageUtils.addErrorMessage("msg", "保存失败");
        }

    }

    /**
     * 
     * <p>Description: 编辑之前初始化数据</p>
     */
    public void beforeEdit() {
        Double fondinPlace = shareHolder.getFondsInPlace();
        if (fondinPlace == null) {
            fondinPlace = 0.0;
        }
        shareHolder.setFondsNotTotal(shareHolder.getFondsTotal() - fondinPlace);
        shareNameEdit = shareHolder.getShareHolderName();
        try {
            shareHolderVo = shareHolder;
            PropertyUtils.copyProperties(shareHolderCopyEdit, shareHolder);
            shareHolder = new ShareHolder();
            PropertyUtils.copyProperties(shareHolder, shareHolderCopyEdit);
        } catch (IllegalAccessException e) {
            logger.error("beforeEdit方法 错误信息：" + e.getMessage());
        } catch (InvocationTargetException e) {
            logger.error("beforeEdit方法 错误信息：" + e.getMessage());
        } catch (NoSuchMethodException e) {
            logger.error("beforeEdit方法 错误信息：" + e.getMessage());
        }
    }

    /**
     * 
     * <p>Description: 更新股东信息</p>
     */
    public void editShareHolder() {
        // 验证股东名称是否唯一
        boolean flag = this.shareHolderService.findShareHolByName(shareHolder.getShareHolderName(), compnyId);
        if (flag && shareNameEdit.equals(shareHolder.getShareHolderName())) {
            flag = false;
        }
        boolean flag2 = false;
        if (!shareHolderList.isEmpty()) {
            for (ShareHolder sh : shareHolderList) {
                if (sh.getCompany() != null && sh.getCompany().getId() != null) {
                    if (!shareNameEdit.equals(sh.getShareHolderName())
                            && sh.getShareHolderName().equals(shareHolder.getShareHolderName())
                            && sh.getCompany().getId().equals(compnyId)) {
                        flag2 = true;
                        break;
                    }
                } else {
                    if (!shareNameEdit.equals(sh.getShareHolderName())
                            && sh.getShareHolderName().equals(shareHolder.getShareHolderName())) {
                        flag2 = true;
                        break;
                    }
                }

            }
        }
        if (false == flag && flag2 == false) {
            //避免sonar检查
        } else {
            if (flag || flag2) {
                shareHolder.setShareHolderName(shareNameEdit);
                shareProperCopy();
                MessageUtils.addErrorMessage("shareHolderNameEdit", "已经存在相同样的股东名称");
                return;
            }
        }
        Double fondIn = shareHolder.getFondsInPlace();
        if (fondIn == null) {
            fondIn = 0.0;
        }
        // 验证已到位金额是否小于注册金额
        if (shareHolder.getFondsTotal() - fondIn < 0) {
            MessageUtils.addErrorMessage("fondsNotInPlaceEdit", "已到位金额大于注册资本金总额");
            return;
        }
        if (shareHolderList.contains(shareHolderVo)) {
            shareHolderList.remove(shareHolderVo);
        }
        shareHolderList.add(shareHolder);
        if (shareHolder.getId() == null) {
            // 重新计算股东的股权比例
            shareHolderList = equityRatioDivision();
        } else {
            // 查询股东信息列表
            // 重新计算股东的股权比例
            shareHolderList = equityRatioDivision();
            // 更新股东信息到数据库
            this.addShareHolderPhysical();
            // 查询更新后的股东信息
        }

        // 更新当前股东信息
        shareHolderModel = shareHolderList;
        MessageUtils.addSuccessMessage("shareholderMsgId", "修改股东信息成功");
    }

    /**
     * 
     * <p>Description: </p>
     */
    public void deleteShareHolder() {
        shareHolderList.remove(shareHolder);
        List<ShareHolder> list = equityRatioDivision();
        shareHolderList.clear();
        shareHolderList.addAll(list);
        shareHolderModel = shareHolderList;
        //add on 2013-12-2 by yan添加操作逻辑添加改为物理添加
        this.addShareHolderPhysical();
        MessageUtils.addSuccessMessage("shareholderMsgId", "删除股东信息成功");
    }

    /**
     * 
     * <p>Description: 计算股权比例</p>
     */
    public void equityCalculation(Long type) {
        // 得到公司下面股东
        List<ShareHolder> nList = Lists.newArrayList();
        ShareHolder shl = null;
        String msgId = "shareholderMsgId";
        // 注册资本金总额度
        double total = 0.0;
        // 股权比例
        double ratio = 0.0;
        if (type == 1) {
            shl = this.getInstance();
        } else {
            shl = shareHolder;
            msgId = "fondsNotInPlaceEdit";
        }
        Double infondplace = shl.getFondsInPlace();
        if (shl.getFondsInPlace() == null) {
            infondplace = 0.0;
        }
        if (shl.getFondsTotal() - infondplace < 0) {
            MessageUtils.addErrorMessage(msgId, "已到位金额大于注册资本金总额");
            return;
        }
        shl.setFondsNotTotal(shl.getFondsTotal() - infondplace);

        if (shareHolderList.isEmpty()) {
            shl.setEquityPerc(Double.valueOf(100));
            return;
        }
        nList.addAll(shareHolderList);
        if (type == 1) {
            total = calculationTotal(nList);
            // 已有的注册+现有需要注册的金额总和
            total = total + shl.getFondsTotal();
            // 新资本计算股东所占股权比例总和
            ratio = caculationTotal(nList, total);
        } else {
            total = calculationTotal(nList, shl.getId());
            // 已有的注册+现有需要注册的金额总和
            total = total + shl.getFondsTotal();
            // 新资本计算股东所占股权比例总和
            ratio = caculationTotal(nList, total, shl.getId());
        }
        // 新股东股权比例
        shl.setEquityPerc(Double.valueOf((1 - ratio) * 100));
    }

    /**
     * 1 已到位金额大于注册资本金总额 0 正确
     * <p>Description: 计算未注册资金</p>
     */
    public void calculationPlace(Long type) {
        ShareHolder shl = null;
        String msgId = "shareholderMsgId";
        if (type == 1) {
            shl = this.getInstance();
        } else {
            shl = shareHolder;
            msgId = "fondsNotInPlaceEdit";
        }
        Double infondplace = shl.getFondsInPlace();
        if (shl.getFondsInPlace() == null) {
            infondplace = 0.0;
        }
        if (shl.getFondsTotal() - infondplace < 0) {
            MessageUtils.addErrorMessage(msgId, "已到位金额大于注册资本金总额");
            return;
        }
        shl.setFondsNotTotal(shl.getFondsTotal() - infondplace);
    }

    

    /**
     * 
     * <p>Description: 公司列表维护按钮调用跳转到股东维护界面</p>
     * @return
     */
    public String goInput() {
        return this.getInputPage();
    }

    /**
     * 
     * <p>Description: 计算各个股东的股权比例</p>
     * @return
     */
    private List<ShareHolder> equityRatioDivision() {
        double total = calculationTotal(shareHolderList);
        // 更新列表的股董股权比例信息
        List<ShareHolder> list = new ArrayList<ShareHolder>();
        for (ShareHolder sh : shareHolderList) {
            sh.setEquityPerc(sh.getFondsTotal() / total * 100);
            list.add(sh);
        }
        return list;
    }

    private void shareProperCopy() {
        try {
            PropertyUtils.copyProperties(shareHolder, shareHolderCopyEdit);
        } catch (IllegalAccessException e) {
            logger.error("shareProperCopy方法 错误信息：" + e.getMessage());
        } catch (InvocationTargetException e) {
            logger.error("shareProperCopy方法 错误信息：" + e.getMessage());
        } catch (NoSuchMethodException e) {
            logger.error("shareProperCopy方法 错误信息：" + e.getMessage());
        }
    }

    /**
     * 
     * <p>Description: 总的注册金额,可以不计算指定的股东</p>
     * @param list
     * @return
     */
    private double calculationTotal(List<ShareHolder> list, Long... shareHolderId) {
        double total = 0.0;
        for (ShareHolder sh : list) {
            if (shareHolderId != null && shareHolderId.length != 0 && sh.getId() != null && sh.getId().equals(shareHolderId[0])) {
                continue;
            }
            total += sh.getFondsTotal();
        }
        return total;
    }

    /**
     * 
     * <p>Description: 计算股权比例，可以不计算指定的股东</p>
     * @param list
     * @param total
     * @param shareHolderId
     * @return
     */
    private double caculationTotal(List<ShareHolder> list, double total, Long... shareHolderId) {
        double ratio = 0.0;
        for (ShareHolder sh : list) {
            if (shareHolderId.length != 0 && sh.getId() != null && sh.getId().equals(shareHolderId[0])) {
                continue;
            }
            ratio = ratio + sh.getFondsTotal() / total;
        }
        return ratio;
    }

    public List<ShareHolder> getShareHolderList() {
        return shareHolderList;
    }

    public void setShareHolderList(List<ShareHolder> shareHolderList) {
        this.shareHolderList = shareHolderList;
    }

    public List<ShareHolder> getShareHolderModel() {
        return shareHolderModel;
    }

    public void setShareHolderModel(List<ShareHolder> shareHolderModel) {
        this.shareHolderModel = shareHolderModel;
    }

    public Long getCompnyId() {
        return compnyId;
    }

    public void setCompnyId(Long compnyId) {
        this.compnyId = compnyId;
    }

    public List<SelectItem> getCurrencySelect() {
        return currencySelect;
    }

    public void setCurrencySelect(List<SelectItem> currencySelect) {
        this.currencySelect = currencySelect;
    }

    public ShareHolder getShareHolder() {
        return shareHolder;
    }

    public void setShareHolder(ShareHolder shareHolder) {
        this.shareHolder = shareHolder;
    }

    public boolean isCurrencyDisable() {
        return currencyDisable;
    }

    public void setCurrencyDisable(boolean currencyDisable) {
        this.currencyDisable = currencyDisable;
    }

    public String getFondsCurrency() {
        return fondsCurrency;
    }

    public void setFondsCurrency(String fondsCurrency) {
        this.fondsCurrency = fondsCurrency;
    }

    public String getShareNameEdit() {
        return shareNameEdit;
    }

    public void setShareNameEdit(String shareNameEdit) {
        this.shareNameEdit = shareNameEdit;
    }

    public ShareHolder getShareHolderCopyEdit() {
        return shareHolderCopyEdit;
    }

    public void setShareHolderCopyEdit(ShareHolder shareHolderCopyEdit) {
        this.shareHolderCopyEdit = shareHolderCopyEdit;
    }

    public ShareHolder getShareHolderVo() {
        return shareHolderVo;
    }

    public void setShareHolderVo(ShareHolder shareHolderVo) {
        this.shareHolderVo = shareHolderVo;
    }

}
