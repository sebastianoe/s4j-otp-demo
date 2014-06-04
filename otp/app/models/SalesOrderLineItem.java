package models;

import java.sql.Date;

public class SalesOrderLineItem {
	private long id;
	private SalesOrder salesOrder;
	private Product product;
	private OutboundDelivery outbounddelivery;
	private int quantity;
	private double price;
	private Date promiseDate;

	public SalesOrderLineItem() {}
	
	public SalesOrderLineItem(long id, SalesOrder salesOrder, Product product,
			OutboundDelivery outbounddelivery, int quantity, double price,
			Date promiseDate) {
		this.id = id;
		this.salesOrder = salesOrder;
		this.product = product;
		this.outbounddelivery = outbounddelivery;
		this.quantity = quantity;
		this.price = price;
		this.promiseDate = promiseDate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public SalesOrder getSalesOrder() {
		return salesOrder;
	}

	public void setSalesOrder(SalesOrder salesOrder) {
		this.salesOrder = salesOrder;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public OutboundDelivery getOutbounddelivery() {
		return outbounddelivery;
	}

	public void setOutbounddelivery(OutboundDelivery outbounddelivery) {
		this.outbounddelivery = outbounddelivery;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Date getPromiseDate() {
		return promiseDate;
	}

	public void setPromiseDate(Date promiseDate) {
		this.promiseDate = promiseDate;
	}
}
