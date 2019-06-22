package messaging.network;

import com.google.gson.Gson; 
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import messaging.exceptions.InvalidCharacteristicOfGoodsException;
import messaging.warehouse.Database;
import messaging.warehouse.Good;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.crypto.SecretKey;

public class ServerHttp {

	private static SecretKey key;
	private static final Database db = new Database();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private volatile static int unicNumber = 0;
	private static CopyOnWriteArraySet<String> tokens = new CopyOnWriteArraySet<String>();

	public static void main(String[] args) throws Exception {

		db.createUser("login", "password");
		db.createUser("Kate", "12345");

		key = MacProvider.generateKey(SignatureAlgorithm.HS256);

		// apiKey = "SecretKey".getBytes("UTF-8");
		HttpServer server = HttpServer.create();
		server.bind(new InetSocketAddress(8765), 0);

		HttpContext context = server.createContext("/api/login", new LoginHandler());

//		HttpContext context1 = server.createContext("/api/good", new ChangeGoodHandler());
//		context1.setAuthenticator(new Auth());

		HttpContext context2 = server.createContext("/api/good", new GoodHandler());
		context2.setAuthenticator(new Auth());

		server.setExecutor(null);
		server.start();
	}

	// Sample method to construct a JWT
	private static String createJWT(String id, String subject) {

		// The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		// Let's set the JWT Claims
		long expMillis = nowMillis + 1800000;
		Date exp = new Date(expMillis);

		return Jwts.builder().setId(id).setIssuedAt(now).setSubject(subject).setIssuer("server").setExpiration(exp)
				.signWith(signatureAlgorithm, key).compact();
	}

	// Sample method to validate and read the JWT
	private static void parseJWT(String jwt) {

		// This line will throw an exception if it is not a signed JWS (as expected)
		Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwt).getBody();
		System.out.println("ID: " + claims.getId());
		System.out.println("Subject: " + claims.getSubject());
		System.out.println("Issuer: " + claims.getIssuer());
		System.out.println("Expiration: " + claims.getExpiration());
	}

	// Sample method to validate and read the JWT
	private static boolean isAlive(String jwt) {

		// This line will throw an exception if it is not a signed JWS (as expected)
		if(tokens.contains(jwt)) {
			try {
				Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwt).getBody();
				long nowMillis = System.currentTimeMillis();
				Date now = new Date(nowMillis);
				if (claims.getExpiration().after(now))
					return true;
				else {
					tokens.remove(jwt);
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		} else return false;
	}

	static class LoginHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {

			if ("GET".equals(exchange.getRequestMethod())) {

				String p = exchange.getRequestURI().getRawQuery();
				System.out.println(p);
				Map<String, String> params = new HashMap<String, String>();

				for (String str : p.split("&")) {
					String[] pair = str.split("=");
					params.put(pair[0], pair[1]);
				}

				String login = "", password = "";
				if (params.containsKey("login"))
					login = params.get("login");
				if (params.containsKey("password"))
					password = params.get("password");
				System.out.println("login = " + login + " password = " + password);
				if (db.existUser(login, password)) {
					String token = createJWT(" " + unicNumber, login);
					unicNumber++;
					tokens.add(token);
					
					JsonObject jo = new JsonObject();
					jo.addProperty("token", token);
					String b = GSON.toJson((JsonElement) jo);
					byte[] body = b.getBytes("UTF-8");

					exchange.sendResponseHeaders(200, body.length);

					OutputStream os = exchange.getResponseBody();
					os.write(body);
					os.close();

				} else {
					exchange.sendResponseHeaders(401, -1);
				}

			} else {
				exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
			}
			exchange.close();

		}
	}

	static class GoodHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			StringBuilder builder = new StringBuilder();

			if (exchange.getRequestMethod().equals("POST")) {
				goodChange(exchange);
			} else if (exchange.getRequestMethod().equals("PUT")) {
				goodCreate(exchange);
			} else if (exchange.getRequestMethod().equals("DELETE")) {
				goodDelete(exchange);
			} else if (exchange.getRequestMethod().equals("GET")) {
				goodInfo(exchange);
			} else
				throw new UnsupportedOperationException();
			exchange.close();
		}

		private void goodInfo(HttpExchange exchange) throws IOException {

			String uri = exchange.getRequestURI().getRawPath();
			String[] uriParths = uri.split("/");
			String id = uriParths[uriParths.length - 1];

			int idOfGood = Integer.valueOf(id);
			Good good = db.getGoodById(idOfGood);
			if (good != null) {
				String b = GSON.toJson(good);
				byte[] body = b.getBytes("UTF-8");

				exchange.sendResponseHeaders(200, body.length);

				OutputStream os = exchange.getResponseBody();
				os.write(body);
				os.close();
			} else
				exchange.sendResponseHeaders(404, -1);

		}

		// delete
		private void goodDelete(HttpExchange exchange) throws IOException {
			String uri = exchange.getRequestURI().getRawPath();
			String[] uriParths = uri.split("/");
			String id = uriParths[uriParths.length - 1];

			int idOfGood = Integer.valueOf(id);

			if (db.deleteGoodById(idOfGood)) {
				exchange.sendResponseHeaders(204, -1);
			} else
				exchange.sendResponseHeaders(404, -1);
		}

		// put
		private void goodCreate(HttpExchange exchange) throws IOException {

			InputStream is = exchange.getRequestBody();

			try {
				Good goodToCreate = GSON.fromJson(new String(is.readAllBytes()), Good.class);
				if (db.createGoods(goodToCreate)) {

					int id = db.getGoodId(goodToCreate.getName());
					JsonObject jo = new JsonObject();
					jo.addProperty("id", id);
					String b = GSON.toJson((JsonElement) jo);
					byte[] body = b.getBytes("UTF-8");

					exchange.sendResponseHeaders(201, body.length);

					OutputStream os = exchange.getResponseBody();
					os.write(body);
					os.close();
				} else
					exchange.sendResponseHeaders(409, -1);

			} catch (JsonSyntaxException | InvalidCharacteristicOfGoodsException e) {
				exchange.sendResponseHeaders(409, -1);
			}
		}

		// post
		private void goodChange(HttpExchange exchange) throws IOException {
			InputStream is = exchange.getRequestBody();

			try {
				JsonObject jo = GSON.fromJson(new String(is.readAllBytes()), JsonObject.class);
				System.out.println("here");
				if(jo.has("id")) {
					int id = jo.get("id").getAsInt();
					Good g = db.getGoodById(id);
					
					if(g==null) exchange.sendResponseHeaders(404, -1);
					else {
						if(jo.has("price"))
							try {
								db.setPrice(id, jo.get("price").getAsDouble());
								if(jo.has("name"))  db.updateNameOfGood(id, jo.get("name").getAsString());
								if(jo.has("group"))  db.updateNameOfGroupInGood(id, jo.get("groupName").getAsString());
								if(jo.has("description"))  db.updateDescriptionOfGood(id, jo.get("description").getAsString());
								if(jo.has("produser"))  db.updateProducer(id, jo.get("produser").getAsString());
							} catch (InvalidCharacteristicOfGoodsException e) {
								exchange.sendResponseHeaders(409, -1);
							}
						
						if(jo.has("addGood"))  db.addGoodsById(id, jo.get("addGood").getAsInt());
						if(jo.has("removeGood"))
							try {
								db.removeGoodsById(id, jo.get("removeGood").getAsInt());
							} catch (InvalidCharacteristicOfGoodsException e) {
								exchange.sendResponseHeaders(409, -1);
							}
							
						exchange.sendResponseHeaders(204, -1);
					}
				} else 
					exchange.sendResponseHeaders(404, -1);
			} catch (JsonSyntaxException e) {
				exchange.sendResponseHeaders(409, -1);
			}
		}
	}



	static class Auth extends Authenticator {
		@Override
		public Result authenticate(HttpExchange httpExchange) {
			Headers head = httpExchange.getRequestHeaders();
			String token = head.getFirst("Authorization");
			if (token==null || token.length() <= 1) 
				return new Failure(403);
			if (isAlive(token))
				return new Success(new HttpPrincipal("c0nst", "realm"));
			else
				return new Failure(403);
		}
	}

}
