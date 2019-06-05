package messaging.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

import javax.crypto.BadPaddingException;

import messaging.PackageGetter;
import messaging.exceptions.InjuredPackageException;

public class StoreClientUDP implements Runnable {
	private InetAddress addr;
	private int port;

	// Bounds on how long we wait between cycles
	private static final int minPause = (int) (0.05 * 1000);
	private static final int maxPause = (int) (0.5 * 1000);
	Random rand = new Random();

	public StoreClientUDP(InetAddress addr, int port, int numThreads) {
		this.addr = addr;
		this.port = port;

		for (int i = 0; i < numThreads; ++i) {
			new Thread(this).start();
		}
	}

	private int numToWrite = 200;
	private byte[] answer;
	private DatagramPacket packet;
	private DatagramSocket s;

	public void run() {

		byte buffer[];
		int numberOfTry = 0;
		try {
			s = new DatagramSocket();
			s.connect(addr, port);

			buffer = Client.generatePackage().getWholePackage();

			//while (true) {
			for(int i=0; i<10;) {

				packet = new DatagramPacket(buffer, buffer.length);
				s.send(packet);
				answer = new byte[numToWrite];
				packet = new DatagramPacket(answer, numToWrite);

				Thread th = new Thread(new WaitForAnswer());
				try {
					th.start();
					Thread.sleep(timeToWait);
					th.stop();
				
				if (answer[0] == 13) {
					PackageGetter pg;
					try {
						pg = new PackageGetter(answer);
						System.out.println(Thread.currentThread().getName() + " wrote " + pg.getMessageString());
						
					} catch (InjuredPackageException | NegativeArraySizeException | ArrayIndexOutOfBoundsException | BadPaddingException e) {
						System.out.println("injured answer");
					}
					i++;
					buffer = Client.generatePackage().getWholePackage();
					//numberOfTry=0;
					int pause = minPause + (int) (rand.nextDouble() * (maxPause - minPause));
					try {
						Thread.sleep(pause);
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					}
				} else {
//					numberOfTry++;
//					if(numberOfTry<10)
					System.out.println(Thread.currentThread().getName() +"No answer. Try to send packege again ");
//					else {
//						numberOfTry=0;
//						buffer = Client.generatePackage().getWholePackage();
//						System.out.println(Thread.currentThread().getName() +"New message");
//					}
				}
				
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
			}

		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	class WaitForAnswer implements Runnable {

		@Override
		public void run() {
			try {
				s.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private static int timeToWait=300;

	static public void main(String args[]) throws Exception {
		InetAddress addr = InetAddress.getByName(null);
		int port = 1050;
		int numThreads = 100;
		if(numThreads>=300) timeToWait=(numThreads/200)*numThreads;

		new StoreClientUDP(addr, port, numThreads);
	}
}
