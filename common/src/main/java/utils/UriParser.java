package utils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UriParser {

    private final URI uri;
    private final Map<String, List<String>> params;

    public UriParser(URI uri) {
        this.uri = uri;
        final String query = uri.getQuery();
        this.params = query == null ? Map.of() : Arrays.stream(uri.getQuery().split("&"))
            .map(p -> p.split("="))
            .collect(Collectors.toUnmodifiableMap(
                l -> l.length > 0 ? l[0] : "",
                l -> l.length > 1 ? Arrays.stream(l[1].split(",")).collect(Collectors.toList()) : List.of("")
            ));
    }

    public String parseParam(String key) {
        final List<String> values = params.get(key);
        if (values == null) {
            throw new IllegalArgumentException("Expected but not found query param " + key);
        }
        return values.get(0);
    }

}
