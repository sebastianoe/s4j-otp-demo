package models;

import java.sql.Date;

import org.joda.time.LocalDate;

public class SalesOrder {
	private long id;
	private Customer customer;
	private PaymentTerm paymentTerm;
	private int priority;
	private Double discountFactor;
	private String status;
	private Date minPromiseDate;
	private Date maxPromiseDate;
	private Double amount;
	private OutboundDelivery outboundDelivery;

	// empty default constructor
	public SalesOrder() {}
	
	public SalesOrder(long id, Customer customer, PaymentTerm paymentTerm,
			int priority, Double discountFactor, String status, Date minPromiseDate, Date maxPromiseDate) {
		this.id = id;
		this.customer = customer;
		this.paymentTerm = paymentTerm;
		this.priority = priority;
		this.discountFactor = discountFactor;
		this.status = status;
		this.minPromiseDate = minPromiseDate;
		this.maxPromiseDate = maxPromiseDate;
	}
	
	public SalesOrder(long id, Customer customer, Date minDate, Date maxDate) {
		this(id, customer, null, 0, 0d, null, minDate, maxDate);
	}
	
	public SalesOrder(long id, Customer customer, PaymentTerm paymentTerm,
			int priority, Double discountFactor, String status) {
		this(id, customer, paymentTerm, priority, discountFactor, status, null, null);
	}

	public String getRequestedDeliveryDateString() {
		return minPromiseDate + " - " + maxPromiseDate;
	}
	
	public boolean isPostable() {
		return !LocalDate.now().isBefore(LocalDate.fromDateFields(getMaxPromiseDate()));
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public PaymentTerm getPaymentTerm() {
		return paymentTerm;
	}

	public void setPaymentTerm(PaymentTerm paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Double getDiscountFactor() {
		return discountFactor;
	}

	public void setDiscountFactor(Double discountFactor) {
		this.discountFactor = discountFactor;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getMinPromiseDate() {
		return minPromiseDate;
	}

	public void setMinPromiseDate(Date minPromiseDate) {
		this.minPromiseDate = minPromiseDate;
	}

	public Date getMaxPromiseDate() {
		return maxPromiseDate;
	}

	public void setMaxPromiseDate(Date maxPromiseDate) {
		this.maxPromiseDate = maxPromiseDate;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public OutboundDelivery getOutboundDelivery() {
		return outboundDelivery;
	}

	public void setOutboundDelivery(OutboundDelivery outboundDelivery) {
		this.outboundDelivery = outboundDelivery;
	}
}
