package com.wcs.base.util.des;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:DES加密解密 <br>
 * 
 * <p>DESEncrypt des = new DESEncrypt(); </p>
 * <p>字符串加密：des.encryptString(str);</p>
 * <p>字符串解密：des.decryptString(str); </p>
 * <p>Copyright © 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
public class DESEncrypt {

    private static final String ENCODING = "UTF-8";

    private static final String ALGORITHM_NAME = "DES";
    // 默认密钥
    private static String strDefaultKey = "wcsSecureKey"; 

    private Cipher encryptCipher = null;

    private Cipher decryptCipher = null;

    public DESEncrypt() throws Exception {
        this(strDefaultKey);
    }

    /**
     * @param strKey
     *            至少8位
     * @throws Exception
     */
    public DESEncrypt(String strKey) throws Exception {
        this(strKey, false);
    }

    public DESEncrypt(String strKey, boolean isRandom) throws Exception {
        Key key = null;
        if (!isRandom) {
            key = getKey(strKey);
        } else {
            key = getRandomKey(strKey);
        }
        encryptCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        encryptCipher.init(Cipher.ENCRYPT_MODE, key);

        decryptCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        decryptCipher.init(Cipher.DECRYPT_MODE, key);
    }

    /**
     * 从指定字符串生成密钥
     * 
     * @param bytes
     * @return
     * @throws Exception
     */
    private Key getKey(String str) throws Exception {
        DESKeySpec dks = new DESKeySpec(str.getBytes(ENCODING));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM_NAME);
        return keyFactory.generateSecret(dks);
    }

    /**
     * 从指定字符串随机生成密钥
     * 
     * @param bytes
     * @return
     * @throws Exception
     */
    private Key getRandomKey(String str) throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM_NAME);
        generator.init(new SecureRandom(str.getBytes(ENCODING)));
        return generator.generateKey();
    }

    /**
     * 解密输入流
     * 
     * @param in
     * @return
     * @throws Exception
     */
    public InputStream decrypt(InputStream in) throws Exception {// 对数据进行解密
        byte[] b = InputStreamToByte(in);
        return new ByteArrayInputStream(decrypt(b));
    }

    private byte[] InputStreamToByte(InputStream is) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        while ((ch = is.read()) != -1) {
            bytestream.write(ch);
        }
        byte imgdata[] = bytestream.toByteArray();
        bytestream.close();
        return imgdata;
    }

    /**
     * 加密字节数组
     * 
     * @param bytes
     * @return
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws Exception
     */
    public byte[] encrypt(byte[] bytes) throws IllegalBlockSizeException, BadPaddingException {
        return encryptCipher.doFinal(bytes);
    }

    /**
     * 解密字节数组
     * 
     * @param bytes
     * @return
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws Exception
     */
    public byte[] decrypt(byte[] bytes) throws IllegalBlockSizeException, BadPaddingException {
        return decryptCipher.doFinal(bytes);
    }

    /**
     * 加密字符串
     * 
     * @param str
     * @return
     * @throws Exception
     * @throws Exception
     */
    public String encrypt(String str) throws Exception {
        BASE64Encoder base64en = new BASE64Encoder();
        byte[] encryptByte = encrypt(str.getBytes(ENCODING));
        return base64en.encode(encryptByte).replaceAll("\n", "");
    }

    /**
     * 
     * 解密字符串
     * 
     * @param str
     * @return
     * @throws Exception
     */
    public String decrypt(String str) throws Exception {

        BASE64Decoder base64De = new BASE64Decoder();
        byte[] sourceByte = base64De.decodeBuffer(str);
        return new String(decrypt(sourceByte),ENCODING);
    }

}
