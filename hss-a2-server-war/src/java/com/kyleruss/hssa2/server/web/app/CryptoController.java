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
import java.security.PrivateKey;
import java.security.PublicKey;
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
    
    public SecretKey generateEphemeralKey(String password, String salt) 
    throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
    InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, InvalidParameterSpecException 
    {
        char[] passChars                =   password.toCharArray();
        byte[] saltBytes                =   salt.getBytes("UTF-8");
        SecretKeyFactory keyFactory     =   SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_128");
        PBEKeySpec keySpec              =   new PBEKeySpec(passChars, saltBytes, 20, 128);
        SecretKey tmp                   =   keyFactory.generateSecret(keySpec);
        byte[] encodedKey               =   tmp.getEncoded();
        SecretKeySpec secretKey         =   new SecretKeySpec(encodedKey, "AES"); 
        
        Cipher cipher   =   Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        String exampleText  =   "Hello world";
        AlgorithmParameters params = cipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        byte[] ciphertext = cipher.doFinal(exampleText.getBytes("UTF-8"));
        String cipherstring = Base64.getEncoder().encodeToString(ciphertext);
        System.out.println("cipher text: " + cipherstring);
        
        
        SecretKeyFactory keyFactory2    =   SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_128");
        PBEKeySpec keySpec2             =   new PBEKeySpec(passChars, saltBytes, 20, 128);
        SecretKey tmp2                  =   keyFactory2.generateSecret(keySpec2);
        byte[] encodedKey2              =   tmp2.getEncoded();
        SecretKeySpec secretKey2        =   new SecretKeySpec(encodedKey2, "AES"); 
        
        Cipher cipherDec    =   Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipherDec.init(Cipher.DECRYPT_MODE, secretKey2, new IvParameterSpec(iv));
        byte[] plaintext = cipherDec.doFinal(Base64.getDecoder().decode(cipherstring.getBytes("UTF-8")));
        
        return new SecretKeySpec(encodedKey, "AES");
    }
    
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
    
    public String publicEncrypt(String plaintext, PublicKey key) 
    throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, 
    NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
    {
        Cipher cipher       =   Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherBytes  =   cipher.doFinal(plaintext.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(cipherBytes);
    }
    
    public String publicDecrypt(String ciphertext, PrivateKey key) 
    throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, 
    IllegalBlockSizeException, InvalidKeyException, BadPaddingException
    {
        Cipher cipher           =   Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decoded          =   Base64.getDecoder().decode(ciphertext.getBytes("UTF-8"));
        byte[] plaintextBytes   =   cipher.doFinal(decoded);
        return new String(plaintextBytes);
    }
    
    public static CryptoController getInstance()
    {
        if(instance == null) instance = new CryptoController();
        return instance;
    }
}
