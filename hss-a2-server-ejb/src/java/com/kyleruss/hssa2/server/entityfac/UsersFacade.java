//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.entityfac;

import com.kyleruss.hssa2.server.entity.Users;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

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
        boolean result;
        String response;
        
        Users user  =  find(phoneID);
        
        if(user == null)
        {
            user        =   new Users(phoneID, name, email);
            create(user);
            result      =   em.contains(user);
            response    =   result? "Successfully created account" : "Failed to create account";
        }
        
        else
        {
            user.setEmail(email);
            user.setName(name);
            edit(user);
            result      =   true;
            response    =   "Successfully updated account";   
        }
        
        return new SimpleEntry<>(result, response);
    }
    
    public List<Users> getUserList()
    {
        return findAll();
    }
    
    public boolean imageExists(String imageName)
    {
        CriteriaBuilder builder          =   em.getCriteriaBuilder();
        CriteriaQuery<Users> query       =   builder.createQuery(entityClass);
        Root<Users> from                 =   query.from(entityClass);
        query.select(from);
        query.where(builder.equal(from.get("profileImage"), imageName));
        
        return !em.createQuery(query).getResultList().isEmpty();
    }
    
    public List<Users> getUsersInList(Collection<String> userIDList)
    {
        CriteriaBuilder builder          =   em.getCriteriaBuilder();
        CriteriaQuery<Users> query       =   builder.createQuery(entityClass);
        Root<Users> from                 =   query.from(entityClass);
        query.multiselect(from.get("id"), from.get("name"), from.get("profileImage"));
        query.where(from.get("id").in(userIDList));
        
        try { return em.createQuery(query).getResultList(); }
        catch(NoResultException e) { return new ArrayList<>(); }
    }
}
