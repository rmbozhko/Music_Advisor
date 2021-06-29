package advisor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * View class used for user informing.
 */
public class View {
    public static int               ENTRIES_PER_PAGE = 5;
    private int                     currentPage = 0;
    private List<List<String>>      pages;

    public void printPaginated(String data) {
        pages = paginateOutput(data);
        printPaginatedPage(currentPage);
    }

    private List<List<String>> paginateOutput(String data) {
        List<List<String>> pages = new ArrayList<>();
        String[] splittedLines = data.split("\n\n");
        for (int i = 0; i < splittedLines.length; i += ENTRIES_PER_PAGE) {
            List<String> page = new ArrayList<>(Arrays.asList(splittedLines).subList(i, ENTRIES_PER_PAGE + i));
            pages.add(page);
        }
        return pages;
    }

    public void printNextPaginated() {
        if (currentPage + 1 >= pages.size()) {
            System.out.println("No more pages.");
        } else {
            currentPage++;
            printPaginatedPage(currentPage);
        }
    }

    public void printPrevPaginated() {
        if (currentPage - 1 < 0) {
            System.out.println("No more pages.");
        } else {
            currentPage--;
            printPaginatedPage(currentPage);
        }
    }

    private void printPaginatedPage(int currentPage) {
        for (String content : pages.get(currentPage)) {
            System.out.println(content);
        }
        System.out.printf("\n---PAGE %d OF %d---\n", currentPage + 1, pages.size());
    }
}
