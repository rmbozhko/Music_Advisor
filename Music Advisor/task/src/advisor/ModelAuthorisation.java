package advisor;

import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;

import java.net.http.HttpResponse;
import java.net.InetSocketAddress;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.io.IOException;
import java.net.URI;


/**
 * The AuthorisationModel class is used to establish connection with Spotify API, retrieve access token
 * and run local server for redirection.
 */
public class ModelAuthorisation {
    public static String SERVER_PATH = "https://accounts.spotify.com";
    public static String API_PATH = "https://api.spotify.com";
    public static String REDIRECT_URI = "http://localhost:8080";
    public static String CLIENT_ID = "5d5cd3cb699540bc9ab61367eb249279";
    public static String CLIENT_SECRET = "2d58749371f949ba9b66ae411021d188";
    public static String ACCESS_TOKEN = "";
    public static String ACCESS_CODE = "";

    /**
     * Getting access_code
     */
    public void getAccessCode() {
        //Creating a line to go to in the browser
            String uri = SERVER_PATH + "/authorize"
                + "?client_id=" + CLIENT_ID
                + "&redirect_uri=" + REDIRECT_URI
                + "&response_type=code";
        System.out.println("use this link to request the access code:");
        System.out.println(uri);

        //Creating a server and listening to the request.
        try {
            HttpServer server = HttpServer.create();
            server.bind(new InetSocketAddress(8080), 0);
            server.start();
            server.createContext("/",
                    exchange -> {
                        String query = exchange.getRequestURI().getQuery();
                        String request;
                        if (query != null && query.contains("code")) {
                            ACCESS_CODE = query.substring(5);
                            System.out.println("code received");
                            System.out.println(ACCESS_CODE);
                            request = "Got the code. Return back to your program.";
                        } else {
                            request = "Authorization code not found. Try again.";
                        }
                        exchange.sendResponseHeaders(200, request.length());
                        exchange.getResponseBody().write(request.getBytes());
                        exchange.getResponseBody().close();
                    });

            System.out.println("waiting for code...");
            while (ACCESS_CODE.length() == 0) {
                Thread.sleep(100);
            }
            server.stop(5);

        } catch (IOException e) {
            System.out.println("Server error");
        } catch (InterruptedException e) {
            System.out.println("Thread error");
        }
    }

    /**
     * Getting access_token based on access_code
     */
    public void getAccessToken() {

        System.out.println("making http request for access_token...");
        System.out.println("response:");

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(SERVER_PATH + "/api/token"))
                .POST(HttpRequest.BodyPublishers.ofString(
                        "grant_type=authorization_code"
                                + "&code=" + ACCESS_CODE
                                + "&client_id=" + CLIENT_ID
                                + "&client_secret=" + CLIENT_SECRET
                                + "&redirect_uri=" + REDIRECT_URI))
                .build();

        try {

            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assert response != null;
            System.out.println(response.body());
            ModelAuthorisation.ACCESS_TOKEN = JsonParser.parseString(response.body())
                                                    .getAsJsonObject()
                                                    .get("access_token")
                                                    .getAsString();
            System.out.println("Success!");

        } catch (InterruptedException | IOException e) {
            System.out.println("Error response");
        }
    }
}