package messaging;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.BadPaddingException;

import messaging.exceptions.InjuredPackageException;
import messaging.network.Server;
import messaging.network.StoreServerTCP;

public class Decriptor {
	
	private final Processor processor;
	private ExecutorService ex;
	
	public Decriptor() {
		processor = new Processor();
		ex = Executors.newFixedThreadPool(3);
	}
	
	public void decript(byte[] message, int number) {
		ex.execute(new DecriptMessage(message, number));
	}
	
	private class DecriptMessage implements  Runnable{

		private final byte[] message;
		private final int num;
		public DecriptMessage(byte[] message, int num) {
			this.message = message;
			this.num = num;
		}
		@Override
		public void run() {
			try {
				PackageGetter pg= new PackageGetter(message);
				processor.process(new Message(pg.getcType(), pg.getbUserId(), pg.getMessageJson()), num);
			} catch (InjuredPackageException | NegativeArraySizeException | ArrayIndexOutOfBoundsException | BadPaddingException e) {
				
				processor.encriptor.encryption(new Message(0, 0, "Your messaje was injured"), num);
			}
		}
		
	}
}
