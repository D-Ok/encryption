package messaging;

import java.util.concurrent.ExecutorService; 
import java.util.concurrent.Executors;

import org.apache.commons.codec.net.QuotedPrintableCodec;

import messaging.exceptions.InvalidCharacteristicOfGoodsException;
import messaging.warehouse.Database;

public class Processor {

	//public static final Warehouse warehouse = new Warehouse();
	final Encriptor encriptor;
	private ExecutorService ex;
	private final Database db;

	public Processor() {
		db = new Database();
		encriptor = new Encriptor();
		ex = Executors.newFixedThreadPool(3);
	}

	public void process(Message message, int num) {
		ex.execute(new ProcessMessage(message, num));
	}

	private class ProcessMessage implements Runnable {

		Message message;
		private int num;

		ProcessMessage(Message message, int num) {
			this.message = message;
			this.num = num;
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
			try {
				switch (message.getcType()) {
				case 0:
					info = "Quantity of " + goods + " in the group " + group + ": "
							+ db.getQuontityOfGoods(goods);
					break;
				case 1:
					try {
						db.removeGoods(goods, quantity);
						info = "Was removed " + goods + " in the group " + group + ": " + quantity;
					} catch (InvalidCharacteristicOfGoodsException e1) {
						info = "Error: " + e1.toString();
					}
					
					break;
				case 2:
					db.addGoods(goods, quantity);
					info = "Was added " + goods + " in the group " + group + " quantity: " + quantity;
					break;
				case 3:
					db.createGroup(group);
					info = "Was added group " + group;
					break;
				case 4:
					try {
						db.createGoods(goods, group, "none", quantity, price);
						info = "Was added goods with name " + goods.toString() + " to the group " + group;
					} catch (InvalidCharacteristicOfGoodsException e1) {
						info = "Error: " + e1.toString();
					}
					break;
				case 5:
					try {
						db.setPrice(goods, price);
						info = "To the " + goods + " in the group " + group + " was set price: " + price;
					} catch (InvalidCharacteristicOfGoodsException e) {
						info = "Error: " + e.getMessage();
					}
					break;
				default:
					break;
				}
			} catch (NullPointerException e) {
				info="Your message was injured or uncorrect";
			}
			encriptor.encryption(new Message(cType, userId, info, message.getUnicNumberOfMessage()), num);
		}
	}
}
