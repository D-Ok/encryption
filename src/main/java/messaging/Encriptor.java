package messaging;

import messaging.exceptions.ArgumentException;
import messaging.exceptions.NoMessageException;

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
	
	public void encryption(Message message) {
		ex.execute(new EncryptMessage(message));
	}
	
	private byte[] encrypt(Message message) throws NoMessageException, ArgumentException, Exception {
		Package pk = new Package(bSrc, message.getcType(), message.getUserId(), message.getJsonMessage());
		return pk.getWholePackage();
	}
	
	private class EncryptMessage implements  Runnable{

		private final Message message;
		public EncryptMessage(Message message) {
			this.message = message;
		}
		@Override
		public void run() {
			try {
				sender.sendMessage(encrypt(message), InetAddress.getLocalHost());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}		
	
}
