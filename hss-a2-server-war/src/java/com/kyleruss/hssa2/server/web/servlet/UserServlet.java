//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.web.servlet;

import com.google.gson.JsonObject;
import com.kyleruss.hssa2.commons.CryptoUtils;
import com.kyleruss.hssa2.commons.Password;
import com.kyleruss.hssa2.commons.RequestPaths;
import com.kyleruss.hssa2.server.entityfac.UsersFacade;
import com.kyleruss.hssa2.server.web.app.CryptoController;
import com.kyleruss.hssa2.server.web.app.MailController;
import com.kyleruss.hssa2.server.web.app.ServerKeyManager;
import com.kyleruss.hssa2.server.web.app.UserManager;
import com.kyleruss.hssa2.server.web.util.ActionResponse;
import com.kyleruss.hssa2.server.web.util.ServletUtils;
import java.io.IOException;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "UserServlet", urlPatterns = 
{
    RequestPaths.SERV_CONNECT_REQ,
    RequestPaths.PASS_REQ,
    RequestPaths.USER_LIST_REQ,
    RequestPaths.PROFILE_UP_REQ,
    RequestPaths.SERV_DISCON_REQ
})
public class UserServlet extends HttpServlet 
{
    @EJB
    private UsersFacade usersFacade;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException 
    {
        String password =   "abcdefghrlemfyir";
        String salt     =   "welymcturkcmpoei";
        
        try
        {
            String plaintext = "Hello World";
        /*    String ciphertext = CryptoController.getInstance().pbeEncrypt(new Password(password), salt, plaintext);
            String dectext = CryptoController.getInstance().pbeDecrypt(new Password(password), salt, ciphertext);
            
            System.out.println("cipher text: " + ciphertext);
            System.out.println("plain text: " + dectext); */
        }
        
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException 
    {
        String path =   request.getServletPath();
        
        switch(path)
        {
            case RequestPaths.SERV_CONNECT_REQ:
                processUserConnect(request, response);
                break;
            case RequestPaths.PASS_REQ: 
                 processUserPasswordRequest(request, response); 
                 break;
            case RequestPaths.USER_LIST_REQ:
            case RequestPaths.PROFILE_UP_REQ:
            case RequestPaths.SERV_DISCON_REQ:
                processUserDisconnect(request, response);
                break;
            default: break;
        }
    }
    
    protected void processUserPasswordRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException 
    {
        try
        {
            JsonObject dataObj  =   ServletUtils.getPublicEncryptedClientJson(request, ServerKeyManager.getInstance().getServerPrivateKey());
            
            String userEmail    =   dataObj.getAsJsonPrimitive("email").getAsString();
            String password     =   CryptoUtils.generateRandomString(8, CryptoUtils.ALPHA_NUMERIC);
            MailController.getInstance().sendPasswordMail(userEmail, password);
            ActionResponse resp =   new ActionResponse("An authentication code has been sent to your email", true);
            ServletUtils.jsonResponse(response, resp);
        }
        
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            ActionResponse resp =   new ActionResponse("Failed to send authentication code ", false);
            ServletUtils.jsonResponse(response, resp);
        }
    }
    
    protected void processUserConnect(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException 
    {
        try
        {
            JsonObject dataObj  =   ServletUtils.getPublicEncryptedClientJson(request, ServerKeyManager.getInstance().getServerPrivateKey());
            String phoneID      =   dataObj.get("phoneID").getAsString();
            boolean connectStatus;
            
            if(usersFacade.find(phoneID) != null)
            {
                UserManager.getInstance().addUser(phoneID);
                connectStatus   =   true;
            }
            
            else connectStatus = false;
            
            String connectMessage       =   connectStatus? "Successfully connected" : "Failed to connect: User ID not found";
            ActionResponse actResponse  =   new ActionResponse(connectMessage, connectStatus);
            ServletUtils.jsonResponse(response, actResponse);
        }
        
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            ActionResponse actResponse  =   new ActionResponse("Failed to connect to server", false);
            ServletUtils.jsonResponse(response, actResponse);
        }
    }
    
    protected void processUserDisconnect(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException 
    {
        try
        {
            JsonObject dataObj          =   ServletUtils.getPublicEncryptedClientJson(request, ServerKeyManager.getInstance().getServerPrivateKey());
            String phoneID              =   dataObj.getAsJsonPrimitive("phoneID").getAsString();
            boolean disconnectStatus    =   false;
            String disconnectResponse;
            
            if(usersFacade.find(phoneID) != null)
            {
                if(!UserManager.getInstance().containsUser(phoneID))
                {
                    disconnectStatus     =   false;
                    disconnectResponse    =   "Failed to disconnect: You are not currently connected";
                }
                
                else
                {
                    UserManager.getInstance().removeUser(phoneID);
                    disconnectStatus     =   true;
                    disconnectResponse   =   "Successfully disconnected from server";
                }
            }
            
            else disconnectResponse  =   "Failed to disconnect: User ID not found";
            
            ActionResponse actResponse  =   new ActionResponse(disconnectResponse, disconnectStatus);
            ServletUtils.jsonResponse(response, actResponse);
        }
        
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            ActionResponse actResponse  =   new ActionResponse("Failed to disconnect from the server", false);
            ServletUtils.jsonResponse(response, actResponse);
        }
    }
}
