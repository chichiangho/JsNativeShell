package com.seeyon.cmp.common.utils;

import java.io.IOException;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

public class BASE64DecoderUtil {

    private final static BASE64Decoder d = new BASE64Decoder();
    private final static BASE64Encoder e = new BASE64Encoder();

    public static String encode(byte[] bytes) {
        return e.encode(bytes);
    }

    public static byte[] decodeBuffer(String bufferStr) throws IOException {
        return d.decodeBuffer(bufferStr);
    }

    public static String encodeBuffer(byte[] bytes) {
        return e.encodeBuffer(bytes);
    }
}
