package com.seohamin.jastapi.util;

import java.nio.charset.StandardCharsets;

public class Converter {

    // 인스턴스화 방지
    public Converter() {}

    /**
     * 특정 객체를 byte 타입 배열로 바꾸는 메서드.
     * byte가 아니거나 String이 아니라면 toString()을 통해 문자열로
     * 변환 후 다시 byte 배열로 변환한다.
     * @param object 변환할 객체
     * @return 변환 된 byte 배열
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
            return object.toString().getBytes(StandardCharsets.UTF_8);
        }
    }
}
