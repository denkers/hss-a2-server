//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.web.crypto;

import java.security.SecureRandom;

public class CryptoUtils 
{
    private static SecureRandom rGen;
    
    static
    {
        rGen    =   new SecureRandom();
    }
            
    public static String generateRandomString(int length)
    {
        StringBuilder builder   =   new StringBuilder();
        for(int i = 0; i < length; i++)
            builder.append((char) rGen.nextInt(Character.MAX_VALUE));
        
        return builder.toString();
    }
}
