package messaging.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

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

public class ClientHttp implements Runnable {

	private final OkHttpClient client;
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Random random = new Random();
	private final String login, password;
	private static String [] groups = {"groats", "dairy"};
	
	public ClientHttp(String login, String password) {
		client = new OkHttpClient.Builder().retryOnConnectionFailure(true).build();
		this.login=login;
		this.password=password;
	}
	
	public void startWork() {
		String token;
		try {
			token = login(login, password);
			if(token!=null && token.length()>0) {
				while(true)
				{
					int type = random.nextInt(6);
					switch(type) {
					case 0: 
						getGood(random.nextInt(501), token);
						break;
					case 1:
						deleteGood(random.nextInt(501), token);
						break;
					case 2:
						Good g = new Good("name"+random.nextInt(100), "description", "tester" , groups[random.nextInt(2)], random.nextDouble()*100, random.nextInt(1000));
						createGood(g, token);
						break;
					case 3:
						change(token, random.nextInt(500), random.nextDouble()*100, null, null, null, null);
						break;
					case 4:
						addGood(token,  random.nextInt(500),  random.nextInt(1000));
						break;
					case 5:
						removeGood(token, random.nextInt(500),  random.nextInt(1000));
						break;
					default:
						break;
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String login(String login, String password) throws IOException {

		System.out.println("\n"+Thread.currentThread()+"try to login login = "+login);
		String MD5Password = ServerHttp.getMD5EncryptedValue(password);
		Request request = new Request.Builder()
				.url("http://localhost:8765/api/login?login="+login+"&password="+MD5Password)
				.build();

		Response response = client.newCall(request).execute();
		int responseCode = response.code();
		System.out.println(Thread.currentThread()+" answer on login: "+responseCode + " " + response.message());

		
		String token = "";
		if (responseCode == 200) {
			JsonObject jo = (JsonObject) GSON.fromJson(response.body().string(), JsonElement.class);
			if (jo.has("token")) {
				token = jo.get("token").getAsString();
				System.out.println(Thread.currentThread()+" token = " + token);
			}

		}
		return token;
	}

	public Good getGood(int id, String token) throws IOException {
		System.out.println("\n"+Thread.currentThread()+"try to get good with id = "+id);
		
		Request request = new Request.Builder()
				.get()
				.addHeader("Authorization", token)
				.url("http://localhost:8765/api/good/" + id)
				.build();

		Response response = client.newCall(request).execute();
		int responseCode = response.code();
		System.out.println(Thread.currentThread()+" answer on get good with id "+id+" : "+responseCode + " " + response.message());

		if (responseCode == 200) {
			Good good = GSON.fromJson(response.body().string(), Good.class);
			System.out.println(Thread.currentThread()+" good with id "+id+" : "+good.toString());
			return good;
		} else
			return null;

	}

	public boolean deleteGood(int id, String token) throws IOException {
		System.out.println("\n"+Thread.currentThread()+"try to delete good with id = "+id);
		
		Request request = new Request.Builder()
				// .addHeader("Connection","close")
				.addHeader("Authorization", token).delete().url("http://localhost:8765/api/good/" + id).build();

		Response response = client.newCall(request).execute();
		int responseCode = response.code();
		System.out.println(Thread.currentThread()+" answer on deleting good with id "+id+" : "+responseCode + " " + response.message());

		if (responseCode == 204) {
			Good good = GSON.fromJson(response.body().string(), Good.class);
			System.out.println("deleted");
			return true;
		} else
			return false;

	}

	public int createGood(Good g, String token) throws IOException {
		System.out.println("\n"+Thread.currentThread()+"try to create good with id ");
		
		Request request = new Request.Builder()
				// .addHeader("Connection","close")
				.addHeader("Authorization", token)
				.url("http://localhost:8765/api/good")
				.put(RequestBody.create(JSON, GSON.toJson(g)))
				.build();

		Response response = client.newCall(request).execute();
		int responseCode = response.code();
		System.out.println(Thread.currentThread()+" answer on creating good : "+responseCode + " " + response.message());

		if (responseCode == 201) {
			JsonObject jo = (JsonObject) GSON.fromJson(response.body().string(), JsonElement.class);
			int id = -1;
			if (jo.has("id"))
				id = jo.get("id").getAsInt();
			System.out.println(Thread.currentThread()+" created id = " + id);

			return id;
		} else
			return -1;

	}

	public boolean change(String token, int id, double newPrice, String newName, String newGroupName,
			String newDescription, String newProduser) throws IOException {
		System.out.println("\n"+Thread.currentThread()+"try to change good with id = "+id);
		
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
		return sendPostRequest(body, id, token);
		
	}

	public boolean change(String token, int id, String newName, String newGroupName, String newDescription, String newProduser) throws IOException {
		System.out.println("\n"+Thread.currentThread()+"try to change good with id = "+id);
		
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
		return sendPostRequest(body, id, token);
	}
	
	public boolean addGood(String token, int id, int quantity) throws IOException {
		System.out.println("\n"+Thread.currentThread()+"try to add "+quantity+" goods with id = "+id);
		
		JsonObject jo = new JsonObject();
		jo.addProperty("id", id);
		jo.addProperty("addGood", quantity);

		RequestBody body = RequestBody.create(JSON, GSON.toJson((JsonElement) jo));
		return sendPostRequest(body, id, token);
	}
	
	public boolean removeGood(String token, int id, int quantity) throws IOException {
		System.out.println("\n"+Thread.currentThread()+"try to remove "+quantity+" goods with id = "+id);
		
		JsonObject jo = new JsonObject();
		jo.addProperty("id", id);
		jo.addProperty("removeGood", quantity);

		RequestBody body = RequestBody.create(JSON, GSON.toJson((JsonElement) jo));
		return sendPostRequest(body, id, token);
	}
	
	private boolean sendPostRequest(RequestBody body, int id, String token) throws IOException {
		Request request = new Request.Builder()
				.addHeader("Authorization", token)
				.url("http://localhost:8765/api/good")
				.post(body)
				.build();

		Response response = client.newCall(request).execute();
		int responseCode = response.code();
		System.out.println(Thread.currentThread()+" answer on changing good with id "+id+" : "+responseCode + " " + response.message());

		if (responseCode == 204) 
			return true;
		else
			return false;
	}
	
	

	static public void main(String args[]) throws Exception {
		 HashMap<String, String> users = new HashMap<String, String>();
        users.put("login", "password");
        users.put("Kate","12345");
        
		for(int i=0; i<10; i++) {
			Thread cli;
			
			if(i%3==0) 
				cli = new Thread(new ClientHttp("login", "wrong"));
			else if(i%3 == 1) 
				cli = new Thread(new ClientHttp("login", users.get("login")));
			else 
				cli = new Thread(new ClientHttp("Kate", users.get("Kate")));
			
			cli.start();
		}
        
	}
	
	

	@Override
	public void run() {
		startWork();
	}
}
