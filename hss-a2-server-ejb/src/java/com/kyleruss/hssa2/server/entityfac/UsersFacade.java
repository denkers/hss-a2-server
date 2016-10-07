//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.entityfac;

import com.kyleruss.hssa2.server.entity.Users;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class UsersFacade extends AbstractFacade<Users>
{

    @PersistenceContext(unitName = "hss-a2-server-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        
        return em;
    }

    public UsersFacade() 
    {
        super(Users.class);
    }
    
    public Entry<Boolean, String> createUserAccount(String phoneID, String name, String email)
    {
        boolean result  =   false;
        String response;
        
        if(find(phoneID) != null)
            response    =   "Account with this phone ID already exists";
        
        else
        {
            Users user  =   new Users(phoneID, name, email);
            create(user);
            result      =   em.contains(user);
            response    =   result? "Successfully created account" : "Failed to create account";
        }
        
        return new SimpleEntry<>(result, response);
    }
}
