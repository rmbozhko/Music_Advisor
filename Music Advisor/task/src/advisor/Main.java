package advisor;

/**
 * Program entry point.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length >= 2 && args[0].equals("-access")) {
            ModelAuthorisation.SERVER_PATH = args[1];
        }
        if (args.length >= 4 && args[2].equals("-resource")) {
            ModelAuthorisation.API_PATH = args[3];
        }
        if (args.length >= 6 && args[4].equals("-page")) {
            View.ENTRIES_PER_PAGE = Integer.parseInt(args[5]);
        }
        Controller controller = new Controller(new ModelService(new ModelAuthorisation()),
                                                new View());
        controller.runApp();
    }
}