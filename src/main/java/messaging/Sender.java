package messaging;

import java.net.InetAddress;

import messaging.exceptions.InjuredPackageException;

public class Sender {
	
	public void sendMessage(byte[] mess, InetAddress target) {
		PackageGetter pg;
		try {
			pg = new PackageGetter(mess);
			System.out.println("Sending message:\n"+pg.toString());
		} catch (InjuredPackageException e) {
			e.printStackTrace();
		}
	}
}
