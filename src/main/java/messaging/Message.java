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
		return jo.get("nameOfGoods").getAsString();
	}

	public String getNameOfGroup() {
		return jo.get("nameOfGroup").getAsString();
	}

	public int getQuantity() {
		return jo.get("quantity").getAsInt();
	}

	public double getPrice() {
		return jo.get("price").getAsDouble();
	}
	
	public Message(int cType, int userId, JsonElement message){
		
		this.cType = cType;
		this.userId = userId;
		
		this.jo = (JsonObject) message;
		informationMessage = message.getAsString();
		
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


