package messaging.warehouse;
 
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import messaging.exceptions.InvalidCharacteristicOfGoodsException;


public class Warehouse {
	
	public static enum CommandTypes{ QuantityOfGoods, CellGoods, AddGoods, 
		AddGroup, AddNameOfGoodsToGroup, SetPrice};
	

	private ConcurrentHashMap<String, CopyOnWriteArraySet<Goods>> groups;
	
	public Warehouse(){
		groups = new ConcurrentHashMap<String, CopyOnWriteArraySet<Goods>>();

		CopyOnWriteArraySet<Goods> hs = new CopyOnWriteArraySet<Goods>();
		try {
			hs.add(new Goods("buckwheat", 25.5, 1000));
			hs.add(new Goods("fig", 33, 1500));
			hs.add(new Goods("bulgur", 40, 800));
		} catch (InvalidCharacteristicOfGoodsException e) {
			e.printStackTrace();
		}
		groups.put("groats", hs);
		
		CopyOnWriteArraySet<Goods> hs1 = new CopyOnWriteArraySet<Goods>();
		try {
			hs1.add(new Goods("milk", 23, 500));
			hs1.add(new Goods("cheese", 44, 900));
			hs1.add(new Goods("butter", 42, 800));
		} catch (InvalidCharacteristicOfGoodsException e) {
			e.printStackTrace();
		}
		
		groups.put("dairy", hs1);
		
	}
	
	public void addGroup(String name){
		groups.put(name, new CopyOnWriteArraySet<Goods>());
	}
	
	public void addNameOfGoodtToGroup(String nameOfGroup, String nameOfGoods) {
		if(groups.containsKey(nameOfGroup)) {
			groups.get(nameOfGroup).add(new Goods(nameOfGoods));
		}
	}
	
	public void setPrice(String nameOfGroup, String nameOfGoods, double price) throws InvalidCharacteristicOfGoodsException {
		findGoods(nameOfGroup, nameOfGoods).setPrice(price);
	}
	
	public void addGoods(String nameOfGroup, String nameOfGoods, int quantity) throws InvalidCharacteristicOfGoodsException {
		Goods g = findGoods(nameOfGroup, nameOfGoods);
		int newQ = g.getQuantity()+quantity;
		g.setQuantity(newQ);
	}
	
	public void removeGoods(String nameOfGroup, String nameOfGoods, int quantity) throws InvalidCharacteristicOfGoodsException {
		Goods g = findGoods(nameOfGroup, nameOfGoods);
		int newQ = g.getQuantity()-quantity;
		g.setQuantity(newQ);
	}
	
	public int getQuantityOfGoods(String nameOfGroup, String nameOfGoods) {
		return findGoods(nameOfGroup, nameOfGoods).getQuantity();
	}
	
	private Goods findGoods(String nameOfGroup, String nameOfGoods) {
		if(groups.containsKey(nameOfGroup) ) {
			Iterator<Goods> it = groups.get(nameOfGroup).iterator();
			Goods cur;
				while(it.hasNext()) {
					cur = it.next();
					if(cur.getName().equals(nameOfGoods)) return cur;
				}
		}
		 return null;
	}
	
	public String toString() {
		Iterator<String> it = groups.keys().asIterator();
		String cur;
		String result = "";
		while(it.hasNext()) {
			cur = it.next();
			result+="\n"+cur+": ";
			for(Goods g:groups.get(cur)) {
				result+=g+", ";
			}
			
		}
		
		return result;
	}
}
