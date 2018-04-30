//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.safesms.server.entityfac;

import com.kyleruss.safesms.server.entity.Users;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Stateless
@LocalBean
public class UsersFacade extends AbstractFacade<Users>
{
    @PersistenceContext(unitName = "hss-a2-server-ejbPU")
    private EntityManager em;

    public UsersFacade() 
    {
        super(Users.class);
    }
    
    @Override
    protected EntityManager getEntityManager() 
    {
        return em;
    }
    
    //Edits the passed user record with the changed name, email or profile image
    //Profile image is expected to be Base 64 encoded and will be decoded
    public Entry<Boolean, String> updateUserAccount(Users user, String name, String email, String profileImage)
    {
        if(user == null)
            return new SimpleEntry<>(false, "User not found");
        
        else
        {
            user.setName(name);
            user.setEmail(email);
            
            if(profileImage != null) 
                user.setProfileImage(Base64.getDecoder().decode(profileImage));
            
            edit(user);
            return new SimpleEntry<>(true, "Successfully updated account");
        }
    }
    
    //Creates a new User record from the passed phone, name and email details
    //Users cannot have the same phone id 
    public Entry<Boolean, String> createUserAccount(String phoneID, String name, String email)
    {
        Users user  =  find(phoneID);
        
        if(user == null)
        {
            user                =   new Users(phoneID, name, email);
            create(user);
            boolean result      =   em.contains(user);
            String response     =   result? "Successfully created account" : "Failed to create account";
            
            return new SimpleEntry<>(result, response);
        }
        
        else return updateUserAccount(user, name, email, null);
    }
    
    public List<Users> getUserList()
    {
        return findAll();
    }
    
    //Returns the User records whose 
    //phone ids are contained in the passed id list
    public List<Tuple> getUsersInList(Collection<String> userIDList)
    {
        CriteriaBuilder builder          =   em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query       =  builder.createTupleQuery();
        Root<Users> from                 =   query.from(entityClass);
        query.multiselect
        (
                from.get("id").alias("id"), 
                from.get("name").alias("name"), 
                from.get("profileImage").alias("profileImage"), 
                from.get("email").alias("email")
        );
        
        query.where(from.get("id").in(userIDList));
        
        try { return em.createQuery(query).getResultList(); }
        catch(NoResultException e) { return new ArrayList<>(); }
    }
}
