package messaging.network;

import java.io.IOException;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import messaging.warehouse.Good;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class ClientHttp {

	OkHttpClient client;
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public ClientHttp() {
		client = new OkHttpClient.Builder().retryOnConnectionFailure(true).build();
	}

	public String login() throws IOException {

		Request request = new Request.Builder()
				// .addHeader("Connection","close")
				.url("http://localhost:8765/api/login?login=login&password=password").build();

		Response response = client.newCall(request).execute();
		System.out.println(response.code() + " " + response.message());

		JsonObject jo = (JsonObject) GSON.fromJson(response.body().string(), JsonElement.class);
		String token = "";
		if (jo.has("token"))
			token = jo.get("token").getAsString();
		System.out.println("token = " + token);

		return token;
	}

	public Good getGood(int id, String token) throws IOException {
		Request request = new Request.Builder()
				// .addHeader("Connection","close")
				.get().addHeader("Authorization", token).url("http://localhost:8765/api/good/" + id).build();

		Response response = client.newCall(request).execute();
		int responseCode = response.code();
		System.out.println(responseCode + " " + response.message());

		if (responseCode == 200) {
			Good good = GSON.fromJson(response.body().string(), Good.class);
			System.out.println(good.toString());
			return good;
		} else
			return null;

	}

	public boolean deleteGood(int id, String token) throws IOException {
		Request request = new Request.Builder()
				// .addHeader("Connection","close")
				.addHeader("Authorization", token).delete().url("http://localhost:8765/api/good/" + id).build();

		Response response = client.newCall(request).execute();
		int responseCode = response.code();
		System.out.println(responseCode + " " + response.message());

		if (responseCode == 204) {
			Good good = GSON.fromJson(response.body().string(), Good.class);
			System.out.println("deleted");
			return true;
		} else
			return false;

	}

	public int createGood(Good g, String token) throws IOException {
		Request request = new Request.Builder()
				// .addHeader("Connection","close")
				.addHeader("Authorization", token)
				.url("http://localhost:8765/api/good")
				.put(RequestBody.create(JSON, GSON.toJson(g)))
				.build();

		Response response = client.newCall(request).execute();
		int responseCode = response.code();
		System.out.println(responseCode + " " + response.message());

		if (responseCode == 201) {
			JsonObject jo = (JsonObject) GSON.fromJson(response.body().string(), JsonElement.class);
			int id = -1;
			if (jo.has("id"))
				id = jo.get("id").getAsInt();
			System.out.println("created id = " + id);

			return id;
		} else
			return -1;

	}

	public boolean change(String token, int id, double newPrice, String newName, String newGroupName,
			String newDescription, String newProduser) throws IOException {
		JsonObject jo = new JsonObject();
		jo.addProperty("id", id);
		if (newName != null)
			jo.addProperty("name", newName);
		if (newGroupName != null)
			jo.addProperty("group", newGroupName);
		if (newDescription != null)
			jo.addProperty("description", newDescription);
		if (newProduser != null)
			jo.addProperty("produser", newProduser);
		jo.addProperty("price", newPrice);

		RequestBody body = RequestBody.create(JSON, GSON.toJson((JsonElement) jo));
		Request request = new Request.Builder()
				.addHeader("Authorization", token)
				.url("http://localhost:8765/api/good")
				.post(body)
				.build();

		Response response = client.newCall(request).execute();
		int responseCode = response.code();
		System.out.println(responseCode + " " + response.message());

		if (responseCode == 204) {
			System.out.println("changed");
			return true;
		} else
			return false;
	}

	public boolean change(String token, int id, String newName, String newGroupName, String newDescription, String newProduser) throws IOException {
		JsonObject jo = new JsonObject();
		jo.addProperty("id", id);
		if (newName != null)
			jo.addProperty("name", newName);
		if (newGroupName != null)
			jo.addProperty("group", newGroupName);
		if (newDescription != null)
			jo.addProperty("description", newDescription);
		if (newProduser != null)
			jo.addProperty("produser", newProduser);

		RequestBody body = RequestBody.create(JSON, GSON.toJson((JsonElement) jo));
		Request request = new Request.Builder().addHeader("Authorization", token).url("http://localhost:8765/api/good").post(body).build();

		Response response = client.newCall(request).execute();
		int responseCode = response.code();
		System.out.println(responseCode + " " + response.message());

		if (responseCode == 204) {
			System.out.println("changed");
			return true;
		} else
			return false;
	}
	
	public boolean addGood(String token, int id, int quantity) throws IOException {
		JsonObject jo = new JsonObject();
		jo.addProperty("id", id);
		jo.addProperty("addGood", quantity);

		RequestBody body = RequestBody.create(JSON, GSON.toJson((JsonElement) jo));
		Request request = new Request.Builder().addHeader("Authorization", token).url("http://localhost:8765/api/good").post(body).build();

		Response response = client.newCall(request).execute();
		int responseCode = response.code();
		System.out.println(responseCode + " " + response.message());

		if (responseCode == 204) {
			System.out.println("changed");
			return true;
		} else
			return false;
	}
	
	public boolean removeGood(String token, int id, int quantity) throws IOException {
		JsonObject jo = new JsonObject();
		jo.addProperty("id", id);
		jo.addProperty("removeGood", quantity);

		RequestBody body = RequestBody.create(JSON, GSON.toJson((JsonElement) jo));
		Request request = new Request.Builder().addHeader("Authorization", token).url("http://localhost:8765/api/good").post(body).build();

		Response response = client.newCall(request).execute();
		int responseCode = response.code();
		System.out.println(responseCode + " " + response.message());

		if (responseCode == 204) {
			System.out.println("changed");
			return true;
		} else
			return false;
	}

	static public void main(String args[]) throws Exception {

		ClientHttp cli = new ClientHttp();
		String token = cli.login();
		cli.getGood(3, token);
		cli.deleteGood(56, token);

		Good g = new Good("name", "descr", "client", "dairy", 20, 100);

		cli.createGood(g, token);
		
		cli.change(token, 57, 20, null, null, null, null);
//		
//		InetAddress addr = InetAddress.getByName(null);
//		String host = addr.getHostAddress();
//		int port = 8765;
//		int numThreads = 1;
//
//		new ClientHttp(host, port, numThreads);
	}
}
