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
    public static final String ALPHA_NUMERIC    =   "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    static
    {
        rGen    =   new SecureRandom();
    }
            
    public static String generateRandomString(int length, String charset)
    {
        StringBuilder builder   =   new StringBuilder();
        
        for(int i = 0; i < length; i++)
        {
            if(charset == null)
                builder.append((char) rGen.nextInt(Character.MAX_VALUE));
            else
            {
                int index   =   rGen.nextInt(charset.length());
                builder.append(charset.charAt(index));
            }
        }
        
        return builder.toString();
    }
    
}
