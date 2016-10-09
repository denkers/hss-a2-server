//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.web.app;

import com.kyleruss.hssa2.commons.Password;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoController 
{
    private static CryptoController instance;
    
    public String pbeDecrypt(Password password, String salt, String encodedCiphertext) 
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
    
    public String pbeEncrypt(Password password, String salt, String plaintext) 
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
    
    public String publicEncrypt(String plaintext, Key key) 
    throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, 
    NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
    {
        Cipher cipher       =   Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherBytes  =   cipher.doFinal(plaintext.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(cipherBytes);
    }
    
    public String publicDecrypt(String ciphertext, Key key) 
    throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, 
    IllegalBlockSizeException, InvalidKeyException, BadPaddingException
    {
        Cipher cipher           =   Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decoded          =   Base64.getDecoder().decode(ciphertext.getBytes("UTF-8"));
        byte[] plaintextBytes   =   cipher.doFinal(decoded);
        return new String(plaintextBytes);
    }
    
    public String generateHash(byte[] data) 
    throws NoSuchAlgorithmException
    {
        return generateHash(data, "MD5");
    }
    
    public String generateHash(byte[] data, String algorithm) 
    throws NoSuchAlgorithmException
    {
        MessageDigest md    =  MessageDigest.getInstance(algorithm);
        byte[] digest       =   md.digest(data);
        return Base64.getEncoder().encodeToString(digest);
    }
    
    public static CryptoController getInstance()
    {
        if(instance == null) instance = new CryptoController();
        return instance;
    }
}
