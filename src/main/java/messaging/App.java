package messaging;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import messaging.exceptions.ArgumentException;
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
		   			byte[] bytes = createPackage(src, 4, 145, jo);
		   			getPackageAndPrintInformation(bytes);
		   			
		   			src = 8;
		   			
		   			jo = new JsonObject();
		   			jo.addProperty("userName", "Mark");
		   			jo.addProperty("location", "Kyiv");
		   			jo.addProperty("message", "useful information");
		   			
		   			bytes = createPackage(src, 2, 333, jo);
		   			getPackageAndPrintInformation(bytes);

		   			bytes = createPackage((byte)3313, 4, 145, jo);
		   			getPackageAndPrintInformation(bytes);
		   		
		   	  }
		   	  
		   	  public static PackageGetter getPackageAndPrintInformation(byte[] bytes) { 
				try {
					PackageGetter pG = new PackageGetter(bytes);
					System.out.println(pG.toString());
					return pG;
				} catch (InjuredPackageException e) {
					System.out.println("Package was injured");
				} catch (NullPointerException e) {
					System.out.println("Argyment can`t be null");
				}
				return null;
			}

			public static byte[] createPackage(byte src, int commandType, int userId, JsonElement je) {
		   		try {
					PackageCreator packege = new PackageCreator( src, commandType, userId, je);
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
}
