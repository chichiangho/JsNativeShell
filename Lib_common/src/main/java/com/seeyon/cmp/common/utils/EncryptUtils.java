package com.seeyon.cmp.common.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密解密工具类
 */
public class EncryptUtils {
    public static final String ALGORITHM = "DES";

    /**
     * 转换密钥<br>
     *
     * @param key
     * @return
     * @throws Exception
     */
    private static Key toKey(byte[] key) throws Exception {

        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey secretKey = keyFactory.generateSecret(dks);

        // 当使用其他对称加密算法时，如AES、Blowfish等算法时，用下述代码替换上述三行代码
        // SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);

        return secretKey;
    }

    /**
     * 解密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, String key) throws Exception {
        Key k = toKey(decryptBASE64(key));

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, k);

        return cipher.doFinal(data);
    }

    /**
     * 加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, String key) throws Exception {
        Key k = toKey(decryptBASE64(key));
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, k);

        return cipher.doFinal(data);
    }

    /**
     * 生成密钥
     *
     * @return
     * @throws Exception
     */
    public static String initKey() throws Exception {
        return initKey(null);
    }

    /**
     * 生成密钥
     *
     * @param seed
     * @return
     * @throws Exception
     */
    public static String initKey(String seed) throws Exception {
        SecureRandom secureRandom = null;

        if (seed != null) {
            secureRandom = new SecureRandom(decryptBASE64(seed));
        } else {
            secureRandom = new SecureRandom();
        }

        KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);
        kg.init(secureRandom);

        SecretKey secretKey = kg.generateKey();

        return encryptBASE64(secretKey.getEncoded());
    }

    /**
     * BASE64解密
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptBASE64(String key) throws Exception {
        return BASE64DecoderUtil.decodeBuffer(key);
    }

    /**
     * BASE64加密
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static String encryptBASE64(byte[] key) throws Exception {
        return BASE64DecoderUtil.encodeBuffer(key);
    }

    /**
     * aes 128 加密
     *
     * @param content  加密内容
     * @param password 加密密匙
     * @return
     */
    public static byte[] aes128Encrypt(String content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密密匙
     *
     * @param content  解密内容
     * @param password 解密密匙
     * @return
     */
    public static byte[] aes128Decrypt(byte[] content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] encrypt(byte[] data, byte[] raw) throws Exception {

        SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        byte[] encrypted = new byte[cipher.getOutputSize(data.length)];

        int ctLength = cipher.update(data, 0, data.length, encrypted, 0);
        ctLength += cipher.doFinal(encrypted, ctLength);

        return encrypted;
    }

    public static String encryptStr2HexStr(String text, String key) throws Exception {
        if (text == null || text.length() == 0)
            return "";
        if (key == null || key.length() != 16)
            throw new InvalidKeyException();

        byte[] encrypted = encrypt(text.getBytes("UTF-8"), key.getBytes());
        String hexStr = parseBytes2HexStr(encrypted);

        return hexStr;
    }

    public static byte[] decrypt(byte[] encrypted, byte[] raw) throws Exception {

        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);

        byte[] decrypted = cipher.doFinal(encrypted);

        return decrypted;
    }

    /**
     * 解密
     *
     * @param hexStr
     * @param key
     * @return
     * @throws Exception
     */
    public static String decryptHexStr2Str(String hexStr, String key) throws Exception {

        if (hexStr == null || hexStr.length() == 0)
            return "";
        if (key == null || key.length() != 16)
            throw new InvalidKeyException();

        byte[] encrypted = parseHexStr2Bytes(hexStr);
        byte[] decrypted = decrypt(encrypted, key.getBytes());

        String text = new String(decrypted, "utf-8");

        return text;
    }

    public static String parseBytes2HexStr(byte[] bytes) {
        if (bytes == null)
            return null;

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1)
                hex = '0' + hex;

            sb.append(hex.toUpperCase());
        }

        return sb.toString();
    }

    public static String getAeskey() {
        return "0123456789ABCDEF";
    }

    public static String getUrlkey() {
        return "seeyonupdate2011";
    }

    public static byte[] parseHexStr2Bytes(String hexStr) throws Exception {
        if (hexStr == null)
            return null;

        int hexLen = hexStr.length();

        if (hexLen % 2 > 0)
            throw new Exception("invalid hexStr");

        int byteLen = hexLen / 2;
        byte[] result = new byte[byteLen];

        for (int i = 0; i < byteLen; i++)
            result[i] = Integer.valueOf(hexStr.substring(2 * i, 2 * i + 2), 16).byteValue();

        return result;
    }
}