package controllers;

import helpers.SqlConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.Customer;
import models.Invoice;
import models.PaymentReceipt;
import models.Product;
import models.SalesOrder;
import models.SalesOrderLineItem;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

import play.libs.Json;

import play.mvc.Controller;
import play.mvc.Result;

import views.html.invoices.paymentReceipts;
import views.html.invoices.dunning;

public class InvoiceController extends Controller {
	public static Result showPaymentReceipts() {
		return ok(paymentReceipts.render());
	}
	
	public static Result searchInvoices(String query) {
		List<Invoice> invoices = SQL[
            SELECT i.id, c.id AS customerid, c.name, i.billingdate, SUM(soli.price) * so.discountfactor * 1.19 + MAX(p.shippingcost) AS amount FROM Invoice i JOIN OutboundDelivery od ON i.outboundid = od.id JOIN SalesOrderLineItem soli ON soli.outboundid = od.id JOIN SalesOrder so ON soli.salesorderid = so.id JOIN Customer c ON so.customerid = c.id JOIN Product p ON soli.productid = p.id WHERE i.id = $query$ OR c.id = $query$ OR c.name LIKE $"%" + query + "%"$ GROUP BY i.id];
		
		return ok(Json.toJson(invoices));
	}
	
	public static Result invoiceDetails() {
		JsonNode json = request().body().asJson();
		long invoiceId = json.get("invoiceId").asLong();
		
		List<SalesOrderLineItem> lineItems = SQL[
	        SELECT soli.quantity, p.name, soli.price
	        FROM SalesOrderLineItem soli
	        JOIN Product p ON soli.productid = p.id
	        JOIN OutboundDelivery od ON soli.outboundid = od.id
	        JOIN Invoice i ON i.outboundid = od.id
	        LEFT OUTER JOIN OverdueNotice odn ON odn.invoiceid = i.id
	        WHERE i.id = $invoiceId$];
		
		List<PaymentReceipt> paymentReceipts = SQL[
            SELECT id, `value`, paymentdate
            FROM PaymentReceipt
            WHERE invoiceid = $invoiceId$];
		
		Double daysOffsetDiscount = getDaysOffsetDiscount(invoiceId);
		
		Integer overdueCount = SQL[
                SELECT COUNT(*) AS cnt FROM OverdueNotice WHERE invoiceid = $invoiceId$];
		
		Date paidDate = SQL[SELECT paid FROM Invoice WHERE id = $invoiceId$];
		Boolean paid = paidDate != null;
		
		ObjectNode result = Json.newObject();
		result.set("overdueCount", Json.toJson(overdueCount));
		result.set("lineItems", Json.toJson(lineItems));
		result.set("paymentReceipts", Json.toJson(paymentReceipts));
		result.set("daysOffsetDiscount", Json.toJson(daysOffsetDiscount));
		result.set("paid", Json.toJson(paid));
		
		return ok(result);
	}
	
	private static Double getDaysOffsetDiscount(long invoiceId) {
		return SQL[
		    SELECT df FROM (SELECT (DATEDIFF(MAX(COALESCE(pr.paymentdate, CURRENT_DATE())), i.billingdate) - pc.daysoffset) AS diff, pc.discountfactor AS df FROM Invoice i LEFT OUTER JOIN PaymentReceipt pr ON i.id = pr.invoiceid JOIN OutboundDelivery od ON i.outboundid = od.id JOIN SalesOrderLineItem soli ON soli.outboundid = od.id JOIN SalesOrder so ON so.id = soli.salesorderid JOIN PaymentTerm pt ON pt.id = so.paymenttermid JOIN PaymentConstraint pc ON pc.paymenttermid = pt.id WHERE i.id = $invoiceId$ GROUP BY pc.id HAVING diff >= 0 OR diff IS NULL ORDER BY diff LIMIT 1) AS sub];
	}
	
	public static Result customerDetails() {
		JsonNode json = request().body().asJson();
		long customerId = json.get("customerId").asLong();
		
		Customer customer = CustomerController.getCustomer(customerId);
		return ok(Json.toJson(customer));
	}
	
	public static Result addPaymentReceipt() {
		JsonNode json = request().body().asJson();
		long invoiceId = json.get("invoiceId").asLong();
		Double amount = json.get("amount").asDouble();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date billingDate = null;
		
		try {
			billingDate = sdf.parse(json.get("date").asText());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		
		SQL[
		    INSERT INTO PaymentReceipt (invoiceid, `value`, paymentdate) VALUES ($invoiceId$, $amount$, $new java.sql.Date(billingDate.getTime())$)];
		long insertId = SQL[SELECT LAST_INSERT_ID()];
		
		ObjectNode node = Json.newObject();
		node.set("id", Json.toJson(insertId));
		return ok(node);
	}
	
	public static Result deletePaymentReceipt() {
		JsonNode json = request().body().asJson();
		Long prId = json.get("prId").asLong();
		
		SQL[DELETE FROM PaymentReceipt WHERE id = $prId$];
		
		return ok(Json.toJson("Success"));
	}
	
	public static Result setPaid() {
		JsonNode json = request().body().asJson();
		boolean paid = json.get("paid").asBoolean();
		long invoiceId = json.get("invoiceId").asLong();
		
		if (paid) {
			SQL[
			    UPDATE Invoice SET paid = CURRENT_DATE() WHERE id = $invoiceId$];
		} else {
			SQL[UPDATE Invoice SET paid = NULL WHERE id = $invoiceId$];
		}
		
		return ok(Json.toJson("Success"));
	}
	
	public static Result daysDiscount() {
		JsonNode json = request().body().asJson();
		Long invoiceId = json.get("invoiceId").asLong();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date maxPrDate = null;
		
		try {
			maxPrDate = sdf.parse(json.get("maxPaymentReceiptDate").asText());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		
		double daysOffsetDiscount = SQL[
		        SELECT discountfactor FROM (SELECT ((DATEDIFF($new java.sql.Date(maxPrDate.getTime())$, i.billingdate)) - pc.daysoffset) AS diff, pc.discountfactor FROM Invoice i JOIN PaymentReceipt pr ON i.id = pr.invoiceid JOIN OutboundDelivery od ON i.outboundid = od.id JOIN SalesOrderLineItem soli ON soli.outboundid = od.id JOIN SalesOrder so ON so.id = soli.salesorderid JOIN PaymentTerm pt ON pt.id = so.paymenttermid JOIN PaymentConstraint pc ON pc.paymenttermid = pt.id WHERE i.id = $invoiceId$ GROUP BY pc.id HAVING diff >= 0 ORDER BY diff ASC LIMIT 1) AS sub];
		
		ObjectNode result = Json.newObject();
		result.set("daysOffsetDiscount", Json.toJson(daysOffsetDiscount));
		
		return ok(result);
	}
	
	public static Result showDunning() {
		List<Invoice> invoices = SQL[
            SELECT i.id, so.id AS salesorderid, c.id AS customerid, c.name, i.billingdate, MAX(soli.promisedate) AS salesordermaxpromisedate, SUM(soli.price) * so.discountfactor * 1.19 + MAX(p.shippingcost) AS amount, (SELECT COUNT(*)  FROM OverdueNotice WHERE invoiceid = i.id) AS overduenoticecoun FROM Invoice i JOIN OutboundDelivery od ON i.outboundid = od.id JOIN SalesOrderLineItem soli ON soli.outboundid = od.id JOIN SalesOrder so ON soli.salesorderid = so.id JOIN Customer c ON so.customerid = c.id JOIN Product p ON soli.productid = p.id JOIN PaymentTerm pt ON so.paymenttermid = pt.id JOIN PaymentConstraint pc ON pc.paymenttermid = pt.id WHERE DATEDIFF(CURRENT_DATE(), i.billingdate) > (SELECT MIN(daysoffset) FROM PaymentConstraint WHERE discountfactor > 1 AND i.paid IS NULL) GROUP BY i.id];
		
		return ok(dunning.render(invoices));
	}
	
	public static Result dun() {
		JsonNode json = request().body().asJson();
		long invoiceId = json.get("invoiceId").asLong();
		long customerId = json.get("customerId").asLong();
		Double netAmount = json.get("netAmount").asDouble();
		Double daysOffsetDiscount = getDaysOffsetDiscount(invoiceId); 
		
		SQL[
		    INSERT INTO OverdueNotice (customerid, invoiceid, amount, date) VALUES ($customerId$, $invoiceId$, $netAmount * daysOffsetDiscount$, CURRENT_DATE())];
		
		return ok(Json.toJson("Success"));
	}
}
