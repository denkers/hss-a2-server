//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.web.app;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class UserManager
{
    private static UserManager instance;
    private Set<String> users;
    
    private UserManager() {}
    
    protected void init()
    {
        if(users != null) return;
        
        users   =   new LinkedHashSet<>();
    }
    
    public synchronized boolean addUser(String id)
    {
        return users.add(id);
    }
    
    public synchronized boolean removeUser(String id)
    {
        return users.remove(id);
    }
    
    public synchronized int getNumCurrentUsers()
    {
        return users.size();
    }
    
    public synchronized Collection<String> getUsers()
    {
        return users;
    }
    
    public synchronized boolean containsUser(String id)
    {
        return users.contains(id);
    }
    
    public static UserManager getInstance()
    {
        if(instance == null) instance = new UserManager();
        return instance;
    }
}
