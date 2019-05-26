package messaging.warehouse;

import java.util.Iterator;

import messaging.exceptions.InvalidCharacteristicOfGoodsException;

public class Goods {

	private double price;
	private int quantity;
	private String name;
	
	public double getPrice() {
		return price;
	}

	public void setPrice(double price) throws InvalidCharacteristicOfGoodsException {
		if(price<0) throw new InvalidCharacteristicOfGoodsException("Price of goods can`t be less than 0");
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) throws InvalidCharacteristicOfGoodsException {
		if(quantity<0) throw new InvalidCharacteristicOfGoodsException("Quantity of goods can`t be less than 0");
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
	
	Goods(String name, double price, int quantity) throws InvalidCharacteristicOfGoodsException{
		this.name = name;
		if(quantity<0 || price<0) throw new InvalidCharacteristicOfGoodsException();
		this.quantity = quantity;
		this.price = price;
	}
	
	public String toString() {
		return "Name: "+name+" Quantity: "+quantity+" Price: "+price;
	}
}
