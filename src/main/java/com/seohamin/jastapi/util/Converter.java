package com.seohamin.jastapi.util;

import java.nio.charset.StandardCharsets;

public class Converter {

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
            return object.toString().getBytes(StandardCharsets.UTF_8);
        }
    }
}
