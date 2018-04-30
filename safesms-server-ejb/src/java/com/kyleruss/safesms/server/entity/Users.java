//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.safesms.server.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
@Table(name = "users")
public class Users implements Serializable 
{
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId")
    private List<UserKeys> userKeysList;

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "id")
    private String id;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "name")
    private String name;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "email")
    private String email;
    
    @Lob
    @Column(name = "profile_image")
    private byte[] profileImage;
    
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    public Users() {}

    public Users(String id) 
    {
        this.id = id;
    }

    public Users(String id, String name, String email) 
    {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() 
    {
        return id;
    }

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getName() 
    {
        return name;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getEmail() 
    {
        return email;
    }

    public void setEmail(String email) 
    {
        this.email = email;
    }

    public Date getCreatedDate() 
    {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) 
    {
        this.createdDate = createdDate;
    }

    public byte[] getProfileImage()
    {
        return profileImage;
    }

    public void setProfileImage(byte[] profileImage) 
    {
        this.profileImage = profileImage;
    }
    
    

    @Override
    public int hashCode() 
    {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        if (object == null || !(object instanceof Users)) 
            return false;
        
        Users other = (Users) object;
        return this.id.equals(other.getId());
    }

    @Override
    public String toString() 
    {
        return "Users[ id=" + id + " ]";
    }

    public List<UserKeys> getUserKeysList()
    {
        return userKeysList;
    }

    public void setUserKeysList(List<UserKeys> userKeysList)
    {
        this.userKeysList = userKeysList;
    }
}
