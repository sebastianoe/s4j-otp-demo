package controllers;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import models.Customer;
import models.OutboundDelivery;
import models.PaymentTerm;
import models.Product;
import models.SalesOrder;
import models.SalesOrderLineItem;

import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;

import play.libs.Json;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import views.html.salesorders.createMaster;
import views.html.salesorders.editProducts;
import views.html.salesorders.index;
import views.html.salesorders.release;
import views.html.salesorders.postGoodsIssue;
import views.html.salesorders.postCustomerInvoice;

public class SalesOrderController extends Controller {
	public static Result index() {
		List<SalesOrder> salesOrders = SQL[
            SELECT so.id, so.priority, so.discountfactor, so.status, c.id AS customerid, c.name, pt.id AS paymenttermid, pt.description, MIN(soli.promisedate) AS minpromisedate, MAX(soli.promisedate) AS maxpromisedate FROM SalesOrder so JOIN Customer c ON so.customerid = c.id JOIN PaymentTerm pt ON so.paymenttermid = pt.id JOIN SalesOrderLineItem soli ON so.id = soli.salesorderid GROUP BY so.id ORDER BY so.id];
		
		return ok(index.render(salesOrders));
	}
	
	public static Result reject(long id) {
		SQL[UPDATE SalesOrder SET status = 'rejected' WHERE id = $id$];
		
		return ok(Json.newObject()
				.put("action", "reject")
				.put("id", id));
	}
	
	public static Result createMasterData() {
		List<PaymentTerm> paymentTerms = SQL[
                SELECT id, description FROM PaymentTerm ORDER BY id];
		
		return ok(createMaster.render(paymentTerms));
	}
	
	public static Result saveMasterData() {
		Map<String, String[]> params = request().body().asFormUrlEncoded();
		int priority = Integer.valueOf(params.get("priority")[0]);
		long customerId = Long.valueOf(params.get("customerId")[0]);
		long paymentTermId = Long.valueOf(params.get("paymentTermsId")[0]);
		
		String initialStatus = "empty";
		double discountFactor = 1;
		
		boolean continueWithProduct = params.containsKey(
				"saveAndEnterProductButton");
		SQL[
		    INSERT INTO SalesOrder (customerid, paymenttermid, priority, discountfactor, status) VALUES ($customerId$, $paymentTermId$, $priority$, $discountFactor$, $initialStatus$)];
		
		if (continueWithProduct) {
			long soInsertId = SQL[SELECT LAST_INSERT_ID()];
			return redirect(controllers.routes.SalesOrderController
					                          .editProductData(soInsertId));
		} else {
			return redirect(controllers.routes.SalesOrderController.index());
		}
	}
	
	public static Result editProductData(long salesOrderId) {
		SalesOrder salesOrder = SQL[
                SELECT so.priority, c.name, pt.description, so.discountfactor, so.status FROM SalesOrder so JOIN Customer c ON so.customerid = c.id JOIN PaymentTerm pt ON so.paymenttermid = pt.id WHERE so.id = $salesOrderId$];

		List<SalesOrderLineItem> lineItems = SQL[
                SELECT soli.id, soli.quantity, soli.price, soli.promisedate, p.id AS productid, p.name, p.shippingcost, p.unit, p.pprice, p.defaultmargin FROM SalesOrderLineItem soli JOIN Product p ON soli.productid = p.id WHERE salesorderid = $salesOrderId$ ORDER BY soli.id];
		
		salesOrder.setId(salesOrderId);
		
		return ok(editProducts.render(salesOrder, lineItems));
	}

	public static Result saveProductData(long salesOrderId) {
		JsonNode json = request().body().asJson();
		JsonNode soliData = json.get("soliData");
		Double discountFactor = json.get("discountFactor").asDouble();
		
		List<Long> soliIds = getStoredSoliIds(salesOrderId);
		storeDiscountFactor(salesOrderId, discountFactor);
		return storeSoliValues(salesOrderId, soliData, soliIds);
	}
	
	public static Result performAtpCheck() {
		JsonNode json = request().body().asJson();
		
		Long requestedProductId = json.get("requestedProductId").asLong();
		LocalDate requestedDate = LocalDate.parse(json.get("requestedDate")
													  .textValue());
		int availableAmount = SQL[
			    SELECT (COALESCE(p.stocklevel, 0) - COALESCE(outgoingquantity.quantity, 0) + COALESCE(incomingquantity.quantity, 0)) AS availableamount FROM Product AS p LEFT OUTER JOIN (SELECT soli.productid, SUM(soli.quantity) AS quantity FROM SalesOrderLineItem AS soli, SalesOrder AS so WHERE soli.salesorderid = so.id AND ((so.status='released') OR (so.priority >= 1)) GROUP BY soli.productid) AS outgoingquantity ON p.id = outgoingquantity.productid LEFT OUTER JOIN (SELECT ig.productid AS productid, SUM(ig.amount) AS quantity FROM IncomingGoods AS ig WHERE ig.incomedate > 1 AND ig.incomedate < 1 GROUP BY ig.productid) AS incomingquantity ON p.id = incomingquantity.productid WHERE p.id = 1];
		
		return ok(Json.newObject().put("availableAmount", availableAmount));
	}

	private static void storeDiscountFactor(long salesOrderId,
			Double discountFactor) {
		SQL[
		    UPDATE SalesOrder 
		    SET discountfactor = $discountFactor$ WHERE id = $salesOrderId$];
	}

	private static List<Long> getStoredSoliIds(long salesOrderId) {
		List<Long> soliIds = SQL[
            SELECT id FROM SalesOrderLineItem WHERE salesorderid = $salesOrderId$];
		
		return soliIds;
	}

	private static Result storeSoliValues(long salesOrderId, JsonNode soliData,
			List<Long> soliIds) {
		List<Long> jsonIds = Lists.newArrayList();
		Iterator<JsonNode> iterator = soliData.iterator();
		while (iterator.hasNext()) {
			JsonNode node = iterator.next();
			long id = node.get("id").asLong();
			jsonIds.add(id);
			
			long productId = node.get("productId").asLong();
			int quantity = node.get("quantity").asInt();
			LocalDate promiseDate = LocalDate.parse(node.get("promiseDate")
					.textValue());
			double price = node.get("price").asDouble();
			
			if (soliIds.contains(id)) {
				// update
				SQL[UPDATE SalesOrderLineItem SET productId = $productId$, quantity = $quantity$, price = $price$, promisedate = $new java.sql.Date(promiseDate.toDate().getTime())$ WHERE id = $id$];
			} else {
				// insert
				SQL[INSERT INTO SalesOrderLineItem (salesorderid, productid, quantity, price, promisedate) VALUES ($salesOrderId$, $productId$, $quantity$, $price$, $new java.sql.Date(promiseDate.toDate().getTime())$)];
			}
		}
		
		// delete missing soli entries
		for (Long soliId : soliIds) {
			if (!jsonIds.contains(soliId)) {
				SQL[DELETE FROM SalesOrderLineItem WHERE id = $soliId$];
			}
		}
		
		// update SalesOrder status based on the availability of line items
		SQL[UPDATE SalesOrder
		    SET status = $jsonIds.isEmpty() ? "empty" : "submitted"$ WHERE id = $salesOrderId$];
	
		return ok("Saved products");
	}
	
	public static Result showRelease() {
		List<SalesOrder> salesOrders = SQL[
            SELECT so.id, c.id AS customerid, c.name, MIN(soli.promisedate) AS minpromisedate, MAX(soli.promisedate) AS maxpromisedate FROM SalesOrder so JOIN Customer c ON so.customerid = c.id JOIN SalesOrderLineItem soli ON so.id = soli.salesorderid WHERE so.status = 'submitted'  GROUP BY so.id ORDER BY so.id];
		
		return ok(release.render(salesOrders));
	}

	public static Result release(long id) {
		SQL[UPDATE SalesOrder SET status = 'released'  WHERE id = $id$];
		
		return ok(Json.newObject().put("action", "release")
								  .put("id", id));
	}
	
	public static Result showPostGoodsIssue() {
		List<SalesOrder> salesOrders = SQL[SELECT so.id, c.id AS customerid, c.name, MAX(soli.promisedate) AS maxpromisedate FROM SalesOrder so JOIN Customer c ON so.customerid = c.id JOIN SalesOrderLineItem soli ON so.id = soli.salesorderid WHERE so.status = 'released'  GROUP BY so.id ORDER BY so.id];
		
		return ok(postGoodsIssue.render(salesOrders));
	}
	
	public static Result postGoodsIssue(long id) {
		SQL[UPDATE SalesOrder SET status = 'sent' WHERE id = $id$];
		
		SQL[INSERT INTO OutboundDelivery (sentdate) VALUES ($new Date((new java.util.Date()).getTime())$)];
		
		long outboundDeliveryId = SQL[SELECT LAST_INSERT_ID()];
		
		SQL[UPDATE SalesOrderLineItem SET outboundid = $outboundDeliveryId$ WHERE salesorderid = $id$];

		flash("success", "The order was sent.");
		return redirect(controllers.routes.SalesOrderController
										  .showPostGoodsIssue());
	}
	
	public static Result showPostCustomerInvoice() {
		List<SalesOrder> salesOrders = SQL[
		    SELECT so.id, c.id AS customerid, c.name, MAX(soli.promisedate) AS maxpromisedate, od.id, od.sentdate, SUM(soli.price) * so.discountfactor * 1.19 + MAX(p.shippingcost) AS amount FROM SalesOrder so JOIN Customer c ON so.customerid = c.id JOIN SalesOrderLineItem soli ON so.id = soli.salesorderid JOIN OutboundDelivery od ON od.id = soli.outboundid JOIN Product p ON soli.productid = p.id WHERE so.status = 'sent' GROUP BY so.id ORDER BY so.id];
		
		return ok(postCustomerInvoice.render(salesOrders));
	}
	
	public static Result postCustomerInvoice(long orderId, long outboundId) {
		SQL[UPDATE SalesOrder SET status = 'invoiced' WHERE id = $orderId$];
		
		SQL[
		    INSERT INTO Invoice (outboundid, billingdate) VALUES ($outboundId$, $new Date((new java.util.Date()).getTime())$)];
		
		return redirect(controllers.routes.SalesOrderController.showPostCustomerInvoice());
	}
}
