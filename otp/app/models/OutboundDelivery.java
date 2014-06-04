package models;

import java.sql.Date;

public class OutboundDelivery {
	private long id;
	private Date sentDate;

	public OutboundDelivery() {}
	
	public OutboundDelivery(long id, Date sentDate) {
		this.id = id;
		this.sentDate = sentDate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

}
