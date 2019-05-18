package messaging;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class App {
	   //public static void main(String[] args) {
		   //
//		       	String str="string";
////		           if (str.length < 1) {
////		               System.err.println("Please provide an input!");
////		               System.exit(0);
////		           }
//		           System.out.println(sha256hex(str));
		   //
//		       }
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

		   	  public static void main(String[] args) {

//		   	        if (args.length < 1) {
//		   	            System.err.println("Please provide an input!");
//		   	            System.exit(0);
//		   	        }
//		   	        System.out.println(sha256hex(args[0]));
		   		  try {
		   			  
		   			JsonArray ja = new JsonArray();
		   			ja.add("some string");
		   			ja.add("one more string");
		   			
		   			JsonObject jo = new JsonObject();
		   			jo.addProperty("userName", "Mary");
		   			jo.add("messages", ja);
		   			
		   			byte src = 1;
		   			PackageCreator packege1 = new PackageCreator( src, 1, 145, jo);
		   			byte[] bytes1 = packege1.getWholePackage();
		   			for(int i=0; i<bytes1.length; i++)
		   				System.out.println((i)+" - "+bytes1[i]);
		   			
		   			PackageCreator p2 = new PackageCreator(src, 2, 333, jo);
		   			byte[] bytes2 = p2.getWholePackage();
		   			for(int i=0; i<bytes2.length; i++)
		   				System.out.println((i)+" - "+bytes2[i]);
		   			
		   			PackageChecker pCh = new PackageChecker(bytes2);
		   			System.out.println(pCh.isCorrectContent());
		   			System.out.println(pCh.isCorrectDescription());
		   			
		   			PackageGetter pG = new PackageGetter(bytes1);
		   			System.out.println(pG.toString());
		   			
//		   			String jsonStr = GSON.toJson((JsonElement)jo);
//		   			System.out.println(jsonStr);
//		   			
//		   			JsonElement newEl = GSON.fromJson(jsonStr, JsonElement.class );
//		   			System.out.println(GSON.toJson(newEl));
		   		
		   			
		   		} catch (Exception e) {
		   			e.printStackTrace();
		   		}
		   	  }
		   	  
		       public static String sha256hex(String input) {
		           return DigestUtils.sha256Hex(input);
		       }
		       
}
