package messaging;

import java.nio.ByteBuffer;

public class PackageChecker {
  private boolean correctDescription;
  private boolean correctContent;
  
	public PackageChecker(byte[] message) {
		if(message == null) throw new NullPointerException();
		
		byte[] firstCheck = new byte[14];
		byte[] secondCheck = new byte[message.length-4-14];
		byte[] firstCrc=new byte[2];
		byte[] secondCrc=new byte[2];
		
		for(int i=0, j=0, k=0; i<message.length; i++)
		{
			if(i<14) {
				firstCheck[j]=message[i];
				j++;
			}else if(i<16){
				if(i==14) firstCrc[0]=message[i];
				else firstCrc[1]=message[i];
			}else if(i<message.length-2){
				secondCheck[k]=message[i];
				k++;
			}else {
				if(i==message.length-2) secondCrc[0]=message[i];
				else secondCrc[1]=message[i];
			}
		}
		
		correctDescription = CRC16.calculate(firstCheck)==ByteBuffer.wrap(firstCrc).getChar();
		correctContent = CRC16.calculate(secondCheck)==ByteBuffer.wrap(secondCrc).getChar();		
	}

	public boolean isCorrectDescription() {
		return correctDescription;
	}

	public boolean isCorrectContent() {
		return correctContent;
	}
	
	public boolean isCorrect(){
		return correctContent && correctDescription;
	}
}
