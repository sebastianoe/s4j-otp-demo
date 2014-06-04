package controllers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import models.Product;

import com.google.common.collect.Lists;

import play.data.Form;
import play.libs.Json;

import play.mvc.Controller;
import play.mvc.Result;

import views.html.products.add;
import views.html.products.edit;
import views.html.products.index;

public class ProductController extends Controller {
	
	public static Result index() {
		List<Product> products = SQL[SELECT * FROM Product];
		
		return ok(index.render(products));
	}
	
	public static Result add() {
		Form<Product> productForm = Form.form(Product.class);
		return ok(add.render(productForm));
	}
	
	public static Result saveAdd() {
		Form<Product> form = Form.form(Product.class);
		Product p = form.bindFromRequest().get();
		
		SQL[
		    INSERT INTO Product (name, unit, pprice, defaultmargin, stocklevel, shippingcost) VALUES $p$];
		
		flash("success", "The product has been added.");
		return index();
	}

	public static Result edit(long id) {
		Product product = SQL[SELECT * FROM Product WHERE id = $id$];
		
		Form<Product> productForm = Form.form(Product.class).fill(product);
		
		return ok(edit.render(productForm));
	}
	
	public static Result saveEdit(long id) {
		Form<Product> form = Form.form(Product.class);
		Product p = form.bindFromRequest().get();
		
		SQL[UPDATE Product SET $p$];
		
		flash("success", "The product has been edited.");
		return index();
	}
	
	public static Result delete(long id) {
		SQL[DELETE FROM Product WHERE id = $id$];
		
		flash("success", "The product has been deleted");
		return index();
	}
	
	public static Result autocompleteJson(String query) {
		List<Product> products = SQL[
	            SELECT id, name, unit, pprice, defaultmargin, shippingcost FROM Product WHERE name LIKE $"%" + query + "%"$];
		
		return ok(Json.toJson(products));
	}
}
