package advisor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private boolean     loggedIn;
    private String      accessServerPoint;

    {
        this.loggedIn = false;
    }

    public Main() {
        this("https://accounts.spotify.com");
    }

    public Main(String accessServerPoint) {
        this.accessServerPoint = accessServerPoint;
    }

    private void printRequestedDataIfAuthorized(String s) {
        if (loggedIn) {
            System.out.println(s);
        } else {
            System.out.println("Please, provide access for application.");
        }
    }

    private void run() throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);

        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(8080), 0);
        HttpClient  client = HttpClient.newBuilder().build();
        server.createContext("/",
                exchange -> {
                    String          browserMessage;
                    String          query = exchange.getRequestURI().getQuery();
                    Map<String, String> queryParameters = new HashMap<>();

                    for (String pair : query.split("&")) {
                        for (int i = 0; i < 2; i++) {
                            String[] keyAndValue = pair.split("=");
                            queryParameters.put(keyAndValue[0], keyAndValue[1]);
                        }
                    }

                    if (queryParameters.containsKey("code")) {
                        browserMessage = "Got the code. Return back to your program.";
                        System.out.println("code received");
                        HttpRequest accessTokenRequest = HttpRequest.newBuilder()
                                                        .header("Content-Type",
                                                                "application/x-www-form-urlencoded")
                                                        .uri(URI.create(accessServerPoint + "/api/token"))
                                                        .POST(HttpRequest
                                                                .BodyPublishers
                                                                .ofString("grant_type=authorization_code&" +
                                                                                "client_id=5d5cd3cb699540bc9ab61367eb249279&" +
                                                                                "client_secret=2d58749371f949ba9b66ae411021d188&" +
                                                                                "redirect_uri=http://localhost:8080&" +
                                                                                "code=" + queryParameters.get("code")))
                                                        .build();
                        HttpResponse<String> access_token;
                        try {
                            access_token = client.send(accessTokenRequest, HttpResponse.BodyHandlers.ofString());
                            System.out.println(access_token.body());
                            System.out.println("---SUCCESS---");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (queryParameters.containsKey("error")) {
                        browserMessage = "Authorization code not found. Try again.";
                        System.out.println("access denied");
                    } else {
                        browserMessage = "Unexpected parameters returned";
                        System.out.println(browserMessage);
                    }
                    exchange.sendResponseHeaders(200, browserMessage.length());
                    exchange.getResponseBody().write(browserMessage.getBytes());
                    exchange.getResponseBody().close();
                    server.stop(1);
                }
        );

        while (true) {
            String userRequest = scanner.nextLine();
            switch (userRequest) {
                case "auth":
                    server.start();
                    String userAuthRequest = accessServerPoint + "/authorize?" +
                                            "client_id=5d5cd3cb699540bc9ab61367eb249279&" +
                                            "redirect_uri=http://localhost:8080&response_type=code";
                    System.out.println("use this link to request the access code:");
                    System.out.println(userAuthRequest);
                    System.out.println("waiting for code...");
                    HttpRequest getCodeRequest = HttpRequest.newBuilder().uri(URI.create(userAuthRequest)).GET().build();
                    client.send(getCodeRequest, HttpResponse.BodyHandlers.ofString());
                    Thread.sleep(2000);
                    loggedIn = true;
                    break;
                case "featured":
                    printRequestedDataIfAuthorized("---FEATURED---\n" +
                            "Mellow Morning\n" +
                            "Wake Up and Smell the Coffee\n" +
                            "Monday Motivation\n" +
                            "Songs to Sing in the Shower");
                    break;
                case "new":
                    printRequestedDataIfAuthorized("---NEW RELEASES---\n" +
                            "Mountains [Sia, Diplo, Labrinth]\n" +
                            "Runaway [Lil Peep]\n" +
                            "The Greatest Show [Panic! At The Disco]\n" +
                            "All Out Life [Slipknot]");
                    break;
                case "categories":
                    printRequestedDataIfAuthorized("---CATEGORIES---\n" +
                            "Top Lists\n" +
                            "Pop\n" +
                            "Mood\n" +
                            "Latin");
                    break;
                case "playlists Mood":
                    printRequestedDataIfAuthorized("---MOOD PLAYLISTS---\n" +
                            "Walk Like A Badass  \n" +
                            "Rage Beats  \n" +
                            "Arab Mood Booster  \n" +
                            "Sunday Stroll");
                    break;
                case "exit":
                    System.out.println("---GOODBYE!---");
//                    server.stop(1);
                    return;
                default:
                    break;
            }
        }
    }

    public static void main(String[] args) {
        try {
            if (args.length == 2) {
                new Main(args[1]).run();
            } else {
                new Main().run();
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

/*abstract class Request {
    abstract void    makeRequest();
}

class featuredRequest extends Request {

    @Override
    void makeRequest() {}
}

abstract class RequestAuthDecorator extends Request {
    Request request;

    public RequestAuthDecorator(Request request) {
        this.request = request;
    }

    void makeRequest() {
        request.makeRequest();
    }

    public void setRequest(Request request) {
        this.request = request;
    }
}

class Authentificator extends RequestAuthDecorator {
    boolean     isLoggedIn = false;

    public Authentificator(Request request) {
        super(request);
    }

    @Override
    void        makeRequest() {
        if (isLoggedIn) {
            super.makeRequest();
        } else {
            System.out.println("Please, provide access for application.");
        }
    }

    void        authentificate() {
        isLoggedIn = true;
    }
}*/