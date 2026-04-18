package com.seohamin.jastapi.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;

/**
 * A utility class providing methods for type conversion.
 */
public class Converter {

    public static final ObjectMapper objectMapper = new ObjectMapper();

    // 인스턴스화 방지 | Prevents instantiation
    private Converter() {}

    /**
     * 특정 객체를 byte 타입 배열로 바꾸는 메서드.
     * byte가 아니거나 String이 아니라면 ObjectMapper를 통해 변환함.
     * Converts a given object into a byte array.
     * If the object is already a byte array, it is returned as is.
     * For all other types, the object is serialized into a JSON byte array
     * using Jackson's ObjectMapper.
     * @param object 변환할 객체 (The object to be converted.)
     * @return 변환 된 byte 배열 (The resulting byte array, or an empty array if conversion fails or the object is null.)
     */
    public static byte[] convertToByte(final Object object) {
        if (object == null) {
            return new byte[0];
        }
        else if (object instanceof byte[]) {
            return (byte[]) object;
        }
        else if (object instanceof String) {
            return ((String) object).getBytes(StandardCharsets.UTF_8);
        }
        else {
            try {
                // Serializes the object into a JSON byte array
                return objectMapper.writeValueAsBytes(object);
            } catch (JsonProcessingException ex) {
                System.err.println("[ERROR] JSON processing exception occurred.");
                return new byte[0];
            }
        }
    }
}
