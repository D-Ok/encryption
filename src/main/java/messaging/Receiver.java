package messaging;

import java.util.Random;

import com.google.gson.JsonObject;

public class Receiver {
	
	final Random random = new Random();
	Decriptor decriptor = new Decriptor();
	
	public void receiveMessage() {
		
		Package pk;
		for(int i=0; i<100; i++) {
			
			
			JsonObject jo = new JsonObject();
   			jo.addProperty("userName", "Mary");
   			jo.addProperty("location", "Chernihiv");
			//pk = new Package(random.nextInt(128), , random.nextInt(), jsonMessage)
			
		}
	}
}