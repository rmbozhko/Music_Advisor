package advisor;

import java.util.Scanner;

/**
 * Controller class. Here is the interface for communicating with the user.
 */
public class Controller {
    ModelService    service;
    View            view;

    public Controller(ModelService modelService, View view) {
        service = modelService;
        this.view = view;
    }

    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        String query = scanner.next();
        while (!query.equals("exit")) {
            switch (query) {
                case ("auth"):
                    service.authorize();
                    break;
                case ("new"):
                    view.printPaginated(service.getReleases());
                    break;
                case ("featured"):
                    view.printPaginated(service.getFeatured());
                    break;
                case ("categories"):
                    view.printPaginated(service.getCategories());
                    break;
                case ("playlists"):
                    view.printPaginated(service.getPlaylists(scanner.nextLine().trim()));
                    break;
                case ("next"):
                    view.printNextPaginated();
                    break;
                case ("prev"):
                    view.printPrevPaginated();
                    break;
            }
            query = scanner.next();
        }
        service.setAuthorised(false);
        System.out.println("---GOODBYE!---");
    }
}