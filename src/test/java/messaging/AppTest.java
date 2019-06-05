package messaging;

import java.nio.ByteBuffer;

import javax.crypto.BadPaddingException;

//import junit.framework.Test;
//import junit.framework.TestCase;
//import junit.framework.TestSuite;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import messaging.exceptions.ArgumentException;
import messaging.exceptions.NoMessageException;
import messaging.warehouse.Warehouse;

public class AppTest {
	
	@Test
	public void testRightQuantity() {
		Decriptor dec = new Decriptor();
 		  
 		  String[] groups = {"groats", "dairy"};
 		  String[] groupG = {"buckwheat", "fig","bulgur"};
 		  String[] groupD = {"milk", "cheese", "butter"};
 		  
 		  System.out.println("Before commands: \n"+Processor.warehouse.toString());
 		
 		  JsonObject jo = new JsonObject();
 		  jo.addProperty("nameOfGroup", groups[1]);
 		  jo.addProperty("nameOfGoods", groupD[1]);
 		  jo.addProperty("quantity", 200);
 		  App.sendMess(jo, dec, Warehouse.CommandTypes.AddGoods.ordinal());
 		  
 		  jo = new JsonObject();
 		  jo.addProperty("nameOfGroup", groups[1]);
 		  jo.addProperty("nameOfGoods", groupD[1]);
 		  jo.addProperty("quantity", 100);
 		 App.sendMess(jo, dec, Warehouse.CommandTypes.AddGoods.ordinal());
 		  
 	   	  jo = new JsonObject();
 		  jo.addProperty("nameOfGroup", groups[1]);
 		  jo.addProperty("nameOfGoods", groupD[1]);
 		  jo.addProperty("quantity", 200);
 		 App.sendMess(jo, dec, Warehouse.CommandTypes.AddGoods.ordinal());
 		  
 		  jo = new JsonObject();
 		  jo.addProperty("nameOfGroup", groups[1]);
 		  jo.addProperty("nameOfGoods", groupD[1]);
 		  jo.addProperty("quantity", 1000);
 		 App.sendMess(jo, dec, Warehouse.CommandTypes.CellGoods.ordinal());
 		  
 		  jo = new JsonObject();
 		  jo.addProperty("nameOfGroup", groups[1]);
 		  jo.addProperty("nameOfGoods", groupD[1]);
 		  jo.addProperty("quantity", 400);
 		 App.sendMess(jo, dec, Warehouse.CommandTypes.CellGoods.ordinal());
 		  
 		  jo = new JsonObject();
 		  jo.addProperty("nameOfGroup", groups[1]);
 		  jo.addProperty("nameOfGoods", groupD[1]);
 		  jo.addProperty("quantity", 300);
 		 App.sendMess(jo, dec, Warehouse.CommandTypes.AddGoods.ordinal());
 		  
 		  jo = new JsonObject();
 		  jo.addProperty("nameOfGroup", groups[1]);
 		  jo.addProperty("nameOfGoods", groupD[1]);
 		  jo.addProperty("quantity", 1000);
 		 App.sendMess(jo, dec, Warehouse.CommandTypes.CellGoods.ordinal());
		try {
			Thread.sleep(1500);
			int quantity = Processor.warehouse.getQuantityOfGoods(groups[1], groupD[1]);
			Assert.assertEquals(300, quantity);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGenerator() {
		//App.generateMessages(100000);
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
    public void testChecker() {
    	
    	JsonObject jo = new JsonObject();
    	jo.addProperty("message", "value");
    	byte [] bytes = App.createPackage((byte)1, 5, 678, jo);
    	
    	PackageChecker pCh = new PackageChecker(bytes, 10);
    	Assert.assertTrue(pCh.isCorrect());
    	Assert.assertTrue(pCh.isCorrectDescription());
    	Assert.assertTrue(pCh.isCorrectContent());
    	
    	bytes[0]--;
    	pCh = new PackageChecker(bytes, 10);
    	Assert.assertFalse(pCh.isCorrect());
    	Assert.assertFalse(pCh.isCorrectDescription());
    	Assert.assertTrue(pCh.isCorrectContent());
    	
    	bytes[bytes.length-3]++;
    	pCh = new PackageChecker(bytes, 10);
    	Assert.assertFalse(pCh.isCorrect());
    	Assert.assertFalse(pCh.isCorrectDescription());
    	Assert.assertFalse(pCh.isCorrectContent());
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

