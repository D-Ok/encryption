package messaging;

import messaging.exceptions.ArgumentException;
import messaging.exceptions.NoMessageException;
import messaging.network.StoreServerTCP;

import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Encriptor {
	
	private final Sender sender;
	private final byte bSrc = 1;
	private ExecutorService ex;
	
	public Encriptor() {
		sender = new Sender();
		ex = Executors.newFixedThreadPool(3);
	}
	
	public void encryption(Message message, int num) {
		ex.execute(new EncryptMessage(message, num));
	}
	
	private byte[] encrypt(Message message) throws NoMessageException, ArgumentException, Exception {
		Package pk = new Package(bSrc, message.getcType(), message.getUserId(), message.getJsonMessage());
		return pk.getWholePackage();
	}
	
	private class EncryptMessage implements  Runnable{

		private final Message message;
		private final int num;
		public EncryptMessage(Message message, int num) {
			this.message = message;
			this.num = num;
		}
		@Override
		public void run() {
			try {
				//sender.sendMessage(encrypt(message), InetAddress.getLocalHost());
				StoreServerTCP.setAnswer(num, encrypt(message));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}		
	
}
