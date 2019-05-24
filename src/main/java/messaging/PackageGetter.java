package messaging;

import java.nio.ByteBuffer;

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
	
	public PackageGetter(byte[] wholeMessage) throws InjuredPackageException{
		if(wholeMessage == null) throw new NullPointerException();
		PackageChecker check =new PackageChecker(wholeMessage);
		if(!check.isCorrect()) throw new InjuredPackageException();
		
		bMagic=wholeMessage[0];
		bSrc=wholeMessage[1];
		
		byte[] pktId = new byte[8];
		byte[] len= new byte[4];
		byte[] type= new byte[4];
		byte[] userId= new byte[4];
		byte[] mess = null;
		
		for(int i=2, p=0, l=0, t=0, u=0, m=0; i<wholeMessage.length; i++){
			if(i<10) {
				pktId[p]=wholeMessage[i];
				p++;
				if(p==8) bPktId=ByteBuffer.wrap(pktId).getLong();
			} else if(i<14) {
				len[l]=wholeMessage[i];
				l++;
				if(l==4) {
					wLen=ByteBuffer.wrap(len).getInt();
					mess = new byte[wLen-8];
				}
			} else if(i>15 && i<20) {
				type[t]=wholeMessage[i];
				t++;
				if(t==4) cType=ByteBuffer.wrap(type).getInt();
			} else if(i>19 && i<24) {
				userId[u]=wholeMessage[i];
				u++;
				if(u==4) bUserId=ByteBuffer.wrap(userId).getInt();
			}  else if(i>23 && i<wholeMessage.length-2) {
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
