package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonMapper {
    final ObjectMapper mapper = new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT);

    public String toJson(Object value) throws JsonProcessingException {
        return mapper.writeValueAsString(value);
    }

    public <T> T fromJson(String value, TypeReference<T> valueTypeRef) throws JsonProcessingException {
        return mapper.readValue(value, valueTypeRef);
    }
}
