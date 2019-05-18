package messaging;

import java.io.UnsupportedEncodingException; 
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import messaging.exceptions.NoMessageException;

public class PackageCreator {
	
	/*
	 * 
byte[] arr = { 0x00, 0x01 };
ByteBuffer wrapped = ByteBuffer.wrap(arr); // big-endian by default
short num = wrapped.getShort(); // 1

ByteBuffer dbuf = ByteBuffer.allocate(2);
dbuf.putShort(num);
byte[] bytes = dbuf.array(); // { 0, 1 }
	 */
	//номер повідомлення 
	private static long bPktIdLong=0;
	private static Key key;
	private static Cipher cipher;
	
	private final byte bMagic= 0xD;
	private byte bSrc;
	private byte[] bPktId = new byte[8];
	private byte[] wLen = new byte[4];
	private byte[] wCrc16= new byte[2];
	
	private byte[] bMsq;
		private byte[] cType = new byte[4];
		private byte[] bUserId = new byte[4];
		private byte[] message;
		
	private byte[] wCrc16Message = new byte[2];
	
	private byte[] wholePackage;
	private byte[] discriptionPart = new byte[14];
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	public PackageCreator( byte src, int commandType, int userId, JsonElement jsonMessage) throws NoMessageException {
		if(jsonMessage == null) throw new NoMessageException();
		String inputMessage = GSON.toJson(jsonMessage);
		try {
			if(cipher==null) cipher = Cipher.getInstance("DES");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		message = encryptMessage(inputMessage);
		if(message.length==0) throw new NoMessageException("Can`t send an empty string");
		bMsq = new byte[message.length+8];
		wholePackage = new byte[message.length+26];
		//augment message number
		bPktIdLong++;
		
		ByteBuffer buf = ByteBuffer.allocate(8);
		buf.putLong(bPktIdLong);
		bPktId = buf.array();
		
		//creating byte with src
		bSrc = src;
		
		//creating byte[] with length of message
		buf = ByteBuffer.allocate(4);
		buf.putInt(message.length+8);
		wLen = buf.array();
		
		discriptionPart[0]=bMagic;
		wholePackage[0]=bMagic;
		discriptionPart[1]=bSrc;
		wholePackage[1]=bSrc;
		
		for (int i=2, j=0, k=0; i<14; i++){
			if(i<10) {
				discriptionPart[i]=bPktId[j];
				wholePackage[i]=bPktId[j];
				j++;
			}else {
				discriptionPart[i]=wLen[k];
				wholePackage[i]=wLen[k];
				k++;
			}
		}
		
		buf = ByteBuffer.allocate(2);
		buf.putChar((char)CRC16.calculate(discriptionPart));
		wCrc16 = buf.array();
		
		wholePackage[14]=wCrc16[0];
		wholePackage[15]=wCrc16[1];
		
		buf = ByteBuffer.allocate(4);
		buf.putInt(commandType);
		cType = buf.array();
		
		buf = ByteBuffer.allocate(4);
		buf.putInt(userId);
		bUserId = buf.array();
		
		for(int i=16, j=0, t=0, u=0, m=0; i<24+message.length; i++, j++){
			if(i<20){
				bMsq[j]=cType[t];
				wholePackage[i]=cType[t];
				t++;
			} else if(i<24) {
				bMsq[j]=bUserId[u];
				wholePackage[i]=bUserId[u];
				u++;
			} else {
				bMsq[j]=message[m];
				wholePackage[i]=message[m];
				m++;
			}
		}
		
		buf = ByteBuffer.allocate(2);
		buf.putChar((char)CRC16.calculate(bMsq));
		wCrc16Message = buf.array();
		
		wholePackage[message.length+24]=wCrc16Message[0];
		wholePackage[message.length+25]=wCrc16Message[1];
		
	}
	
	  public Key getKey() {
		return key;
	}

	public byte[] getWholePackage() {
		return wholePackage;
	}

	public byte[] encryptMessage( String message) {
		  try {
			  if(key==null) {
		      KeyGenerator keyGen = KeyGenerator.getInstance("DES");
		      SecureRandom secRandom = new SecureRandom();
		      keyGen.init(secRandom);
		      key = keyGen.generateKey();   
			  }  
			  cipher.init(Cipher.ENCRYPT_MODE, key);

			  byte[] plainText  = message.getBytes("UTF-8");
			  byte[] cipherText = cipher.doFinal(plainText);
			  return cipherText;

		      
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}  catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		  return null;
	  }
	
	public static String decryptMessage(byte[] mess) {
		  try {
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] cipherText = cipher.doFinal(mess);
			return new String(cipherText, "UTF-8");
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		  return null;
	      
	}
	
}
