package messaging;

import java.nio.ByteBuffer;

import javax.crypto.BadPaddingException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import messaging.exceptions.InjuredPackageException;

public class PackageGetter {
	
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	private byte bMagic;
	private byte bSrc;
	private long bPktId;
	private int wLen;
	private int cType;
	private int bUserId;
	private String messageString;
	private JsonElement messageJson;
	
	public PackageGetter(byte[] wholeMessage) throws InjuredPackageException, BadPaddingException{
		if(wholeMessage == null) throw new NullPointerException();
		if(wholeMessage.length<18) throw new InjuredPackageException();
		byte[] len= new byte[4];
		for(int i=10, l=0; i<14; i++, l++)  
			len[l]=wholeMessage[i];
		wLen=ByteBuffer.wrap(len).getInt();
		
		PackageChecker check =new PackageChecker(wholeMessage, wLen+18);
		if(!check.isCorrect()) throw new InjuredPackageException();
		
		bMagic=wholeMessage[0];
		bSrc=wholeMessage[1];
		
		byte[] pktId = new byte[8];
		byte[] type= new byte[4];
		byte[] userId= new byte[4];
		byte[] mess = new byte[wLen-8];
		
		for(int i=2, p=0, t=0, u=0, m=0; i<wLen+18; i++){
			if(i<10) {
				pktId[p]=wholeMessage[i];
				p++;
				if(p==8) bPktId=ByteBuffer.wrap(pktId).getLong();
			}  else if(i>15 && i<20) {
				type[t]=wholeMessage[i];
				t++;
				if(t==4) cType=ByteBuffer.wrap(type).getInt();
			} else if(i>19 && i<24) {
				userId[u]=wholeMessage[i];
				u++;
				if(u==4) bUserId=ByteBuffer.wrap(userId).getInt();
			}  else if(i>23 && i<16+wLen) {
				mess[m]=wholeMessage[i];
				m++;
			}
			 
		}
		messageString = Package.decryptMessage(mess);   
	    messageJson = GSON.fromJson(messageString, JsonElement.class );
		  
	}

	public byte getbMagic() {
		return bMagic;
	}

	public byte getbSrc() {
		return bSrc;
	}

	public long getbPktId() {
		return bPktId;
	}

	public int getwLen() {
		return wLen;
	}

	public int getcType() {
		return cType;
	}

	public int getbUserId() {
		return bUserId;
	}

	public JsonElement getMessageJson() {
		return messageJson;
	}
	
	public String getMessageString() {
		return messageString;
	}
	
	public String toString() {
		return new String("Description of the package: \nbegining of package = "+bMagic+"\nsrc = "+bSrc+"\nnumber of the message = "+bPktId
				+"\nlength of the message = "+wLen+"\ntype of the commad = "+cType+"\nuserId = "+bUserId+"\nmessage = "+messageString);
	}
}
