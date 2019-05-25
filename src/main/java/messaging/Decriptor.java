package messaging;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import messaging.exceptions.InjuredPackageException;

public class Decriptor {
	
	private final Processor processor;
	private ExecutorService ex;
	
	public Decriptor() {
		processor = new Processor();
		ex = Executors.newFixedThreadPool(3);
	}
	
	public void decript(byte[] message) {
		ex.execute(new DecriptMessage(message));
	}
	
	private class DecriptMessage implements  Runnable{

		private final byte[] message;
		public DecriptMessage(byte[] message) {
			this.message = message;
		}
		@Override
		public void run() {
			try {
				PackageGetter pg= new PackageGetter(message);
				processor.process(new Message(pg.getcType(), pg.getbUserId(), pg.getMessageJson()));
			} catch (InjuredPackageException e) {
				e.printStackTrace();
			}
		}
		
	}
}
