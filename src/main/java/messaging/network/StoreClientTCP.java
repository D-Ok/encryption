package messaging.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import messaging.PackageGetter;
import messaging.exceptions.InjuredPackageException;

public class StoreClientTCP implements Runnable {
	private String host;
	private int port;

	// Bounds on how long we wait between cycles
	private static final int minPause = (int) (0.05 * 1000);
	private static final int maxPause = (int) (0.5 * 1000);

	Random rand = new Random();

	public StoreClientTCP(String host, int port, int numThreads) {
		this.host = host;
		this.port = port;

		for (int i = 0; i < numThreads; ++i) {
			new Thread(this).start();
		}
	}

	public void run() {

		byte buffer[];

		try {
			Socket s = null;
			InputStream in = null;
			OutputStream out = null;
			
			s = tryToGetConnection(s);
			in = s.getInputStream();
			out = s.getOutputStream();
			buffer = Client.generatePackage().getWholePackage();
			
			while (true) {
			//for(int i=0; i<10; i++) {
				
				PackageGetter pg;
				boolean socketInterrapted = false;
				 buffer = Client.generatePackage().getWholePackage();
				 
				try {
					out.write(buffer, 0, buffer.length);
				} catch (IOException e) {
					System.out.println("Error while wrining message");
					buffer = Client.generatePackage().getWholePackage();
					socketInterrapted = true;
				} 
				
				int numToWrite = 200;
				byte[] answer = new byte[numToWrite];
				int sofar = 0;
				int preSofar = 0;

				while (sofar < numToWrite && !socketInterrapted) {
					preSofar = sofar;
					try {
						sofar += in.read(answer, sofar, numToWrite - sofar);
					} catch (IOException e) {
						socketInterrapted = true;
					}

					if (sofar < preSofar) {
						System.out.println("Error while processing message");
						socketInterrapted = true;
					}
				}

				if (!socketInterrapted) {
					try {
						pg = new PackageGetter(answer);
						System.out.println(Thread.currentThread() + " wrote "+ pg.getMessageString());
					} catch (InjuredPackageException e) {
						System.out.println("injured answer");
					}

					buffer = Client.generatePackage().getWholePackage();
					int pause = minPause + (int) (rand.nextDouble() * (maxPause - minPause));
					try {
						Thread.sleep(pause);
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					}

				} else {
					s.close();
					in.close();
					out.close();
					
					s = tryToGetConnection(s);
					in = s.getInputStream();
					out = s.getOutputStream();
				}

			}
			
		} catch (IOException ie) {
			ie.printStackTrace();
		} 
	}
	
	private Socket tryToGetConnection(Socket s) {
		try {
			s = new Socket(host, port);
		} catch (ConnectException e){
			System.out.println(Thread.currentThread() + " No connection");
			s= tryToGetConnection(s);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (s==null) tryToGetConnection(s);
		
		return s;
	}

	static public void main(String args[]) throws Exception {
		InetAddress addr = InetAddress.getByName(null);
		String host = addr.getHostAddress();
		int port = 1050;
		int numThreads = 1000;

		new StoreClientTCP(host, port, numThreads);
	}
}
