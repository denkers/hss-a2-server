//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.entityfac;

import com.kyleruss.hssa2.server.entity.UserKeys;
import com.kyleruss.hssa2.server.entity.Users;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class UserKeysFacade extends AbstractFacade<UserKeys> 
{
    @PersistenceContext(unitName = "hss-a2-server-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        
        return em;
    }

    public UserKeysFacade() 
    {
        super(UserKeys.class);
    }
    
    public Entry<Boolean, String> createUserKey(Users user, String publicKey)
    {
        boolean result  =   false;
        String response;
        
        if(user == null) response   =   "User not found";
        
        else if(publicKey == null || publicKey.length() == 0) response  =   "Invalid key length";
        
        else
        {
            UserKeys keyRecord  =   new UserKeys();
            keyRecord.setUserId(user);
            keyRecord.setPubKey(publicKey);
            create(keyRecord);
            result      =   em.contains(keyRecord);
            response    =   result? "Successfully create users public key" : "Failed to create user public key";   
        }
        
        return new SimpleEntry<>(result, response);
    }
}
