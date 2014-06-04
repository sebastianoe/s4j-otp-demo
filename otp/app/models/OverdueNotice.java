package models;

import java.util.Date;

public class OverdueNotice {
	private long id;
	private Customer customer;
	private Invoice invoice;
	private Double amount;
	private Date date;

	public OverdueNotice() {}
	
	public OverdueNotice(long id, Customer customer, Invoice invoice,
			Double amount, Date date) {
		this.id = id;
		this.customer = customer;
		this.invoice = invoice;
		this.amount = amount;
		this.date = date;
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

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
