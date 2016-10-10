//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.web.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kyleruss.hssa2.commons.CryptoUtils;
import com.kyleruss.hssa2.commons.EncryptedSession;
import com.kyleruss.hssa2.commons.Password;
import com.kyleruss.hssa2.commons.RequestPaths;
import com.kyleruss.hssa2.server.entity.UserKeys;
import com.kyleruss.hssa2.server.entity.Users;
import com.kyleruss.hssa2.server.entityfac.UserKeysFacade;
import com.kyleruss.hssa2.server.entityfac.UsersFacade;
import com.kyleruss.hssa2.server.web.app.CryptoController;
import com.kyleruss.hssa2.server.web.app.MailController;
import com.kyleruss.hssa2.server.web.app.ServerKeyManager;
import com.kyleruss.hssa2.server.web.app.UserManager;
import com.kyleruss.hssa2.server.web.util.ActionResponse;
import com.kyleruss.hssa2.server.web.util.ServletUtils;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;
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
    
    @EJB
    private UserKeysFacade userKeysFacade;
    
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
                processUserListRequest(request, response);
                break;
            case RequestPaths.PROFILE_UP_REQ:
                processProfileImageUploadRequest(request, response);
                break;
            case RequestPaths.SERV_DISCON_REQ:
                processUserDisconnect(request, response);
                break;
            default: break;
        }
    }
    
    private void processProfileImageUploadRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException  
    {
        try
        {
            String keyParam                     =   request.getParameter("authKey");
            String dataParam                    =   request.getParameter("clientData");
            EncryptedSession encSession         =   new EncryptedSession(keyParam, dataParam, 
                                                    ServerKeyManager.getInstance().getServerPrivateKey(), true);
            encSession.unlock();
            String decData                      =   new String(encSession.getData());
            JsonObject requestObj               =   ServletUtils.parseJsonInput(decData);
            JsonObject responseObj              =   ServletUtils.createAuthResponseObjFromInput(requestObj);
            byte[] imageData                    =   Base64.getDecoder().decode(requestObj.get("imageData").getAsString());
            String imageName                    =   "";
            
            do imageName                        =   CryptoUtils.generateRandomString(6, CryptoUtils.ALPHA_NUMERIC) + ".jpg";
            while(usersFacade.imageExists(imageName));
            
            String imagePath                    =   getServletConfig().getServletContext().getRealPath("WEB-INF") + "/resources/" + imageName;
            try(BufferedOutputStream outputStream   =   new BufferedOutputStream(new FileOutputStream(imagePath)))
            {
                outputStream.write(imageData);
                responseObj.addProperty("status", true);
                responseObj.addProperty("statusMessage", "Profile image successfully uploaded");
                responseObj.addProperty("imageName", imageName);
            }
            
            Users user                          =   usersFacade.find(requestObj.get("userID").getAsString());
            UserKeys usersKey                   =   userKeysFacade.getKeyForUser(user);
            Key userPublicKey                   =   CryptoUtils.stringToAsymKey(usersKey.getPubKey(), false, true);
            String encryptedResponse            =   CryptoController.getInstance().publicEncrypt(responseObj.toString(), userPublicKey);
            response.getWriter().write(encryptedResponse);
        }
        
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private void processUserListRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException 
    {
        try
        {
            JsonObject inputObj                 =   ServletUtils.getPublicEncryptedClientJson(request, 
                                                    ServerKeyManager.getInstance().getServerPrivateKey());
            JsonObject responseObj              =   ServletUtils.createAuthResponseObjFromInput(inputObj);
            List<Users> users                   =   usersFacade.getUsersInList(UserManager.getInstance().getUsers());
            String userListJson                 =   new Gson().toJson(users);
            responseObj.addProperty("userList", userListJson);
            Users user                          =   usersFacade.find(inputObj.get("userID").getAsString());
            UserKeys usersKey                   =   userKeysFacade.getKeyForUser(user);
            Key userPublicKey                   =   CryptoUtils.stringToAsymKey(usersKey.getPubKey(), false, true);
            EncryptedSession encSession         =   new EncryptedSession(responseObj.toString().getBytes("UTF-8"), userPublicKey);
            JsonObject encResponseObj           =   ServletUtils.prepareKeySessionResponse(encSession);
            ServletUtils.jsonResponse(response, encResponseObj);    
        }
        
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private void processUserPasswordRequest(HttpServletRequest request, HttpServletResponse response)
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
