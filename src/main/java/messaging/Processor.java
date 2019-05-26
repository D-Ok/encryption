package messaging;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import messaging.exceptions.InvalidCharacteristicOfGoodsException;
import messaging.warehouse.Warehouse;

public class Processor {
	
	public static final Warehouse warehouse = new Warehouse();
	private final Encriptor encriptor;
	private ExecutorService ex;
	
	public Processor() {
		encriptor = new Encriptor();
		ex = Executors.newFixedThreadPool(3);
	}
	
	public void process(Message message) {
		ex.execute(new ProcessMessage(message));
	}
	
	private class ProcessMessage implements Runnable{
		
		Message message;
		ProcessMessage(Message message){
			this.message = message;
		}
		@Override
		public void run() {
			
			int cType = message.getcType();
			int userId = message.getUserId();
			String group = message.getNameOfGroup();
			String goods = message.getNameOfGoods();
			int quantity = message.getQuantity();
			double price = message.getPrice();
			String info = "";
			
			switch (message.getcType()) {
			case 0:
				info = "Quantity of "+goods+" in the group "+group+": "+warehouse.getQuantityOfGoods(group, goods);
				break;
			case 1 :
				try {
					warehouse.removeGoods(group, goods, quantity);
					info = "Was removed "+goods+" in the group "+group+": "+quantity;
				} catch (InvalidCharacteristicOfGoodsException e) {
					info = "Error: "+e.getMessage();
				}
				break;
			case 2:
				try {
					warehouse.addGoods(group, goods, quantity);
					info = "Was added "+goods+" in the group "+group+" quantity: "+quantity;
				} catch (InvalidCharacteristicOfGoodsException e) {
					info = "Error: "+e.getMessage();
				}
				
				break;
			case 3:
				warehouse.addGroup(group);
				info = "Was added group "+group;
				break;
			case 4:
				warehouse.addNameOfGoodtToGroup(group, goods);
				info = "Was added goods with name '"+goods+"' to the group "+group;
				break;
			case 5:
				try {
					warehouse.setPrice(group, goods, price);
					info = "To the "+goods+" in the group "+group+" was set price: "+price;
				} catch (InvalidCharacteristicOfGoodsException e) {
					info = "Error: "+e.getMessage();
				}
				break;
			default:
				break;
		}

			encriptor.encryption(new Message(cType, userId, info));
	}
	}
}
