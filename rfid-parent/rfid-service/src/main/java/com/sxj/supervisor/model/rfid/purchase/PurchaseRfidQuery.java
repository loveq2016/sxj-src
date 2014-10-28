package com.sxj.supervisor.model.rfid.purchase;

import java.io.Serializable;

import com.sxj.mybatis.pagination.Pagable;
import com.sxj.supervisor.enu.rfid.RfidTypeEnum;

public class PurchaseRfidQuery extends Pagable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 672220062430203127L;
	/**
	 * 采购单编号
	 */
	private String purchaseNo;

	/**
	 * 供应商编号
	 */
	private String supplierNo;

	/**
	 * 供应商名称
	 */
	private String supplierName;

	/**
	 * RFID类型
	 */
	private RfidTypeEnum rfidType;

	/**
	 * 数量
	 */
	private Long count;

	/**
	 * 单价
	 */
	private Long price;

	/**
	 * 金额
	 */
	private Long amount;

	/**
	 * 采购日期
	 */
	private String startDate;

	private String endDate;

	/**
	 * 招标合同号
	 */
	private String contractNo;

	/**
	 * 申请单编号
	 */
	private String applyNo;

	/**
	 * 导入状态
	 */
	private String importState;
	/**
	 * 支付状态
	 */
	private String payState;

	/**
	 * 收货状态
	 */
	private String receiptState;


	public String getPurchaseNo() {
		return purchaseNo;
	}

	public void setPurchaseNo(String purchaseNo) {
		this.purchaseNo = purchaseNo;
	}

	public String getSupplierNo() {
		return supplierNo;
	}

	public void setSupplierNo(String supplierNo) {
		this.supplierNo = supplierNo;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public RfidTypeEnum getRfidType() {
		return rfidType;
	}

	public void setRfidType(RfidTypeEnum rfidType) {
		this.rfidType = rfidType;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getApplyNo() {
		return applyNo;
	}

	public void setApplyNo(String applyNo) {
		this.applyNo = applyNo;
	}

	public String getImportState() {
		return importState;
	}

	public void setImportState(String importState) {
		this.importState = importState;
	}

	public String getPayState() {
		return payState;
	}

	public void setPayState(String payState) {
		this.payState = payState;
	}

	public String getReceiptState() {
		return receiptState;
	}

	public void setReceiptState(String receiptState) {
		this.receiptState = receiptState;
	}

}
