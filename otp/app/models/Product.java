package models;

public class Product {
	private long id;
	private String name;
	private String unit;
	private Double pprice;
	private Double defaultMargin;
	private Long stockLevel;
	private Double shippingCost;

	// empty default constructor
	public Product() {}
	
	public Product(long id, String name, String unit, Double pprice,
			Double defaultMargin, Long stockLevel, Double shippingCost) {
		this.id = id;
		this.name = name;
		this.unit = unit;
		this.pprice = pprice;
		this.defaultMargin = defaultMargin;
		this.stockLevel = stockLevel;
		this.shippingCost = shippingCost;
	}
	
	public Product(long id, String name) {
		this(id, name, null, null, null, null, null);
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Double getPprice() {
		return pprice;
	}

	public void setPprice(Double pprice) {
		this.pprice = pprice;
	}

	public Double getDefaultMargin() {
		return defaultMargin;
	}

	public void setDefaultMargin(Double defaultMargin) {
		this.defaultMargin = defaultMargin;
	}

	public Long getStockLevel() {
		return stockLevel;
	}

	public void setStockLevel(Long stockLevel) {
		this.stockLevel = stockLevel;
	}

	public Double getShippingCost() {
		return shippingCost;
	}

	public void setShippingCost(Double shippingCost) {
		this.shippingCost = shippingCost;
	}
}
