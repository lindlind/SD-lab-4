package utils;

import exceptions.UnavailableException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlReader {

    public String readFromUrl(String stringUrl) {
        final URL url;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to parse url");
        }
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String inputLine;
            final StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            return content.toString();
        } catch (IOException e) {
            throw new UnavailableException("Failed to connect to stock market");
        }
    }

}