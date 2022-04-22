package clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import models.Stock;
import utils.JsonMapper;
import utils.UrlReader;

public class StockMarketClientImpl implements StockMarketClient {

    private static final String HOST = "localhost";
    private final int port;
    private final UrlReader urlReader;
    private final JsonMapper jsonMapper;

    public StockMarketClientImpl(int port, UrlReader urlReader) {
        this.port = port;
        this.urlReader = urlReader;
        this.jsonMapper = new JsonMapper();
    }

    @Override
    public List<Stock> getStocksInfo() {
        final String url = constructUrl("/stocks", Map.of());
        final String resultJson = urlReader.readFromUrl(url);
        try {
            return jsonMapper.fromJson(resultJson, new TypeReference<List<Stock>>(){});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to convert from json");
        }
    }

    @Override
    public boolean buyStocks(String companyName, int count) {
        final String url = constructUrl("/buy", Map.of(
            "company_name", companyName,
            "stocks_count", Integer.toString(count)
        ));
        final String result = urlReader.readFromUrl(url);
        return "Done".equals(result);
    }

    @Override
    public boolean sellStocks(String companyName, int count) {
        final String url = constructUrl("/sell", Map.of(
            "company_name", companyName,
            "stocks_count", Integer.toString(count)
        ));
        final String result = urlReader.readFromUrl(url);
        return "Done".equals(result);
    }

    private String constructUrl(String query, Map<String, String> params) {
        String paramsAsString = params.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));
        return "http://" + HOST + ":" + port + query + "?" + paramsAsString;
    }
}
