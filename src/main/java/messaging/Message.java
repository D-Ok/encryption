package messaging; 

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Message {
	
	private int cType;
	private int userId;
	
	private JsonObject jo;
	
	private String informationMessage;
	
	public int getcType() {
		return cType;
	}

	public int getUserId() {
		return userId;
	}

	public String getNameOfGoods() {
		if(jo.has("nameOfGoods")) return jo.get("nameOfGoods").getAsString();
		else return "";
	}

	public String getNameOfGroup() {
		if(jo.has("nameOfGroup")) return jo.get("nameOfGroup").getAsString();
		else return "";
	}

	public int getQuantity() {
		if(jo.has("quantity")) return jo.get("quantity").getAsInt();
		else return 0;
	}

	public double getPrice() {
		if(jo.has("price")) return jo.get("price").getAsDouble();
		else return 0;
	}
	
	public Message(int cType, int userId, JsonElement message){
		
		this.cType = cType;
		this.userId = userId;
		
		this.jo = (JsonObject) message;
		informationMessage = message.toString();
		
	}
	
	public Message(int cType, int userId, String someInformation) {
		this.cType = cType;
		this.userId = userId;
		informationMessage = someInformation;
		
		jo = new JsonObject();
		jo.addProperty("message", informationMessage);

	}

	public JsonObject getJsonMessage() {
		return jo;
	}

	public String getInformationMessage() {
		return informationMessage;
	}
	
	
}


