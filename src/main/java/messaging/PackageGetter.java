package messaging;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import messaging.exceptions.InjuredPackageException;

public class PackageGetter {
	
	private byte bMagic;
	private byte bSrc;
	private long bPktId;
	private int wLen;
	private int cType;
	private int bUserId;
	private String message;
	
	public PackageGetter(byte[] wholeMessage) throws InjuredPackageException {
		PackageChecker check =new PackageChecker(wholeMessage);
		if(!check.isCorrect()) throw new InjuredPackageException();
//		byte[] arr = { 0x00, 0x01 };
//		ByteBuffer wrapped = ByteBuffer.wrap(arr); // big-endian by default
//		short num = wrapped.getShort(); // 1
		
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
		message = PackageCreator.decryptMessage(mess);   
	     
		  
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

	public String getMessage() {
		return message;
	}
}
