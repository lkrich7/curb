package curb.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * JSON 序列化/反序列化工具
 * 封装Jackson
 */
public enum JsonUtil {
    ;

    private static final ObjectMapper MAPPER;
    private static final GenericType<Map<String, Object>> MAP_TYPE_REFERENCE = new GenericType<Map<String, Object>>() {
    };

    static {
        MAPPER = new ObjectMapper();
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        MAPPER.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static Map<String, Object> parseObject(String jsonStr) {
        return parseObject(jsonStr, MAP_TYPE_REFERENCE);
    }

    public static <T> T parseObject(String jsonStr, Class<T> clazz) {
        if (jsonStr == null || jsonStr.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readValue(jsonStr, clazz);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T parseObject(String jsonStr, GenericType<T> type) {
        if (jsonStr == null) {
            return null;
        }
        try {
            return MAPPER.readValue(jsonStr, type.typeReference);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> List<T> parseArray(String src, Class<T> clazz) {
        if (src == null) {
            return null;
        }
        try {
            CollectionType type = MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);
            return MAPPER.readValue(src, type);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String toJSONString(Object obj) {
        return toJSONString(obj, false);
    }

    public static String toJSONString(Object obj, boolean pretty) {
        if (obj == null) {
            return null;
        }
        try {
            ObjectMapper mapper = MAPPER;
            if (pretty) {
                mapper = MAPPER.copy();
                mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            }
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public abstract static class GenericType<T> {
        private final Type myType;

        private final TypeReference<T> typeReference = new TypeReference<T>() {
            @Override
            public Type getType() {
                return myType;
            }
        };

        protected GenericType() {
//             copy from com.fasterxml.jackson.core.type.TypeReference
            Type superClass = getClass().getGenericSuperclass();
            if (superClass instanceof Class<?>) {
                throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
            }
            myType = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        }

    }

}
