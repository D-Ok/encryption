package messaging;

import java.nio.ByteBuffer;
import java.util.LinkedList;

import javax.crypto.BadPaddingException;

//import junit.framework.Test;
//import junit.framework.TestCase;
//import junit.framework.TestSuite;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import messaging.warehouse.Database;
import messaging.warehouse.Good;
import messaging.warehouse.Group;

public class AppTest {
	

	
	@Test 
	public void testDatabaseCreating() {
		Database db = new Database();
		String groupName = "UnitTest";
		String goodName = "Unit";
		db.deleteGroup(groupName);
		db.createGroup(groupName);
		db.createGoods(goodName, groupName, "smb", 20.2);
		
		LinkedList<Group> l = db.getAllGroups();
		Assert.assertTrue(consistGroup(l, groupName));
		
		LinkedList<Good> list = db.getAllGoods();
		Assert.assertTrue(consistGood(list, goodName));

		db.deleteGroup(groupName);
	}
	
	
	@Test 
	public void testDatabaseDeleting() {
		Database db = new Database();
		String groupName = "UnitTest";
		String goodName = "Unit";
		db.deleteGroup(groupName);
		db.createGroup(groupName);
		db.createGoods(goodName, groupName, "smb", 20.2);
		
		db.deleteGroup(groupName);
		LinkedList<Group> l = db.getAllGroups();
		Assert.assertFalse(consistGroup(l, groupName));
		LinkedList<Good> list = db.getAllGoods();
		Assert.assertFalse(consistGood(list, goodName));

		
	}
	
	private boolean consistGroup(LinkedList<Group> l, String groupName) {
		for(Group g: l) {
			if(g.getName().equals(groupName)) return true;
		}
		return false;
	}
	
	private boolean consistGood(LinkedList<Good> l, String goodName) {
		for(Good g: l) {
			if(g.getName().equals(goodName)) return true;
		}
		
		return false;
	}
	
	@Test
	public void testDatabaseUpdating() {
		Database db = new Database();
		String groupName = "UnitTest";
		String goodName = "Unit";
		db.deleteGroup(groupName);
		db.createGroup(groupName);
		db.createGoods(goodName, groupName, "smb", 20.2);
		
		String description = "des";
		db.updateDescriptionOfGood(goodName, description);
		String producer = "prod";
		db.updateProducer(goodName, producer);
		double price =33.3;
		db.setPrice(goodName, price);
		
		Good g = db.getGood(goodName);
		
		Assert.assertEquals(description, g.getDescription());
		Assert.assertEquals(producer, g.getProducer());
		Assert.assertTrue(price == g.getPrice());

		db.deleteGroup(groupName);
	}
	
	@Test
	public void testDatabaseAdditionAndRemoving() {
		Database db = new Database();
		String groupName = "UnitTest";
		String goodName = "Unit";
		db.deleteGroup(groupName);
		db.createGroup(groupName);
		db.createGoods(goodName, groupName, "smb", 20.2);
		
		db.addGoods(goodName, 50);
		Assert.assertEquals(50, db.getQuontityOfGoods(goodName));
		db.removeGoods(goodName, 50);
		Assert.assertEquals(0, db.getQuontityOfGoods(goodName));
		db.removeGoods(goodName, 50);
		Assert.assertEquals(0, db.getQuontityOfGoods(goodName));

		db.deleteGroup(groupName);
	}
	
	

    @Test
    public void testCreator() {
    	JsonObject jo = new JsonObject();
    	jo.addProperty("message", "value");
       Assert.assertNotNull(App.createPackage((byte)1, 5, 678, jo));
       Assert.assertNotNull(App.createPackage((byte)127, 55, 678, jo));
       Assert.assertNull(App.createPackage((byte)129, 5, 678, jo));
       Assert.assertNull(App.createPackage((byte)1, 5, -678, jo));
       Assert.assertNull(App.createPackage((byte)1, 5, 678, null));
    }

    @Test
    public void testGetter() {
    	Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    	
    	JsonObject jo = new JsonObject();
    	jo.addProperty("message", "value");
    	byte src = 10;
        int commandType = 89;
        int userId = 345;
        String expectedString = GSON.toJson(jo);
        byte[] pack = App.createPackage(src, commandType, userId, jo);
        
        PackageGetter pg = App.getPackageAndPrintInformation(pack);
        Assert.assertEquals(expectedString, pg.getMessageString());
        Assert.assertEquals(src, pg.getbSrc());
        Assert.assertEquals(commandType, pg.getcType());
        Assert.assertEquals(userId, pg.getbUserId());
        
        jo.addProperty("name", "Kate");
    	src = 111;
        commandType = 111;
        userId = 1111;
        expectedString = GSON.toJson(jo);
        pack = App.createPackage(src, commandType, userId, jo);
        
        pg = App.getPackageAndPrintInformation(pack);
        Assert.assertEquals(expectedString, pg.getMessageString());
        Assert.assertEquals(src, pg.getbSrc());
        Assert.assertEquals(commandType, pg.getcType());
        Assert.assertEquals(userId, pg.getbUserId());
        
    }
    
    @Test
    public void testGetterIfNull() {
    	Assert.assertNull(App.getPackageAndPrintInformation(null));
    }
    
    @Test
    public void testEnlargingNumberOfMessage() {
    	JsonObject jo = new JsonObject();
    	jo.addProperty("message", "value");
    	
    	byte[] firstMess  = App.createPackage((byte)10, 33, 678, jo);
    	byte[] secondMess = App.createPackage((byte)34, 38888, 898, jo);
    	
    	byte[] pktId1=new byte[8];
    	for(int i=2, f=0; i<10; i++, f++)
    		pktId1[f]=firstMess[i];
    	
    	long bPktId=ByteBuffer.wrap(pktId1).getLong();
    	
    	Assert.assertEquals(bPktId+1, App.getPackageAndPrintInformation(secondMess).getbPktId());
    }

    @Test 
    public void testEncryption() {
    	String message = "some information";
    	byte[] encrypted = Package.encryptMessage(message);
    	
    	try {
			Assert.assertEquals(message, Package.decryptMessage(encrypted));
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
    }
}

