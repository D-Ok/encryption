package messaging.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import messaging.PackageGetter;
import messaging.exceptions.InjuredPackageException;

public class StoreClientTCP implements Runnable{
	private String host;
    private int port;

    // Bounds on how long we wait between cycles
    private static final int minPause = (int) (0.05 * 1000);
    private static final int maxPause = (int) (0.5 * 1000);

    // Random number generator
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
            Socket s = new Socket(host, port);

            InputStream in = s.getInputStream();
            OutputStream out = s.getOutputStream();

            while (true) {
            	if(s.isConnected()) {
            	buffer = Client.generatePackage().getWholePackage();
            	PackageGetter pg;
                out.write(buffer, 0, buffer.length);
                
                try {
                	int numToWrite = 200;
                	 byte[] answer = new byte[numToWrite];
                	 int sofar = 0;
                     while (sofar < numToWrite) {
                         sofar += in.read(answer, sofar, numToWrite - sofar);
                     }
					pg = new PackageGetter(answer);
					System.out.println(Thread.currentThread() + " wrote " + pg);
				} catch (InjuredPackageException e) {
					e.printStackTrace();
				}catch(IOException ie) {
					ie.printStackTrace();
                }

                int pause = minPause +
                        (int) (rand.nextDouble() * (maxPause - minPause));
                try {
                    Thread.sleep(pause);
                } catch (InterruptedException ie) {
                }
                
            } else {
            	s = new Socket(host, port);
            }
            }
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    static public void main(String args[]) throws Exception {
    	InetAddress addr = InetAddress.getByName(null);
        String host = addr.getHostAddress();
        int port = 1050;
        int numThreads = 1;

        new StoreClientTCP(host, port, numThreads);
    }
}
