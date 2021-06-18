package advisor;

import java.util.Scanner;

public class Main {
    private boolean     loggedIn = false;

    private void printRequestedDataIfAuthorized(String s) {
        if (loggedIn) {
            System.out.println(s);
        } else {
            System.out.println("Please, provide access for application.");
        }
    }

    private void run() {
        Scanner scanner = new Scanner(System.in);
        String userAuthRequest = "https://accounts.spotify.com/authorize?" +
                                "client_id=5d5cd3cb699540bc9ab61367eb249279&" +
                                "redirect_uri=http://localhost:8080&response_type=code";
        Request request = new Authentificator(new featuredRequest());
        while (true) {
            String userRequest = scanner.nextLine();
            switch (userRequest) {
                case "auth":
                    System.out.println(userAuthRequest + "\n---SUCCESS---");
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
                    return;
                default:
                    break;
            }
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }
}

abstract class Request {
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
}