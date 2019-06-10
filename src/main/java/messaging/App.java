package messaging;
 
import java.util.concurrent.ExecutorService; 
import java.util.concurrent.Executors;

import javax.crypto.BadPaddingException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import messaging.exceptions.ArgumentException;
import messaging.exceptions.InjuredPackageException;
import messaging.exceptions.NoMessageException;

public class App {
	
	private volatile static Receiver r = new Receiver();
		   	  public static void main(String[] args) {
		   		 // generateMessages(100000);
		   		  
		   	  }
		   	  
		   	
		   	  
		   	  public static void sendMess(JsonObject jo, Decriptor dec, int cType) {
		   		  try {
					Package pk = new Package((byte) 1, cType , 123, jo);
					//dec.decript(pk.getWholePackage());
				} catch (Exception e) {
					e.printStackTrace();
				}
		   	  }
		   	  
		   	  public static void generateMessages(int numberOfMessages) {
		   		  ExecutorService ex= Executors.newFixedThreadPool(3);
		   		  
		   		  for(int i=0; i<numberOfMessages; i++) {
		   			  ex.execute(new GeneratorOfMessages());
		   		  }
		   	  }
		   	  
		   	  public static PackageGetter getPackageAndPrintInformation(byte[] bytes) { 
				try {
					PackageGetter pG = new PackageGetter(bytes);
					System.out.println(pG.toString());
					return pG;
				} catch (InjuredPackageException e) {
					System.out.println("Package was injured");
				} catch (NullPointerException | BadPaddingException e) {
					System.out.println("Argyment can`t be null");
				}
				return null;
			}

			public static byte[] createPackage(byte src, int commandType, int userId, JsonElement je) {
		   		try {
					Package packege = new Package( src, commandType, userId, je);
					byte[] bytes = packege.getWholePackage();
					return bytes;
					
				} catch (NoMessageException e) {
					System.out.println(e.getMessage());
				} catch (ArgumentException e) {
					System.out.println("Please, enter correct data");
				}catch (NullPointerException e) {
					System.out.println("Argyment can`t be null");
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
		   		return null;
		   	  }
			
			private static class GeneratorOfMessages implements Runnable{

				@Override
				public void run() {
					r.receiveMessage();
				}
				
			}
}
