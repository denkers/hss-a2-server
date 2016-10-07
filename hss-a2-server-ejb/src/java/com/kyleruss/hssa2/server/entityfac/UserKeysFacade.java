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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

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
