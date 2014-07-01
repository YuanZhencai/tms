package com.wcs.tms.view.process.guarantee.vo;

/**
 * <p>Project: tms</p>
 * <p>Description: </p>
 * <p>Copyright © 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
public class GuaranteeVO {

    /*
     * 担保方信息
     */
    // 可担保总额度:净资产*1.5
    private Double totalGuarAmount;
    // 已担保总额度:已担保总额度（有效期内的经过审批的担保）+预担保总额度（经过【集团财务总监审批】并同意的担保）
    private Double alreadyTotalGuarAmount;
    // 累计担保额度(已担保总额度+本次担保额度)
    private Double allTotalGuarAmount;
    // 剩余可用担保额度(可担保总额度-已担保总额度-预担保总额度-审批中担保总额度（集团资金部审批完到集团财务总监审批完之间的担保）)
    private Double remainGuarAmount;
    // 审批中的额度(集团资金部审批完到集团财务总监审批完之间的担保)
    private Double reviewingGuarAmount;

    /*
     * 受保方信息
     */
    // 已接受担保总额度(已接受担保总额度+预担保总额度)
    private Double receivedGuarAmount;
    // 已向外担保总额度(担保其他公司的额度+预担保其他公司的额度)
    private Double outerProviderGuarAmount;
    // 审批中的额度
    private Double outerReviewingGuarAmount;
    // 已接受当前担保公司的额度
    private Double alreadyReceCurCompanyAmount;

    public Double getTotalGuarAmount() {
        return totalGuarAmount == null ? 0.00 : totalGuarAmount;
    }

    public void setTotalGuarAmount(Double totalGuarAmount) {
        this.totalGuarAmount = totalGuarAmount;
    }

    public Double getAlreadyTotalGuarAmount() {
        return alreadyTotalGuarAmount == null ? 0.00 : alreadyTotalGuarAmount;
    }

    public void setAlreadyTotalGuarAmount(Double alreadyTotalGuarAmount) {
        this.alreadyTotalGuarAmount = alreadyTotalGuarAmount;
    }

    public Double getAllTotalGuarAmount() {
        return allTotalGuarAmount ;
    }

    public void setAllTotalGuarAmount(Double allTotalGuarAmount) {
        this.allTotalGuarAmount = allTotalGuarAmount;
    }

    public Double getRemainGuarAmount() {
        return remainGuarAmount == null ? 0.00 : remainGuarAmount;
    }

    public void setRemainGuarAmount(Double remainGuarAmount) {
        this.remainGuarAmount = remainGuarAmount;
    }

    public Double getReviewingGuarAmount() {
        return reviewingGuarAmount == null ? 0.00 : reviewingGuarAmount;
    }

    public void setReviewingGuarAmount(Double reviewingGuarAmount) {
        this.reviewingGuarAmount = reviewingGuarAmount;
    }

    public Double getReceivedGuarAmount() {
        return receivedGuarAmount == null ? 0.00 : receivedGuarAmount;
    }

    public void setReceivedGuarAmount(Double receivedGuarAmount) {
        this.receivedGuarAmount = receivedGuarAmount;
    }

    public Double getOuterProviderGuarAmount() {
        return outerProviderGuarAmount == null ? 0.00 : outerProviderGuarAmount;
    }

    public void setOuterProviderGuarAmount(Double outerProviderGuarAmount) {
        this.outerProviderGuarAmount = outerProviderGuarAmount;
    }

    public Double getOuterReviewingGuarAmount() {
        return outerReviewingGuarAmount == null ? 0.00 : outerReviewingGuarAmount;
    }

    public void setOuterReviewingGuarAmount(Double outerReviewingGuarAmount) {
        this.outerReviewingGuarAmount = outerReviewingGuarAmount;
    }

    public Double getAlreadyReceCurCompanyAmount() {
        return alreadyReceCurCompanyAmount == null ? 0.00 : alreadyReceCurCompanyAmount;
    }

    public void setAlreadyReceCurCompanyAmount(Double alreadyReceCurCompanyAmount) {
        this.alreadyReceCurCompanyAmount = alreadyReceCurCompanyAmount;
    }

}
