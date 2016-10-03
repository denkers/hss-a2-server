//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.web.crypto;

public class CryptoController 
{
    private static CryptoController instance;
    
    public static CryptoController getInstance()
    {
        if(instance == null) instance = new CryptoController();
        return instance;
    }
}
