package messaging;

import java.util.Random;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Receiver {
	
	final Random random = new Random();
	Decriptor decriptor = new Decriptor();
	
	private String[] groups = {"groats", "dairy"};
	private String[] groupG = {"buckwheat", "fig","bulgur"};
	private String[] groupD = {"milk", "cheese", "butter"};
	
	public void receiveMessage() {
		
		Package pk;
		for(int i=0; i<100; i++) {
			
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
				jo.addProperty("nameOfGoods", ""+random.nextInt(1000));
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
				decriptor.decript(pk.getWholePackage());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
}