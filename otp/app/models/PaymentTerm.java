package models;

public class PaymentTerm {
	private Long id;
	private String description;

	public PaymentTerm() {}
	
	public PaymentTerm(Long id, String description) {
		this.id = id;
		this.description = description;
	}
	
	public PaymentTerm(String description) {
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
