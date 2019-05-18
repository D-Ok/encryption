package messaging;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import messaging.exceptions.InjuredPackageException;
import messaging.exceptions.NoMessageException;

public class App {
	
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

		   	  public static void main(String[] args) {
		   	
		   			JsonArray ja = new JsonArray();
		   			ja.add("some string");
		   			ja.add("one more string");
		   			
		   			JsonObject jo = new JsonObject();
		   			jo.addProperty("userName", "Mary");
		   			jo.addProperty("location", "Chernihiv");
		   			jo.add("messages", ja);
		   			
		   			byte src = 1;
		   			createAndGetPackage(src, 4, 145, jo);
					
		   			src = 8;
		   			
		   			jo = new JsonObject();
		   			jo.addProperty("userName", "Mark");
		   			jo.addProperty("location", "Kyiv");
		   			jo.addProperty("message", "useful information");
		   			
		   			createAndGetPackage(src, 2, 333, jo);
		   			
		   		
		   	  }
		   	  
		   	  public static void createAndGetPackage(byte src, int commandType, int userId, JsonElement je) {
		   		try {
					PackageCreator packege = new PackageCreator( src, commandType, userId, je);
					byte[] bytes = packege.getWholePackage();
					
					PackageGetter pG = new PackageGetter(bytes);
		   			System.out.println(pG.toString());
				} catch (NoMessageException e) {
					e.printStackTrace();
				} catch (InjuredPackageException e) {
					e.printStackTrace();
				}
		   	  }
		   	  
		       public static String sha256hex(String input) {
		           return DigestUtils.sha256Hex(input);
		       }
		       
}
