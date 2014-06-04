package models;

import java.sql.Date;

public class Invoice {
	private long id;
	private OutboundDelivery outboundDelivery;
	private Date billingDate;
	private Date paid;
	private Double amount;
	private Customer customer;
	private SalesOrder salesOrder;
	private int overdueNoticeCount;

	public Invoice() {}
	
	public Invoice(long id, OutboundDelivery outboundDelivery, Date billingDate, Customer customer, Double amount) {
		this.id = id;
		this.outboundDelivery = outboundDelivery;
		this.billingDate = billingDate;
		this.customer = customer;
		this.amount = amount;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public OutboundDelivery getOutboundDelivery() {
		return outboundDelivery;
	}

	public void setOutboundDelivery(OutboundDelivery outboundDelivery) {
		this.outboundDelivery = outboundDelivery;
	}

	public Date getBillingDate() {
		return billingDate;
	}

	public void setBillingDate(Date billingDate) {
		this.billingDate = billingDate;
	}

	public Date getPaid() {
		return paid;
	}

	public void setPaid(Date paid) {
		this.paid = paid;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public SalesOrder getSalesOrder() {
		return salesOrder;
	}

	public void setSalesOrder(SalesOrder salesOrder) {
		this.salesOrder = salesOrder;
	}

	public int getOverdueNoticeCount() {
		return overdueNoticeCount;
	}

	public void setOverdueNoticeCount(int overdueNoticeCount) {
		this.overdueNoticeCount = overdueNoticeCount;
	}
}
