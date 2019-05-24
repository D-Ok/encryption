package messaging;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Message {
	
	enum CommandTypes{ QuontityOfGoods, CellGoods, AddGoods, AddGroup, AddNameOfGoodsToGroup, SetPrice};
	
	int cType;
	int userId;
	
	String nameOfGoods;
	String nameOfGroup;
	int quontity;
	double price;
	
	public Message(int cType, int userId, JsonElement message){
		
		this.cType = cType;
		this.userId = userId;
		
		JsonObject jo = (JsonObject) message;
		if(jo.has(nameOfGoods)) nameOfGoods=jo.get(nameOfGoods).getAsString();
		
	}
	
	
}


