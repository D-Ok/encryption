package messaging.warehouse;

import java.util.HashMap;
import java.util.HashSet;

public class Warehouse {
	
	static enum CommandTypes{ QuontityOfGoods, CellGoods, AddGoods, AddGroup, AddNameOfGoodsToGroup, SetPrice};
	

	private HashMap<String, HashSet<Goods>> groups;
	
	Warehouse(){
		groups = new HashMap<String, HashSet<Goods>>();
	}
	
	public void addGroup(String name){
		groups.put(name, new HashSet<Goods>());
	}
	
	public void addNameOfGoodtToGroup(String nameOfGroup, String nameOfGoods) {
		if(groups.containsKey(nameOfGroup)) {
			groups.get(nameOfGroup).add(new Goods(nameOfGoods));
		}
	}
	
	public void setPrice(String nameOfGroup, String nameOfGoods, double price) {
		findGoods(nameOfGroup, nameOfGoods).setPrice(price);
	}
	
	public void addGoods(String nameOfGroup, String nameOfGoods, int quantity) {
		Goods g = findGoods(nameOfGroup, nameOfGoods);
		g.setQuantity(g.getQuantity()+quantity);
	}
	
	public void removeGoods(String nameOfGroup, String nameOfGoods, int quantity) {
		Goods g = findGoods(nameOfGroup, nameOfGoods);
		g.setQuantity(g.getQuantity()-quantity);
	}
	
	public int getQuantityOfGoods(String nameOfGroup, String nameOfGoods) {
		return findGoods(nameOfGroup, nameOfGoods).getQuantity();
	}
	
	private Goods findGoods(String nameOfGroup, String nameOfGoods) {
		if(groups.containsKey(nameOfGroup) ) {
			if(groups.get(nameOfGroup).contains(nameOfGoods)) {
				for(Goods g:groups.get(nameOfGroup)) {
					if(g.getName()==nameOfGoods)
						{
							return g;
						}
				}
			}
		}
		 return null;
	}
}
