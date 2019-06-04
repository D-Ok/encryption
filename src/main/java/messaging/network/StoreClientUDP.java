package messaging.network;

import java.io.IOException; 
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
//	private static ByteBuffer buff = ByteBuffer.allocate(1024);

	public void run() {

		byte buffer[];

		try {
			DatagramSocket s = new DatagramSocket();
	//		DatagramChannel ch = s.getChannel();
//			InputStream in = ch.getInputStream();
//			OutputStream out = s.getOutputStream();
			
			buffer = Client.generatePackage().getWholePackage();
			
			while (true) {
				
				boolean socketInterrapted = false;
				 buffer = Client.generatePackage().getWholePackage();
				 
				// send request
		        byte[] buf = new byte[256];
		        
		        DatagramPacket packet = new DatagramPacket(buf, buf.length, addr, port);
		        s.send(packet);

		        int numToWrite = 200;
				byte[] answer = new byte[numToWrite];
				int sofar = 0;
				int preSofar = 0;
		        // get response
		        packet = new DatagramPacket(answer, numToWrite);
		        s.receive(packet);

		        // display response
		       // String received = new String(packet.getData(), 0, packet.getLength());
		        PackageGetter pg ;
		        try {
					pg = new PackageGetter(answer);
					System.out.println(Thread.currentThread() + " wrote "+ pg.getMessageString());
				} catch (InjuredPackageException e) {
					System.out.println("injured answer");
				}
			//for(int i=0; i<10; i++) {
				

			}
			
		} catch (IOException ie) {
			ie.printStackTrace();
		} 
	}
	
//	private Socket tryToGetConnection(Socket s) {
//		try {
//			s = new Socket(host, port);
//		} catch (ConnectException e){
//			System.out.println(Thread.currentThread() + " No connection");
//			s= tryToGetConnection(s);
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		if (s==null) tryToGetConnection(s);
//		
//		return s;
//	}

	static public void main(String args[]) throws Exception {
		InetAddress addr = InetAddress.getByName(null);
		//String host = addr.getHostAddress();
		int port = 1050;
		int numThreads = 1;

		new StoreClientUDP(addr, port, numThreads);
	}
}
