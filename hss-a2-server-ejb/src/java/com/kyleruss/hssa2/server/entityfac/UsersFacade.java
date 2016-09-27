//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.entityfac;

import com.kyleruss.hssa2.server.entity.Users;
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
}
