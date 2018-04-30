//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.safesms.server.web.util;

import java.io.Serializable;

public class ActionResponse implements Serializable
{
    //The response message i.e error/success message
    private String message;
    
    //The status of the action/service request
    //True indicates some action was successful
    private boolean actionStatus;
    
    //Extra data to pass back to the user
    private Object data;
    
    public ActionResponse()
    {
        message         =   "";
        actionStatus    =   false;
        data            =   null;
    }
    
    public ActionResponse(String message, boolean actionStatus)
    {
        this(message, actionStatus, null);
    }
    
    public ActionResponse(String message, boolean actionStatus, Object data)
    {
        this.message        =   message;
        this.actionStatus   =   actionStatus;
        this.data           =   data;
    }

    public String getMessage() 
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public boolean isActionStatus()
    {
        return actionStatus;
    }

    public void setActionStatus(boolean actionStatus)
    {
        this.actionStatus = actionStatus;
    }

    public Object getData() 
    {
        return data;
    }

    public void setData(Object data)
    {
        this.data = data;
    }
}
