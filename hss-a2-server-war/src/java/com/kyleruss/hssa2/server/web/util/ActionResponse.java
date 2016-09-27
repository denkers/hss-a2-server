//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.web.util;

import java.io.Serializable;

public class ActionResponse implements Serializable
{
    private String message;
    private boolean actionStatus;
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
