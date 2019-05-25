package messaging;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import messaging.warehouse.Warehouse;

public class Processor {
	
	private final Warehouse warehouse;
	private final Encriptor encriptor;
	private ExecutorService ex;
	
	public Processor() {
		warehouse = new Warehouse();
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
				info = "Quantity of "+goods+" in group "+group+": "+warehouse.getQuantityOfGoods(group, goods);
				break;
			case 1 :
				warehouse.removeGoods(group, goods, quantity);
				info = "Was removed "+goods+" in group "+group+": "+quantity;
				break;
			case 2:
				warehouse.addGoods(group, goods, quantity);
				info = "Was added "+goods+" in group "+group+" quantity: "+quantity;
				break;
			case 3:
				warehouse.addGroup(group);
				info = "Was added group "+group;
				break;
			case 4:
				warehouse.addNameOfGoodtToGroup(group, goods);
				info = "Was added goods with name '"+goods+"' to group "+group;
				break;
			case 5:
				warehouse.setPrice(group, goods, price);
				info = "To the "+goods+" in group "+group+" was set price: "+price;
				break;
			default:
				break;
		}

			encriptor.encryption(new Message(cType, userId, info));
	}
	}
}
