package messaging.warehouse;


public class Goods {

	private double price;
	private int quantity;
	private String name;
	
	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	Goods(String name){
		this.name = name;
		price = 0.1;
		quantity = 0;
	}

	Goods(String name, double price){
		this.name = name;
		this.price = price;
		this.quantity = 0;
	}
	
	Goods(String name, double price, int quantity){
		this.name = name;
		this.price = price;
		this.quantity = quantity;
	}
}
