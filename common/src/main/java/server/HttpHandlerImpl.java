package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.InvalidRequestException;
import exceptions.UnavailableException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class HttpHandlerImpl implements HttpHandler {

    private final Function<URI, String> handler;

    public HttpHandlerImpl(Function<URI, String> handler) {
        this.handler = handler;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            int rc = 200;
            String response;
            try {
                response = handler.apply(exchange.getRequestURI());
            } catch (IllegalArgumentException e) {
                rc = 400;
                e.printStackTrace();
                response = e.getMessage();
            } catch (InvalidRequestException e) {
                rc = 404;
                e.printStackTrace();
                response = "Not found";
            } catch (UnavailableException e) {
                rc = 500;
                e.printStackTrace();
                response = e.getMessage();
            } catch (Exception e) {
                rc = 500;
                e.printStackTrace();
                response = "Internal error";
            }

            exchange.sendResponseHeaders(rc, response.length());

            OutputStream s = exchange.getResponseBody();
            s.write(response.getBytes(StandardCharsets.UTF_8));
            s.flush();
            s.close();
        }
    }
}