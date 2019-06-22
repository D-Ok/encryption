package messaging.warehouse;

public class Good {

	private int id;
	private String name;
	private String description;
	private String producer;
	private String groupName;
	private double price;
	private int quontity;
	
	public Good() {
		
	}
	
	public Good(String name, String description, String producer, String groupName, double price, int quontity) {
		this.name = name;
		this.description = description;
		this.producer = producer;
		this.groupName = groupName;
		this.price = price;
		this.quontity = quontity;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProducer() {
		return producer;
	}

	public void setProducer(String producer) {
		this.producer = producer;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getQuontity() {
		return quontity;
	}

	public void setQuontity(int quontity) {
		this.quontity = quontity;
	}

	@Override
	public String toString() {
		return "Good [name=" + name + ", description=" + description + ", producer=" + producer + ", groupName="
				+ groupName + ", price=" + price + ", quontity=" + quontity + "]";
	}
	
	
}
