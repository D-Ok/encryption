package messaging.warehouse;

import java.util.HashMap;
import java.util.HashSet;

public class Warehouse {
	
	static enum CommandTypes{ QuontityOfGoods, CellGoods, AddGoods, 
		AddGroup, AddNameOfGoodsToGroup, SetPrice};
	

	private HashMap<String, HashSet<Goods>> groups;
	
	Warehouse(){
		groups = new HashMap<String, HashSet<Goods>>();
		
		HashSet<Goods> hs = new HashSet<Goods>();
		hs.add(new Goods("buckwheat", 25.5, 1000));
		hs.add(new Goods("fig", 33, 1500));
		hs.add(new Goods("bulgur", 40, 800));
		groups.put("groats", hs);
		
		HashSet<Goods> hs1 = new HashSet<Goods>();
		hs.add(new Goods("milk", 23, 500));
		hs.add(new Goods("cheese", 44, 900));
		hs.add(new Goods("butter", 42, 800));
		groups.put("dairy", hs1);
		
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
