package pl.mdmb.KafkaDataBridge.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static String serialize(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
    public static <T> T deserialize(String data, TypeReference<T> typeReference) throws JsonProcessingException {
        return objectMapper.readValue(data, typeReference);
    }

}
