package messaging.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.BadPaddingException;

import org.xml.sax.HandlerBase;

import com.google.gson.JsonObject;

import messaging.PackageGetter;
import messaging.exceptions.InjuredPackageException;

public class StoreClientUDP implements Runnable {
	private InetAddress addr;
	private int port;

	// Bounds on how long we wait between cycles
	private static final int minPause = (int) (0.05 * 1000);
	private static final int maxPause = (int) (0.5 * 1000);

	private static ConcurrentHashMap<Long, byte[]> messages = new ConcurrentHashMap<Long, byte[]>();
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
	private static int n = 0;

	public void run() {

		byte buffer[];
		try {
			s = new DatagramSocket(n);
			n++;
			s.connect(addr, port);

			buffer = Client.generatePackage().getWholePackage();

			// while (true) {
			for (int i = 0; i < 10;) {
				
				packet = new DatagramPacket(buffer, buffer.length);
				s.send(packet);
				try {
					PackageGetter p = new PackageGetter(buffer);
					messages.put(p.getbPktId(), buffer);
					System.out.println(" send " + p.getbPktId());
				} catch (InjuredPackageException | NegativeArraySizeException | ArrayIndexOutOfBoundsException
						| BadPaddingException  e2) {
					//e2.printStackTrace();
				} 
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
							JsonObject jo = (JsonObject) pg.getMessageJson();
							long num = jo.get("unicNumber").getAsLong();
							if (messages.containsKey(num)) {
								System.out.println( " wrote " + num);
								messages.remove(num);
								i++;
								buffer = Client.generatePackage().getWholePackage();
							} else {
								System.out.println("No answer. Try to send packege again "+num);
								}
						} catch (InjuredPackageException | NegativeArraySizeException | ArrayIndexOutOfBoundsException
								| BadPaddingException e) {
							System.out.println("injured answer");
						}
						
						int pause = minPause + (int) (rand.nextDouble() * (maxPause - minPause));
						try {
							Thread.sleep(pause);
						} catch (InterruptedException ie) {
							ie.printStackTrace();
						}
					} else {
						System.out.println(Thread.currentThread().getName() + "No answer. Try to send packege again ");
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

	private static int timeToWait = 10;

	static public void main(String args[]) throws Exception {
		InetAddress addr = InetAddress.getByName(null);
		int port = 1050;
		int numThreads = 10;
//		if (numThreads >= 300)
//			timeToWait = (numThreads / 200) * numThreads;

		new StoreClientUDP(addr, port, numThreads);
	}
}
