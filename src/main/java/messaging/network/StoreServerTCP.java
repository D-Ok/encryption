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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import messaging.Decriptor;

public class StoreServerTCP  extends Server implements Runnable{
  // The port we will listen on
  private int port;
  private static int freeUnicNumber = 1;
  private Set<Integer> processingMessages;
  private Decriptor decriptor;
  private static ConcurrentHashMap<Integer, byte[]> answers;
  
  public static void setAnswer(int unicNumber, byte[] answer) {
	  answers.put(unicNumber, answer);
  }

  // A pre-allocated buffer for encrypting data
  private static final ByteBuffer buffer = ByteBuffer.allocate( 163840 );

  public StoreServerTCP( int port ) {
    this.port = port;
    answers = new ConcurrentHashMap<Integer, byte[]>();
    decriptor = new Decriptor();
    new Thread( this ).start();

  }

  Selector selector;
  public void run() {
    try {
      ServerSocketChannel serverChannel = ServerSocketChannel.open();
      serverChannel.configureBlocking( false );
      
      ServerSocket serverSocket = serverChannel.socket();
      InetSocketAddress isa = new InetSocketAddress( port );
      serverSocket.bind( isa );

      selector = Selector.open();
      
      serverChannel.register( selector, SelectionKey.OP_ACCEPT );
      System.out.println( "Listening on port "+port );

      while (true) {
        int num = selector.select();

        if (num == 0)  continue;

        Set keys = selector.selectedKeys();
        Iterator it = keys.iterator();
        
        while (it.hasNext()) {
          SelectionKey key = (SelectionKey)it.next();

          if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {

        	System.out.println( "acc" );
            Socket s = serverSocket.accept();
            System.out.println( "Got connection from "+s );

            SocketChannel sc = s.getChannel();
            sc.configureBlocking( false );
            sc.register( selector , SelectionKey.OP_READ );
            
          } else if ((key.readyOps() & SelectionKey.OP_READ) ==
            SelectionKey.OP_READ) {
            SocketChannel sc = null;

            try {
              sc = (SocketChannel)key.channel();
              boolean ok = processInput( sc );

              
              if (!ok) {
                key.cancel();

                Socket s = null;
                try {
                  s = sc.socket();
                  s.close();
                } catch( IOException ie ) {
                  System.err.println( "Error closing socket "+s+": "+ie );
                }
              }

            } catch( IOException ie ) {
            	key.cancel();

	              try {
	            	  Socket s = sc.socket();
	                  s.close();
	                  sc.close();
	              } 
	              catch( IOException ie2 ) { System.out.println( ie2 ); }

              	System.out.println( "Closed "+sc );
            }
          }
        }
        keys.clear();
      }
    } catch( IOException ie ) {
      System.err.println( ie );
    }
  }

  // Do some cheesy encryption on the incoming data,
  // and send it back out
  private boolean processInput( SocketChannel sc ) throws IOException {
    buffer.clear();
    sc.read( buffer );
    buffer.flip();

    // If no data, close the connection
    if (buffer.limit()==0) {
      return false;
    }

    byte[] message = new byte[buffer.limit()];
    for (int i=0; i<buffer.limit(); ++i) {
        message[i] = buffer.get( i );
    }
    
    Random r = new Random();
    int unicNumb = r.nextInt();
    
    while(answers.containsKey(unicNumb)) unicNumb = r.nextInt();
    decriptor.decript(message, unicNumb);
    
    buffer.clear();
   // buffer.flip();
    while(true)
    if(answers.containsKey(unicNumb)) {
    	buffer.put(answers.get(unicNumb));
    	break;
    }
    
    sc.write( buffer );
    answers.remove(unicNumb);
//    buffer.clear();
//    sc.read( buffer );
//    buffer.flip();
//    
//    if (buffer.limit()==0) System.out.println("User doesn`t accept message");
//    //sc.register(selector, SelectionKey.OP_WRITE, unicNumb);
//    
    System.out.println( "Processed "+buffer.limit()+" from "+sc );
    
    return true;
  }
  
  public static byte[] getAnswer(byte[] message) {
	 return message;
  }

  static public void main( String args[] ) throws Exception {
    int port = 1050;
    new StoreServerTCP( port );
  }

}
