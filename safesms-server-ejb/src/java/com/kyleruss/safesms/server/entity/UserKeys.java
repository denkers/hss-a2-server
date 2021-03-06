//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.safesms.server.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
@Table(name = "user_keys")
public class UserKeys implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "pub_key")
    private String pubKey;
    
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Users userId;

    public UserKeys() {}

    public UserKeys(Integer id) 
    {
        this.id = id;
    }

    public UserKeys(Integer id, String pubKey) 
    {
        this.id = id;
        this.pubKey = pubKey;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id) 
    {
        this.id = id;
    }

    public String getPubKey() 
    {
        return pubKey;
    }

    public void setPubKey(String pubKey) 
    {
        this.pubKey = pubKey;
    }

    public Date getCreatedDate() 
    {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) 
    {
        this.createdDate = createdDate;
    }

    public Users getUserId() 
    {
        return userId;
    }

    public void setUserId(Users userId)
    {
        this.userId = userId;
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
        if (id == null || object == null || !(object instanceof UserKeys)) 
            return false;
        
        UserKeys other = (UserKeys) object;
        return this.id.equals(other.getId());
    }

    @Override
    public String toString()
    {
        return "UserKeys[ id=" + id + " ]";
    }
}
