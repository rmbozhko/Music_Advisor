package advisor;

public class Main {
    public static void main(String[] args) {
        if (args.length >= 2 && args[0].equals("-access")) {
            Authorisation.SERVER_PATH = args[1];
        }
        if (args.length >= 4 && args[2].equals("-resource")) {
            Authorisation.API_PATH = args[3];
        }
        Advisor advisor = new Advisor();
        advisor.start();
    }
}