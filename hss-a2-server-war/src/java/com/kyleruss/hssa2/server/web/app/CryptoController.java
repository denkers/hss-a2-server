//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.web.app;

import com.kyleruss.hssa2.commons.Password;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoController 
{
    private static CryptoController instance;
    
    public SecretKey generateEphemeralKey(Password password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        char[] passChars            =   password.getPasswordCharacters();
        byte[] saltBytes            =   salt.getBytes();
        SecretKeyFactory keyFactory =   SecretKeyFactory.getInstance("PBEWithSHA256AndAES");
        KeySpec keySpec             =   new PBEKeySpec(passChars, saltBytes, 256);
        byte[] encodedKey           =   keyFactory.generateSecret(keySpec).getEncoded();
        
        return new SecretKeySpec(encodedKey, "AES");
    }
    
    public static CryptoController getInstance()
    {
        if(instance == null) instance = new CryptoController();
        return instance;
    }
}
