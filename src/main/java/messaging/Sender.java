package messaging;

import java.net.InetAddress;

import javax.crypto.BadPaddingException;

import messaging.exceptions.InjuredPackageException;

public class Sender {
	
	public synchronized void sendMessage(byte[] mess, InetAddress target) {
		PackageGetter pg;
		try {
			pg = new PackageGetter(mess);
			System.out.println("\nSending message:\n"+pg.toString());
		} catch (InjuredPackageException | BadPaddingException e) {
			e.printStackTrace();
		}
	}
}
