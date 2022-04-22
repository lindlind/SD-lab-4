package utils;

import exceptions.UnavailableException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UrlReader {

    public String readFromUrl(String stringUrl) {
        final URI uri;
        try {
            uri = new URI(stringUrl);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to parse uri");
        }
        final HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        final HttpResponse<String> response;
        try{
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException e) {
            throw new UnavailableException("Failed to connect to stock market");
        } catch (InterruptedException e) {
            throw new IllegalStateException("Interrupted", e);
        }
    }

}