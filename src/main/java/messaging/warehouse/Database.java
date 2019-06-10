package messaging.warehouse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArraySet;

import messaging.exceptions.InvalidCharacteristicOfGoodsException;

public class Database  {
	private Connection connection;
	
	public Database() {
		String user = "root";
    	String password = "root35";
    	String connectionURL = "jdbc:mysql://localhost:3306/messaging?serverTimezone=Europe/Kiev&useSSL=false";
    	try {
			Class.forName("com.mysql.cj.jdbc.Driver");
	    	connection = DriverManager.getConnection(connectionURL, user, password);
	    	creatingTables();
    	}catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	public void createGroup(String nameOfGroup) {
		try {
	    	PreparedStatement ps = connection.prepareStatement("Insert into `groups` (`name`) values (?)");
	    	ps.setString(1, nameOfGroup);
	    	ps.executeUpdate();
	    	ps.close();
    	} catch(SQLIntegrityConstraintViolationException e) {
    		System.out.println("Group with this name exist.");
    	} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void createGroup(String nameOfGroup, String description) {
		try {
	    	PreparedStatement ps = connection.prepareStatement("Insert into `groups` (`name`, `description`) values (?, ?)");
	    	ps.setString(1, nameOfGroup);
	    	ps.setString(2,  description);
	    	ps.executeUpdate();
	    	ps.close();
    	} catch(SQLIntegrityConstraintViolationException e) {
    		System.out.println("Group with this name exist.");
    	} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void createGoods(String nameOfGoods, String nameOfGroup, String description, String producer, int quontity, double price) throws InvalidCharacteristicOfGoodsException { 
		if(quontity<0 || price<=0 ) throw new InvalidCharacteristicOfGoodsException("Ucorrect parameters");
		try {
	    	PreparedStatement ps = connection.prepareStatement("Insert into `goods` (`name`, `description`, `price`, `quontity`, `producer`, `groupName`) values (?, ?, ?, ?, ?, ?)");
	    	ps.setString(1, nameOfGoods);
	    	ps.setString(2, description);
	    	ps.setDouble(3, price);
	    	ps.setInt(4, quontity);
	    	ps.setString(5, producer);
	    	ps.setString(6, nameOfGroup);
	    	ps.executeUpdate();
	    	ps.close();
    	} catch(SQLIntegrityConstraintViolationException e) {
    		System.out.println("Goods with this name exist.");
    	} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void createGoods(String nameOfGoods, String nameOfGroup, String producer, int quontity, double price) throws InvalidCharacteristicOfGoodsException {
		if(quontity<0 || price<=0 ) throw new InvalidCharacteristicOfGoodsException("Ucorrect parameters");
		try {
	    	PreparedStatement ps = connection.prepareStatement("Insert into `goods` (`name`, `price`, `quontity`, `producer`, `groupName`) values (?, ?, ?, ?, ?)");
	    	ps.setString(1, nameOfGoods);
	    	ps.setDouble(2, price);
	    	ps.setInt(3, quontity);
	    	ps.setString(4, producer);
	    	ps.setString(5, nameOfGroup);
	    	ps.executeUpdate();
	    	ps.close();
    	} catch(SQLIntegrityConstraintViolationException e) {
    		System.out.println("Goods with this name exist.");
    	} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void createGoods(String nameOfGoods, String nameOfGroup, String producer, double price) throws InvalidCharacteristicOfGoodsException {
		if(price<=0 ) throw new InvalidCharacteristicOfGoodsException("Ucorrect parameters");
		try {
	    	PreparedStatement ps = connection.prepareStatement("Insert into `goods` (`name`, `price`, `quontity`, `producer`, `groupName`) values (?, ?, ?, ?, ?)");
	    	ps.setString(1, nameOfGoods);
	    	ps.setDouble(2, price);
	    	ps.setInt(3, 0);
	    	ps.setString(4, producer);
	    	ps.setString(5, nameOfGroup);
	    	ps.executeUpdate();
	    	ps.close();
    	} catch(SQLIntegrityConstraintViolationException e) {
    		System.out.println("Goods with this name exist.");
    	} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public LinkedList<Group> getAllGroups() { 
		String command = "SELECT * FROM messaging.groups";
		return getGroups(command);
	}
	
	public LinkedList<Good> getAllGoodsOfTheGroup(String nameOfGroup) {
		String str ="SELECT * FROM goods WHERE goods.groupName='"+nameOfGroup+"'";
		return getGoods(str);
		
	}
	
	public LinkedList<Good> getAllGoods() {
		String str ="SELECT * FROM goods";
		return getGoods(str);
		
	}
	
	public Good getGood(String nameOfGood) { 
		String str ="SELECT * FROM goods WHERE goods.name='"+nameOfGood+"'";
		Good g = null;
		try {
			Statement statement = connection.createStatement();
	    	ResultSet rs = statement.executeQuery(str);
	    	if(rs.next()) {
	    		g = new Good(rs.getString("name"), rs.getString("description"), rs.getString("producer"), rs.getString("groupName"), rs.getDouble("price"), rs.getInt("quontity"));
	    		System.out.println("id = "+rs.getInt("id")+", name : "
	    	+rs.getString("name")+", description: "+rs.getString("description")+", producer: "+rs.getString("producer")
	    	+", price: "+rs.getDouble("price")+", quontity: "+rs.getInt("quontity")+", group: "+rs.getString("groupName"));
	    	}else System.out.println("Good with this name doesn`t exist. ");
	    	rs.close();
	    	statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return g;
	}
	
	
	public void addGoods(String nameOfGood, int quontity){
		String command = "SELECT * FROM goods WHERE goods.name='"+nameOfGood+"'";
		try {
			Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
	    	ResultSet rs = statement.executeQuery(command);
	    	if(rs.next()) {
	    		int old = rs.getInt("quontity");
		    	rs.updateInt("quontity", old+quontity);
		    	rs.updateRow();
	    	}
	    	rs.close();
	    	statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removeGoods(String nameOfGood, int quontity) throws InvalidCharacteristicOfGoodsException{
		String command = "SELECT * FROM goods WHERE goods.name='"+nameOfGood+"'";
		try {
			Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
	    	ResultSet rs = statement.executeQuery(command);
	    	if(rs.next()) {
	    		int old = rs.getInt("quontity");
	    		if(old-quontity>=0) {
			    	rs.updateInt("quontity", old-quontity);
			    	rs.updateRow();
	    		} else {
	    			rs.close();
	    			throw new InvalidCharacteristicOfGoodsException("Can`t remove products.");
	    		}
	    	}
	    	rs.close();
	    	statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getQuontityOfGoods(String nameOfGood) {
		
		String str ="SELECT * FROM goods WHERE goods.name='"+nameOfGood+"'";
		
		try {
			Statement statement = connection.createStatement();
	    	ResultSet rs = statement.executeQuery(str);
	    	if(rs.next()) return rs.getInt("quontity");
	    	rs.close();
	    	statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	
	public LinkedList<Group> listByGroupNameBeginsWith(String groupName) {
		String command = "SELECT * FROM `groups` WHERE `groups`.`name` LIKE '"+groupName+"%'";
		return getGroups(command);
	}
	
	public LinkedList<Group> listByGroupNameEndsWith(String groupName) {
		String command = "SELECT * FROM `groups` WHERE `name` LIKE '%"+groupName+"'";
		return getGroups(command);
	}
	
	public LinkedList<Group> sortGroupsByNameIncrease() {
		String command = "SELECT * FROM `groups` ORDER BY `groups`.`name`";
		return getGroups(command);
	}
	
	public LinkedList<Group> sortGroupsByNameDecrease() {
		String command = "SELECT * FROM `groups` ORDER BY `groups`.`name` DESC";
		return getGroups(command);
	}
	
	public LinkedList<Good> listByGoodNameBeginWith(String goodName) {
		String command = "SELECT * FROM goods WHERE goods.name LIKE '"+goodName+"%'";
		return getGoods(command);
	}
	
	public LinkedList<Good> listByGoodNameEndWith(String goodName) {
		String command = "SELECT * FROM goods WHERE goods.name LIKE '%"+goodName+"'";
		return getGoods(command);
	}
	
	public LinkedList<Good> listByGoodProducerBeginWith(String producer) {
		String command = "SELECT * FROM goods WHERE goods.producer LIKE '"+producer+"%'";
		return getGoods(command);
	}
	
	public LinkedList<Good> listByGoodProducerEndWith(String producer) {
		String command = "SELECT * FROM goods WHERE goods.producer LIKE '%"+producer+"'";
		return getGoods(command);
	}
	
	public LinkedList<Good> listByGoodProducer(String producer) {
		String command = "SELECT * FROM goods WHERE goods.producer='"+producer+"'";
		return getGoods(command);
	}
	
	public LinkedList<Good> sortGoodsByPriceIncrease() {
		String command = "SELECT * FROM goods ORDER BY goods.price";
		return getGoods(command);
	}
	
	public LinkedList<Good> sortGoodsByPriceDecrease() {
		String command = "SELECT * FROM goods ORDER BY goods.price DESC";
		return getGoods(command);
	}
	
	public LinkedList<Good> sortGoodsByQuontityIncrease() {
		String command = "SELECT * FROM goods ORDER BY goods.quontity";
		return getGoods(command);
	}
	
	public LinkedList<Good> sortGoodsByQountityDecrease() {
		String command = "SELECT * FROM goods ORDER BY goods.quontity DESC";
		return getGoods(command);
	}
	
	
	public void deleteGood(String nameOfGood) {
		String str = "delete from goods where name='"+nameOfGood+"'";
		update(str);
	}
	
	public void deleteGroup(String nameOfGroup) {
		String str = "delete from `groups` where `name`='"+nameOfGroup+"'";
		update(str);
	}
	
	public void setPrice(String nameOfGood, double price) throws InvalidCharacteristicOfGoodsException{
		if(price<=0) throw new InvalidCharacteristicOfGoodsException("Price must be > 0");
		else {
			String s = "update `goods` set `price`='"+price+"' where `name`='"+nameOfGood+"'";
			update(s);
		}
	}
	
	public void updateNameOfGroup(String nameOfGroup, String newNameOfGroup) {
		String s = "update `groups` set `name`='"+newNameOfGroup+"' where `name`='"+nameOfGroup+"'";
		update(s);
	}
	
	public void updateNameOfGood(String nameOfGood, String newNameOfGood) {
		String s = "update `goods` set `name`='"+newNameOfGood+"' where `name`='"+nameOfGood+"'";
		update(s);
	}
	
	public void updateDescriptionOfGroup(String groupName, String description) {
		String s = "update `groups` set `description`='"+description+"' where `name`='"+groupName+"'";
		update(s);
	}
    
	public void updateDescriptionOfGood(String nameOfGood, String description) {
		String s = "update `goods` set `description`='"+description+"' where `name`='"+nameOfGood+"'";
		update(s);
	}
	
	public void updateProducer(String nameOfGood, String producer) {
			String s = "update `goods` set `producer`='"+producer+"' where `name`='"+nameOfGood+"'";
			update(s);
	}
	
	
	private void update(String command) {
		try {
			Statement st = connection.createStatement();
			st.executeUpdate(command);
			st.close();
		} catch (SQLException e) {
			System.out.println("Uncorrect data.");
		}
	}
	
	private LinkedList<Good> getGoods(String command) { 
		LinkedList<Good> result = new LinkedList<Good>();
		
		try {
			Statement statement = connection.createStatement();
	    	ResultSet rs = statement.executeQuery(command);
	    	while(rs.next()) {
	    		result.add(new Good(rs.getString("name"), rs.getString("description"), rs.getString("producer"), rs.getString("groupName"), rs.getDouble("price"), rs.getInt("quontity")));
	    		System.out.println(rs.getRow()+". "+"id = "+rs.getInt("id")+", name : "
	    	+rs.getString("name")+", description: "+rs.getString("description")+", producer: "+rs.getString("producer")
	    	+", price: "+rs.getDouble("price")+", quontity: "+rs.getInt("quontity")+", group: "+rs.getString("groupName"));
	    	}
	    	rs.close();
	    	statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	private LinkedList<Group> getGroups(String command) {
		LinkedList<Group> result = new LinkedList<Group>();
		Statement statement;
		try {
			statement = connection.createStatement();
	    	ResultSet rs = statement.executeQuery(command);
	    	while(rs.next()) {
	    		result.add(new Group(rs.getString("name"), rs.getString("description")));
	    		System.out.println(rs.getRow()+". "+"id = "+rs.getInt("id")+", name : "+rs.getString("name")
	    		+", description: "+rs.getString("description"));
	    	}
	    	rs.close();
	    	statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	private void creatingTables() { 
		String goodsTable = "create table if not exists `goods`"
				+ " (`id` INT NOT NULL AUTO_INCREMENT, "
				+ "`name` VARCHAR(45) NOT NULL, "
				+ "`description` VARCHAR(1000) NULL, "
				+ "`producer` VARCHAR(45) NOT NULL, "
				+ "`price` DOUBLE NOT NULL, "
				+ "`quontity` INT NOT NULL, "
				+ "`groupName` VARCHAR(45) NOT NULL, "
				+ "PRIMARY KEY (`id`), "
				+ "UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE, "
				+ "UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE, "
				+ "FOREIGN KEY (`groupName`) REFERENCES `groups`(`name`) "
				+ "ON DELETE CASCADE "
				+ "ON UPDATE CASCADE "
				+ ");";
		
		String groupsTable = "create table if not exists `groups`"
				+ " (`id` INT NOT NULL AUTO_INCREMENT, "
				+ "`name` VARCHAR(45) NOT NULL, "
				+ "`description` VARCHAR(1000) NULL, "
				+ "PRIMARY KEY (`id`, `name`), "
				+ "UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE, "
				+ "UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE);";
		
		 PreparedStatement st;
		 
		try {
			st = connection.prepareStatement(groupsTable);
			st.executeUpdate();
			
			st = connection.prepareStatement(goodsTable);
	        st.executeUpdate();
	        
	        st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
    public static void main(String[] args){
    	
    	   Database db = new Database();
//    	   db.createGoods("milk", "dairy", "milk product", "Kyiv", 600, 23.9);
    	   
//    	   db.createGroup("dairy", "products with milk");
//    	   db.createGroup("groats", "description");
//    	   db.createGroup("dairy");
//    	   db.createGroup("groupToTest", "test group");
    	   
//    	   db.getAllGroups();
    	   
//    	   db.createGoods("milk", "dairy", "milk product", "Kyiv", 600, 23.9);
//    	   db.createGoods("cheese", "dairy", "milk product", "Poltava", 780, 44);
//    	   db.createGoods("butter", "dairy", "milk product", "Chernihiv", 450, 43.5);
//    	   
//    	   db.createGoods("buckwheat", "groats", "description", "Kyiv", 1000, 25.5);
//    	   db.createGoods("fig", "groats", "description", "Chernihiv", 1500, 33);
//    	   db.createGoods("bulgur", "groats", "description", "Kyiv", 800, 40);
//    	   
//    	   db.createGoods("goods1", "groupToTest", "description", "Poltava", 600, 23.9);
//    	   db.createGoods("goods2", "groupToTest", "description", "Kyiv", 600, 23.9);
//    	   db.createGoods("goods3", "groupToTest", "description", "Kyiv", 600, 23.9);
//    	   db.createGoods("goods3", "groupToTest", "description", "Kyiv", 600, 23.9);
    	   
//    	   db.getAllGoods();
//    	   db.getAllGoodsOfTheGroup("groupToTest");
//    	   db.getGood("mmm");
//    	   db.getGood("milk");
    	   
//    	   db.removeGoods("goods1", 800);
//    	   db.addGoods("goods1", 200);
//    	   System.out.println("Quontity afrer addition 200 = "+db.getQuontityOfGoods("goods1"));
//    	   db.removeGoods("goods1", 800);
//    	   System.out.println("Quontity remove 800 = "+db.getQuontityOfGoods("goods1"));
    	   
//    	   System.out.println();
//    	   db.listByGoodNameBeginWith("goo");
//    	   System.out.println();
//    	   db.listByGoodNameEndWith("r");
//    	   System.out.println();
    	   
//    	   db.listByGroupNameBeginsWith("d");
//    	   System.out.println();
//    	   db.listByGroupNameEndsWith("t");
//    	   System.out.println();
//    	   db.listByGroupNameEndsWith("a");
//    	   System.out.println();
    	   
//    	   db.listByGoodProducer("Kyiv");
//    	   System.out.println();
//    	   db.listByGoodProducerBeginWith("Ch");
//    	   System.out.println();
//    	   db.listByGoodProducerEndWith("a");
    	   
//    	   System.out.println();
//    	   db.sortGroupsByNameIncrease();
//    	   System.out.println();
//    	   db.sortGroupsByNameDecrease();
//    	   
//    	   System.out.println();
//    	   db.sortGoodsByPriceIncrease();
//    	   System.out.println();
//    	   db.sortGoodsByPriceDecrease();
//    	   
//    	   System.out.println();
//    	   db.sortGoodsByQuontityIncrease();
//    	   System.out.println();
//    	   db.sortGoodsByQountityDecrease();
    	   
//    	   db.updateDescriptionOfGroup("groupToTest", "testing");
//    	   db.updateNameOfGroup("groupToTest", "test");
//    	   db.getAllGroups();
//    	   db.getAllGoods();
//    	   
//    	   db.updateNameOfGroup("test", "dairy");
    	   
//    	   db.updateNameOfGood("good", "milk");
//    	   db.updateNameOfGood("g", "gg");
//    	   db.updateNameOfGood("goods1", "good");
//    	   db.updateDescriptionOfGood("good", "sssss");
//    	   db.updateProducer("good", "Lviv");
//    	   db.setPrice("good", 90);
//    	   db.getAllGoods();
    	   
    	   db.deleteGood("good");
    	   db.getAllGoods();
    	   System.out.println();
    	   db.deleteGroup("test");
    	   db.getAllGroups();
    	   System.out.println();
    	   db.getAllGoods();
    	   
		} 

}
