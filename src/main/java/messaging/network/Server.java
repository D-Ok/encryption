package messaging.network;

import java.util.concurrent.ConcurrentHashMap;

public interface Server {

	static ConcurrentHashMap<Integer, byte[]> answers = new ConcurrentHashMap<Integer, byte[]>();

	public static void setAnswer(int unicNumber, byte[] answer) {
		answers.put(unicNumber, answer);
	}
}
