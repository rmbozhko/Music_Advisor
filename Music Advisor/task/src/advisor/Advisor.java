package advisor;

import java.util.Scanner;

/**
 * Advisor class. Here is the interface for communicating with the user.
 */
public class Advisor {

    public void start() {

        Service service = new Service();
        Scanner scanner = new Scanner(System.in);
        String query = scanner.next();
        while (!query.equals("exit")) {
            switch (query) {
                case ("auth"):
                    service.authorize();
                    break;
                case ("new"):
                    System.out.print(service.getReleases());
                    break;
                case ("featured"):
                    System.out.print(service.getFeatured());
                    break;
                case ("categories"):
                    System.out.print(service.getCategories());
                    break;
                case ("playlists"):
                    System.out.print(service.getPlaylists(scanner.nextLine().trim()));
                    break;
            }
            query = scanner.next();
        }
        service.setAuthorised(false);
        System.out.println("---GOODBYE!---");
    }
}