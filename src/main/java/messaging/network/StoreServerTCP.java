package messaging.network;

import java.io.IOException; 
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import messaging.Decriptor;

public class StoreServerTCP implements Runnable, Server {
	// The port we will listen on
	private int port;
	private Decriptor decriptor;
	

	// A pre-allocated buffer for encrypting data
	private static ByteBuffer buffer = ByteBuffer.allocate(1024);

	public StoreServerTCP(int port) {
		this.port = port;
		decriptor = new Decriptor();
		new Thread(this).start();

	}

	Selector selector;

	public void run() {
		try {
			ServerSocketChannel serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);

			ServerSocket serverSocket = serverChannel.socket();
			InetSocketAddress isa = new InetSocketAddress(port);
			serverSocket.bind(isa);

			selector = Selector.open();

			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("Listening on port " + port);

			while (true) {
				int num = selector.select();

				if (num == 0)
					continue;

				Set keys = selector.selectedKeys();
				Iterator it = keys.iterator();

				while (it.hasNext()) {
					SelectionKey key = (SelectionKey) it.next();

					if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {

						System.out.println("acc");
						Socket s = serverSocket.accept();
						System.out.println("Got connection from " + s);

						SocketChannel sc = s.getChannel();
						sc.configureBlocking(false);
						sc.register(selector, SelectionKey.OP_READ);

					} else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
						SocketChannel sc = null;

						try {
							sc = (SocketChannel) key.channel();
							boolean ok = processInput(sc);
							if (!ok) closeSocket(sc, key);

						} catch (IOException ie) {
							closeSocket(sc, key);
						}
					}
				}
				keys.clear();
			}
		} catch (IOException ie) {
			System.err.println(ie);
		}
	}
	
	private void closeSocket(SocketChannel sc, SelectionKey key) {
		key.cancel();

		Socket s = null;
		try {
			s = sc.socket();
			s.close();
			sc.close();
		} catch (IOException ie) {
			System.err.println("Error closing socket " + s + ": " + ie);
		}
		
		System.out.println("Closed " + sc);
	}

	
	private boolean processInput(SocketChannel sc) throws IOException {
		buffer.clear();
		sc.read(buffer);
		buffer.flip();

		if (buffer.limit() == 0) {
			return false;
		}

		byte[] message = new byte[buffer.limit()];
		for (int i = 0; i < buffer.limit(); ++i) {
			message[i] = buffer.get(i);
		}

		Random r = new Random();
		int unicNumb = r.nextInt();

		while (answers.containsKey(unicNumb)|| unicNumb==-1)
			unicNumb = r.nextInt();
		decriptor.decript(message, unicNumb);

		buffer.clear();
		while (true)
			if (answers.containsKey(unicNumb)) {

				byte[] answer = answers.get(unicNumb);
				answers.remove(unicNumb);

				if (answer[0] != 13)
					return false;

				for (int i = 0; i < 200; i++) {
					if (i < answer.length)
						buffer.put(answer[i]);
					else
						buffer.put((byte) 0);
				}
				break;
			}

		buffer.flip();
		sc.write(buffer);

		System.out.println("Processed " + buffer.limit() + " from " + sc);
		return true;
	}

	static public void main(String args[]) throws Exception {
		int port = 1050;
		new StoreServerTCP(port);
	}

}
