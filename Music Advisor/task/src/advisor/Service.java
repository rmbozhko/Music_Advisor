package advisor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The engine of the advisor. Here are all the methods of work.
 */
public class Service {
    private boolean isAuthorised = false;

    public boolean isAuthorised() {
        return this.isAuthorised;
    }

    public void setAuthorised(boolean authorised) {
        this.isAuthorised = authorised;
    }

    public void authorize() {
        Authorisation authorisation = new Authorisation();
        authorisation.getAccessCode();
        authorisation.getAccessToken();
        this.isAuthorised = true;
    }

    /**
     * Returns response data if authorised
     * @param service - a String containing response information
     * @return response data or error message if not authorised
     */
    private String getServicesIfAuthorised(String service) {
        if (this.isAuthorised()) {
            return service;
        } else {
            return "Please, provide access for application.\n";
        }
    }

    public String getReleases() {
        Request request = new Request() {

            { this.uriPath = "/v1/browse/new-releases"; }

            @Override
            protected String extractDataFromJSON(String jsonData) {
                StringBuilder builder = new StringBuilder();
                JsonArray albumsData = JsonParser.parseString(jsonData)
                                        .getAsJsonObject()
                                        .getAsJsonObject("albums")
                                        .getAsJsonArray("items");

                for (JsonElement album : albumsData) {
                    JsonObject albumData = album.getAsJsonObject();
                    builder.append(albumData.get("name").getAsString()).append("\n[");
                    List<String> artistNames = new ArrayList<>();
                    for (JsonElement artist : albumData.getAsJsonArray("artists")) {
                        JsonObject artistData = artist.getAsJsonObject();
                        artistNames.add(artistData.get("name").getAsString());
                    }
                    builder.append(String.join(", ", artistNames));
                    builder.append("]\n")
                            .append(albumData.getAsJsonObject("external_urls")
                                    .get("spotify")
                                    .getAsString())
                            .append("\n\n");
                }
                return builder.toString();
            }
        };
        return getServicesIfAuthorised(request.getData());
    }

    public String getFeatured() {
        Request request = new Request() {
            { this.uriPath = "/v1/browse/featured-playlists"; }

            @Override
            protected String extractDataFromJSON(String jsonData) {
                StringBuilder builder = new StringBuilder();
                JsonArray playlistsData = JsonParser.parseString(jsonData)
                        .getAsJsonObject()
                        .getAsJsonObject("playlists")
                        .getAsJsonArray("items");

                for (JsonElement playlist : playlistsData) {
                    JsonObject playlistData = playlist.getAsJsonObject();
                    builder.append(playlistData.get("name").getAsString())
                            .append("\n")
                            .append(playlistData.getAsJsonObject("external_urls")
                                                .get("spotify")
                                                .getAsString())
                            .append("\n\n");
                }
                return builder.toString();
            }
        };
        return getServicesIfAuthorised(request.getData());
    }

    public String getCategories() {
        Request request = new Request() {
            { this.uriPath = "/v1/browse/categories"; }

            @Override
            protected String extractDataFromJSON(String jsonData) {
                StringBuilder builder = new StringBuilder();
                JsonArray categoriesData = JsonParser.parseString(jsonData)
                        .getAsJsonObject()
                        .getAsJsonObject("categories")
                        .getAsJsonArray("items");

                for (JsonElement category : categoriesData) {
                    JsonObject categoryData = category.getAsJsonObject();
                    builder.append(categoryData.get("name").getAsString()).append("\n");
                }
                return builder.toString();
            }
        };
        return getServicesIfAuthorised(request.getData());
    }

    public String getPlaylists(String categoryId) {
        Request request = new Request() {
            { this.uriPath = "/v1/browse/categories"; }

            @Override
            protected String extractDataFromJSON(String jsonData) {
                StringBuilder builder = new StringBuilder();
                JsonObject categoriesData = JsonParser.parseString(jsonData).getAsJsonObject();
                if (categoriesData.has("error")) {
                    return categoriesData.getAsJsonObject("error").get("message").getAsString();
                } else {
                    for (JsonElement playlist : categoriesData.getAsJsonObject("playlists")
                                                                .getAsJsonArray("items")) {
                        JsonObject playlistData = playlist.getAsJsonObject();
                        builder.append(playlistData.get("name").getAsString())
                                .append("\n")
                                .append(playlistData.getAsJsonObject("external_urls")
                                        .get("spotify")
                                        .getAsString())
                                .append("\n\n");
                    }
                    return builder.toString();
                }
            }

            @Override
            public String   getData() {
                HashMap<String, String> categoriesIds = extractCategoriesDataFromJSON(makeRequestToAPI());
                this.uriPath = "/v1/browse/categories/" + categoriesIds.get(categoryId) + "/playlists";
                return extractDataFromJSON(makeRequestToAPI());
            }

            private HashMap<String, String> extractCategoriesDataFromJSON(String jsonData) {
                HashMap<String, String> categories = new HashMap<>();
                JsonArray categoriesData = JsonParser.parseString(jsonData)
                        .getAsJsonObject()
                        .getAsJsonObject("categories")
                        .getAsJsonArray("items");

                for (JsonElement category : categoriesData) {
                    JsonObject categoryData = category.getAsJsonObject();
                    categories.put(categoryData.get("name").getAsString(),
                                    categoryData.get("id").getAsString());
                }
                return categories;
            }
        };
        return getServicesIfAuthorised(request.getData());
    }
}

abstract class Request {
    protected String uriPath;

    public String getData() {
        return extractDataFromJSON(makeRequestToAPI());
    }

    abstract protected String extractDataFromJSON(String jsonData);

    protected String makeRequestToAPI() {
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + Authorisation.ACCESS_TOKEN)
                .uri(URI.create(Authorisation.API_PATH + this.uriPath))
                .GET()
                .build();
        HttpClient client = HttpClient.newBuilder().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return "{\"error\":\"Error occurred while getting response from API\"}";
    }
}