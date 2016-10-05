//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.web.crypto;

public class Password extends SafeStructure
{
    
    public Password(int length)
    {
        super(length);
        data    =   CryptoUtils.generateRandomString(length, CryptoUtils.ALPHA_NUMERIC).getBytes();
    }
    
    public Password(String password)
    {
        super(password.length());
        data    =   password.getBytes();
    }
    
    public String getPassword()
    {
        return new String(data);
    }
    
    public void setPassword(String password)
    {
        data   =   password.getBytes();
    }
    
    public char[] getPasswordCharacters()
    {
        return new String(data).toCharArray();
    }
}
