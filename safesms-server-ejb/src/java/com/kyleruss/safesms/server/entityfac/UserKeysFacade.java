//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.safesms.server.entityfac;

import com.kyleruss.safesms.server.entity.UserKeys;
import com.kyleruss.safesms.server.entity.Users;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Stateless
@LocalBean
public class UserKeysFacade extends AbstractFacade<UserKeys> 
{
    @PersistenceContext(unitName = "hss-a2-server-ejbPU")
    private EntityManager em;

    public UserKeysFacade() 
    {
        super(UserKeys.class);
    }
    
    @Override
    protected EntityManager getEntityManager() 
    {
        return em;
    }
    
    //Creates a new public key user_keys record for the passed user with public key
    //User record should already exist and the public key must be valid
    //Updates the public key if the user already has a public key record
    public Entry<Boolean, String> createUserKey(Users user, String publicKey)
    {
        boolean result  =   false;
        String response;
        
        if(user == null) response   =   "User not found";
        
        else if(publicKey == null || publicKey.length() == 0) response  =   "Invalid key length";
        
        else
        {
            UserKeys keyRecord  =   getKeyForUser(user);
            
            //No public key record, create a new one
            if(keyRecord == null)
            {
                keyRecord   =   new UserKeys();
                keyRecord.setUserId(user);
                keyRecord.setPubKey(publicKey);
                create(keyRecord);
                result      =   em.contains(keyRecord);
                response    =   result? "Successfully created users public key" : "Failed to create user public key";   
            }
            
            //Public key record already exists, update it
            else
            {
                keyRecord.setPubKey(publicKey);
                edit(keyRecord);
                result      =   true;
                response    =   "Successfully updated public key record";   
            } 
        }
        
        return new SimpleEntry<>(result, response);
    }
    
    //Returns the public key record for the passed user
    //If exception occurs or user is not found, returns null
    public UserKeys getKeyForUser(Users user)
    {
        if(user == null) return null;
        
        else
        {
            CriteriaBuilder builder             =   em.getCriteriaBuilder();
            CriteriaQuery<UserKeys> query       =   builder.createQuery(entityClass);
            Root<UserKeys> from                 =   query.from(entityClass);
            query.select(from);
            query.where(builder.equal(from.get("userId"), user));
            
            try { return em.createQuery(query).getSingleResult(); }
            catch(NoResultException e) { return null; }
        }
    }
}
