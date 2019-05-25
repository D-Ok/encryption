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

import messaging.exceptions.ArgumentException;
import messaging.exceptions.NoMessageException;

public class Package {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final String algoritm = "AES";
	private static Key key;
	private static Cipher cipher;
	
	private static long bPktIdLong=0;
	
	//descryption of the package
	private byte[] descriptionPart = new byte[14];
		private final byte bMagic= 0xD;
		private byte bSrc;
		private byte[] bPktId = new byte[8];
		private byte[] wLen = new byte[4];
	private byte[] wCrc16= new byte[2];
	
	//descryption of the message
	private byte[] bMsq;
		private byte[] cType = new byte[4];
		private byte[] bUserId = new byte[4];
		private byte[] message;	
	private byte[] wCrc16Message = new byte[2];
	
	private byte[] wholePackage;
	
	public Package( byte src, int commandType, int userId, JsonElement jsonMessage) 
			throws NoMessageException, ArgumentException, Exception {
		if(jsonMessage == null) throw new NullPointerException();
		String inputMessage = GSON.toJson(jsonMessage);
		if(src<0 || commandType<0 || userId<0) throw new ArgumentException();
		
		message = encryptMessage(inputMessage);
		if(message.length==0) throw new NoMessageException("Can`t send an empty string");
		
		bMsq = new byte[message.length+8];
		wholePackage = new byte[message.length+26];
		
		//byte with src
		bSrc = src;
		
		//augment message number
		bPktIdLong++;
		if(bPktIdLong<0) throw new Exception("Can`t create message any more");
		
		ByteBuffer buf = ByteBuffer.allocate(8);
		buf.putLong(bPktIdLong);
		bPktId = buf.array();
		
		//creating byte[] with length of message
		buf = ByteBuffer.allocate(4);
		buf.putInt(message.length+8);
		wLen = buf.array();
		
		//full array containing whole package and array that contains description of the package
		descriptionPart[0]=bMagic;
		wholePackage[0]=bMagic;
		descriptionPart[1]=bSrc;
		wholePackage[1]=bSrc;
		
		for (int i=2, j=0, k=0; i<14; i++){
			if(i<10) {
				descriptionPart[i]=bPktId[j];
				wholePackage[i]=bPktId[j];
				j++;
			}else {
				descriptionPart[i]=wLen[k];
				wholePackage[i]=wLen[k];
				k++;
			}
		}
		
		//calculate first crc16
		buf = ByteBuffer.allocate(2);
		buf.putChar((char)CRC16.calculate(descriptionPart));
		wCrc16 = buf.array();
		
		wholePackage[14]=wCrc16[0];
		wholePackage[15]=wCrc16[1];
		
		//creating byte[] with type of the command
		buf = ByteBuffer.allocate(4);
		buf.putInt(commandType);
		cType = buf.array();
		
		//creating byte[] with userId
		buf = ByteBuffer.allocate(4);
		buf.putInt(userId);
		bUserId = buf.array();
		
		//full array containing whole package and array that contains description of the message
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
		
		//calculate second crc16
		buf = ByteBuffer.allocate(2);
		buf.putChar((char)CRC16.calculate(bMsq));
		wCrc16Message = buf.array();
		
		wholePackage[message.length+24]=wCrc16Message[0];
		wholePackage[message.length+25]=wCrc16Message[1];
		
	}

	public byte[] getWholePackage() {
		return wholePackage;
	}

	public static synchronized byte[] encryptMessage( String message) {
		try {
			  if(cipher == null) initializeCipher();
			  if(key == null) initializeKey();
			  cipher.init(Cipher.ENCRYPT_MODE, key);

			  byte[] plainText  = message.getBytes("UTF-8");
			  byte[] cipherText = cipher.doFinal(plainText);
			  return cipherText;
		      
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		  return null;
	  }
	
	public static synchronized String decryptMessage(byte[] mess) {
		  try {
			if(cipher == null) initializeCipher();
			if(key == null) initializeKey();
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] cipherText = cipher.doFinal(mess);
			return new String(cipherText, "UTF-8");
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		  return null;
	      
	}
	
	private static void initializeCipher() {
		if(cipher==null) {
			try {
				 cipher = Cipher.getInstance(algoritm);
			} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void initializeKey() {
		if(key==null) {
			try {
		      KeyGenerator keyGen = KeyGenerator.getInstance(algoritm);
		      SecureRandom secRandom = new SecureRandom();
		      keyGen.init(secRandom);
		      key = keyGen.generateKey();   
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
	}
}
