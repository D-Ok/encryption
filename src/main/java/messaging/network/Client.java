package messaging.network;

import java.util.Random;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import messaging.Package;

public class Client {
	
	private static final Random random = new Random();
	
	private static String[] groups = {"groats", "dairy"};
	private static String[] groupG = {"buckwheat", "fig","bulgur"};
	private static String[] groupD = {"milk", "cheese", "butter"};
	
	public static Package generatePackage()
	{
		Package pk = null;
		
		JsonObject jo = new JsonObject();
		int cType= random.nextInt(6);
		switch (cType) {
		case 0:
			int j = random.nextInt(2);
			jo.addProperty("nameOfGroup", groups[j]);
			if(j==0) jo.addProperty("nameOfGoods", groupG[random.nextInt(3)]);
			else jo.addProperty("nameOfGoods", groupD[random.nextInt(3)]);
			break;
		case 1 :
			j = random.nextInt(2);
			jo.addProperty("nameOfGroup", groups[j]);
			if(j==0) jo.addProperty("nameOfGoods", groupG[random.nextInt(3)]);
			else jo.addProperty("nameOfGoods", groupD[random.nextInt(3)]);
			jo.addProperty("quantity", random.nextInt(201));
			break;
		case 2:
			j = random.nextInt(2);
			jo.addProperty("nameOfGroup", groups[j]);
			if(j==0) jo.addProperty("nameOfGoods", groupG[random.nextInt(3)]);
			else jo.addProperty("nameOfGoods", groupD[random.nextInt(3)]);
			jo.addProperty("quantity", random.nextInt(201));
			break;
		case 3:
			jo.addProperty("nameOfGroup", groups[random.nextInt(2)]+""+random.nextInt(1000));
			break;
		case 4:
			jo.addProperty("nameOfGroup", groups[random.nextInt(2)]);
			jo.addProperty("nameOfGoods", "good"+random.nextInt(1000));
			break;
		case 5:
			j = random.nextInt(2);
			jo.addProperty("nameOfGroup", groups[j]);
			if(j==0) jo.addProperty("nameOfGoods", groupG[random.nextInt(3)]);
			else jo.addProperty("nameOfGoods", groupD[random.nextInt(3)]);
			jo.addProperty("price", random.nextInt(9000)/10);
			break;
		default:
			break;
		}
		
		
			try {
				pk = new Package((byte) random.nextInt(127), cType, random.nextInt(1000), (JsonElement) jo);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		return pk;
	}
}
