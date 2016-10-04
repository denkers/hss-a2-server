//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.web.crypto;

public class Password 
{
    private String password;
    
    public Password(int length)
    {
        password    =   CryptoUtils.generateRandomString(length);
    }
    
    public Password(String password)
    {
        this.password   =   password;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setPassword(String password)
    {
        this.password   =   password;
    }
    
    public byte[] getPasswordBytes()
    {
        return password.getBytes();
    }
    
    public int getPasswordLength()
    {
        return password.length();
    }
}
