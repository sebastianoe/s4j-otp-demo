package models;

import java.util.Date;

public class PaymentReceipt {
	private long id;
	private Invoice invoice;
	private Double value;
	private Date paymentdate;

	public PaymentReceipt() {}
	
	public PaymentReceipt(long id, Invoice invoice, Double value,
			Date paymentdate) {
		this.id = id;
		this.invoice = invoice;
		this.value = value;
		this.paymentdate = paymentdate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Date getPaymentdate() {
		return paymentdate;
	}

	public void setPaymentdate(Date paymentdate) {
		this.paymentdate = paymentdate;
	}

}
