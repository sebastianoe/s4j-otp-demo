package controllers;

import helpers.SqlConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import models.Customer;
import models.SalesOrder;

import com.google.common.collect.Lists;

import play.data.Form;
import play.libs.Json;

import play.mvc.Controller;
import play.mvc.Result;

import views.html.customers.add;
import views.html.customers.details;
import views.html.customers.edit;
import views.html.customers.index;

public class CustomerController extends Controller {
	
	public static Result index() {
		List<Customer> customers = SQL[SELECT * FROM Customer];
		
		return ok(index.render(customers));
	}
	
	public static Result details(long id) {
		Customer c = getCustomer(id);

		c.setSalesOrders(
				SQL{List<SalesOrder>}[
                        SELECT id, status FROM SalesOrder WHERE customerid = $id$]);
		
		double lastMonthDso = getDso(id, 30);
		double lastYearDso = getDso(id, 365);
		
		return ok(details.render(c, lastMonthDso, lastYearDso));
	}
	
	public static Customer getCustomer(long customerId) {
		return SQL[SELECT * FROM Customer WHERE id = $customerId$];
	}
	
	public static Result add() {
		Form<Customer> customerForm = Form.form(Customer.class);
		return ok(add.render(customerForm));
	}
	
	public static Result saveAdd() {
		Form<Customer> form = Form.form(Customer.class);
		Customer c = form.bindFromRequest().get();
		
		SQL[INSERT INTO Customer (name, street, streetnumber, postcode, city)
			VALUES $c$];
		
		flash("success", "The customer has been added.");
		return index();
	}
	
	public static Result edit(long id) {
		Customer customer = SQL[SELECT * FROM Customer WHERE id = $id$];
		
		Form<Customer> customerForm = Form.form(Customer.class).fill(customer);
		return ok(edit.render(customerForm));
	}
	
	public static Result saveEdit(long id) {
		Form<Customer> form = Form.form(Customer.class);
		Customer c = form.bindFromRequest().get();
		
		SQL[UPDATE Customer SET $c$];
		
		flash("success", "The customer has been edited.");
		return index();
	}
	
	public static Result delete(long id) {
		SQL[DELETE FROM Customer WHERE id = $id$];
		
		flash("success", "The customer has been deleted");
		return index();
	}
	
	public static Result namesJson(String query) {
		List<Customer> customers = SQL[
                SELECT id, name FROM Customer WHERE name LIKE $"%" + query + "%"$];
		
		return ok(Json.toJson(customers));
	}

	private static double getDso(long customerId, int daysAgo) {
//		String dsoIntervalStart = new LocalDate().minusDays(daysAgo).toString("yyyy-MM-dd"); 
//		String dsoIntervalEnd = new LocalDate().toString("yyyy-MM-dd");
		
		/* 
		 * note: we simulate the dso with fixed dates. With up-to-date data,
		 * the dso could also work with the real values (commented out).
		 */
		String dsoIntervalStart = "2012-04-05";
		String dsoIntervalEnd = "2012-05-05";
		
		return SQL[
			SELECT (accountsreceivable.amount / totalcreditsales.amount) * $daysAgo$ AS dso FROM (SELECT SUM((soli.quantity * soli.price) * so.discountfactor) AS amount FROM Invoice i JOIN OutboundDelivery od ON od.id = i.outboundid JOIN SalesOrderLineItem soli ON od.id = soli.outboundid JOIN SalesOrder so ON so.id = soli.salesorderid WHERE so.customerid = $customerId$ AND (paid = NULL OR paid < $dsoIntervalEnd$)) AS accountsreceivable, (SELECT SUM((soli.quantity * soli.price) * so.discountfactor) AS amount FROM Invoice i JOIN OutboundDelivery od ON od.id = i.outboundid JOIN SalesOrderLineItem soli ON od.id = soli.outboundid JOIN SalesOrder so ON so.id = soli.salesorderid WHERE so.customerid = $customerId$ AND (i.billingdate BETWEEN $dsoIntervalStart$ AND $dsoIntervalEnd$)) AS totalcreditsales];
	}
}
