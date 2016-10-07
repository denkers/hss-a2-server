//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.web.app;

import com.kyleruss.hssa2.commons.Password;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Encoder;

public class CryptoController 
{
    private static CryptoController instance;
    
    public String ephemeralDecrypt(Password password, String salt, String encodedCiphertext) 
    throws NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchPaddingException, InvalidKeyException, 
    InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
    {
        byte[] passwordBytes    =   password.getPassword().getBytes("UTF-8");
        byte[] saltBytes        =   salt.getBytes("UTF-8");
        SecretKeySpec secretKey =   new SecretKeySpec(passwordBytes, "AES");
        IvParameterSpec iv      =   new IvParameterSpec(saltBytes);
        
        Cipher cipherDec    =   Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipherDec.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] plaintext = cipherDec.doFinal(Base64.getDecoder().decode(encodedCiphertext.getBytes("UTF-8")));
        return new String(plaintext);
    }
    
    public String ephemeralEncrypt(Password password, String salt, String plaintext) 
    throws NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchPaddingException, InvalidKeyException, 
    InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
    {
        byte[] passwordBytes    =   password.getPassword().getBytes("UTF-8");
        byte[] saltBytes        =   salt.getBytes("UTF-8");
        SecretKeySpec secretKey =   new SecretKeySpec(passwordBytes, "AES");
        IvParameterSpec iv      =   new IvParameterSpec(saltBytes);
        
        Cipher cipher           =   Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] ciphertext = cipher.doFinal(plaintext.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(ciphertext);
    }
    
    public static CryptoController getInstance()
    {
        if(instance == null) instance = new CryptoController();
        return instance;
    }
}
