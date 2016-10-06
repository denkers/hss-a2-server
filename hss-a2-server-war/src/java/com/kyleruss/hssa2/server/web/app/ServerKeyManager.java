//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.web.app;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class ServerKeyManager 
{
    private static ServerKeyManager instance;
    private KeyPair serverKeyPair;
    
    private ServerKeyManager() {}
    
    protected void init()
    {
        try
        {
            KeyPairGenerator keyGen =   KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(512);
            serverKeyPair   =   keyGen.generateKeyPair();
        }
        
        catch(NoSuchAlgorithmException e)
        {
            System.out.println("[Error] Failed to initialize server key pair: " + e.getMessage());
        }
    }
    
    public KeyPair getServerKeyPair()
    {
        return serverKeyPair;
    }
    
    public PrivateKey getServerPrivateKey()
    {
        return serverKeyPair.getPrivate();
    }

    private PublicKey getServerPublicKey()
    {
        return serverKeyPair.getPublic();
    }
    
    public static ServerKeyManager getInstance()
    {
        if(instance == null) instance = new ServerKeyManager();
        return instance;
    }
}
