//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.web.app;

public class ServerKeyManager 
{
    private static ServerKeyManager instance;
    
    private ServerKeyManager() {}
    
    protected void init()
    {
        initPrivateKey();
        initPublicKey();
    }
    
    private void initPrivateKey()
    {
        
    }
    
    private void initPublicKey()
    {
        
    }
    
    public static ServerKeyManager getInstance()
    {
        if(instance == null) instance = new ServerKeyManager();
        return instance;
    }
}
