//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.web.servlet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kyleruss.hssa2.commons.CryptoUtils;
import com.kyleruss.hssa2.commons.EncryptedSession;
import com.kyleruss.hssa2.commons.RequestPaths;
import com.kyleruss.hssa2.server.entity.UserKeys;
import com.kyleruss.hssa2.server.entity.Users;
import com.kyleruss.hssa2.server.entityfac.UserKeysFacade;
import com.kyleruss.hssa2.server.entityfac.UsersFacade;
import com.kyleruss.hssa2.server.web.app.MailController;
import com.kyleruss.hssa2.server.web.app.ServerKeyManager;
import com.kyleruss.hssa2.server.web.app.UserManager;
import com.kyleruss.hssa2.server.web.util.ActionResponse;
import com.kyleruss.hssa2.server.web.util.ServletUtils;
import java.io.IOException;
import java.security.Key;
import java.util.Base64;
import java.util.List;
import java.util.Map.Entry;
import javax.ejb.EJB;
import javax.persistence.Tuple;
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
    RequestPaths.SERV_DISCON_REQ,
    RequestPaths.USER_SETTINGS_SAVE
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
                
            case RequestPaths.SERV_DISCON_REQ:
                processUserDisconnect(request, response);
                break;
                
            case RequestPaths.USER_SETTINGS_SAVE:
                processAccountUpdateRequest(request, response);
                break;
                
            default: break;
        }
    }
    
    //Request handler for updating a user account
    //Edits the passed user account if it's found with the changed name, email, profile image etc.
    //Request is encrypted with AES and secret key is encrypted with the servers public key
    //Request and response need are authenticated
    private void processAccountUpdateRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException 
    {
        try
        {
            //Decrypt request with AES where secret key is decrypted with server private key
            EncryptedSession encSession     =   ServletUtils.decryptSessionRequest(request);
            JsonObject requestData          =   ServletUtils.parseJsonInput(new String(encSession.getData()));
            //Attach authentication values to response
            JsonObject responseObj          =   ServletUtils.createAuthResponseObjFromInput(requestData);
            
            //Read updated user data 
            //Profile image is omitted if there are no changes to it
            String userID                   =   requestData.get("userID").getAsString();
            Users user                      =   usersFacade.find(userID);
            String name                     =   requestData.get("name").getAsString();
            String email                    =   requestData.get("email").getAsString();
            String profileImage             =   requestData.has("profileImage")? requestData.get("profileImage").getAsString() : null;
            
            //Update user account with changed fields
            Entry<Boolean, String> result   =   usersFacade.updateUserAccount(user, name, email, profileImage);
            responseObj.addProperty("actionStatus", result.getKey());
            responseObj.addProperty("message", result.getValue());
            
            //Encrypt response with AES where the secret key is encrypted with the clients public key
            UserKeys usersKey                   =   userKeysFacade.getKeyForUser(user);
            byte[] publicKeyBytes               =   Base64.getDecoder().decode(usersKey.getPubKey().getBytes("UTF-8"));
            Key userPublicKey                   =   CryptoUtils.stringToAsymKey(publicKeyBytes, true);
            EncryptedSession encSessionResp     =   new EncryptedSession(responseObj.toString().getBytes("UTF-8"), userPublicKey);
            JsonObject encResponseObj           =   ServletUtils.prepareKeySessionResponse(encSessionResp);
            
            ServletUtils.jsonResponse(response, encResponseObj);
        }
        
        catch(Exception e)
        {
            System.out.println("[USER_SERVLET_ERROR@processAccountUpdateRequest] " + e.getMessage());
            ActionResponse errorResponse   =   new ActionResponse("Failed to update account", false);
            ServletUtils.jsonResponse(response, errorResponse);
        }
    }
    
    //Request handler for fetching the online user list
    //User can request to get the records of all online users
    //Request is encrypted with AES and secret key is encrypted with the servers public key
    //Request and response need are authenticated
    private void processUserListRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException 
    {
        try
        {
            //Decrypt request with AES where secret key is decrypted with server private key
            byte[] keyBytes                     =   Base64.getDecoder().decode(request.getParameter("key"));
            byte[] dataBytes                    =   Base64.getDecoder().decode(request.getParameter("data"));
            EncryptedSession encSession         =   new EncryptedSession(keyBytes, dataBytes, ServerKeyManager.getInstance().getServerPrivateKey());
            encSession.unlock();
            
            JsonObject requestObj               =   ServletUtils.parseJsonInput(new String(encSession.getData()));
            JsonObject responseObj              =   ServletUtils.createAuthResponseObjFromInput(requestObj);
            
            //Fetch user records for each of the online users
            List<Tuple> users                   =   usersFacade.getUsersInList(UserManager.getInstance().getUsers());
            
            //Add user records to the usersJArray to be passed to the client
            JsonObject userListObj              =   new JsonObject();
            JsonArray usersJArray               =   new JsonArray();
            for(Tuple user : users)
            {
                
                JsonObject userRecordObj    =   new JsonObject();
                userRecordObj.addProperty("name", (String) user.get("name"));
                userRecordObj.addProperty("phoneID", (String) user.get("id"));
                
                //omit the profile image if it is null
                //client assumes a default profile image if it is null/omitted
                byte[] profImgBytes =   (byte[]) user.get("profileImage");
                if(profImgBytes != null)
                {
                    String profileImg   =   Base64.getEncoder().encodeToString(profImgBytes);
                    userRecordObj.addProperty("profileImage", profileImg);
                }
                
                userRecordObj.addProperty("email", (String) user.get("email"));
                usersJArray.add(userRecordObj);
            }
            
            userListObj.add("users", usersJArray);
            userListObj.addProperty("userCount", usersJArray.size());
            responseObj.add("userList", userListObj);
            
            //Encrypt response with AES where the secret key is encrypted with the clients public key
            Users user                          =   usersFacade.find(requestObj.get("userID").getAsString());
            UserKeys usersKey                   =   userKeysFacade.getKeyForUser(user);
            byte[] publicKeyBytes               =   Base64.getDecoder().decode(usersKey.getPubKey().getBytes("UTF-8"));
            Key userPublicKey                   =   CryptoUtils.stringToAsymKey(publicKeyBytes, true);
            EncryptedSession encSessionResp     =   new EncryptedSession(responseObj.toString().getBytes("UTF-8"), userPublicKey);
            JsonObject encResponseObj           =   ServletUtils.prepareKeySessionResponse(encSessionResp);
            
            ServletUtils.jsonResponse(response, encResponseObj);    
        }
        
        catch(Exception e)
        {
            System.out.println("[USER_SERVLET_ERROR@processUserListRequest] " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    //Request handler for sending password/code to clients email 
    //Generate a random code to send to the client
    //Request is encrypted with AES and secret key is encrypted with the servers public key
    private void processUserPasswordRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException 
    {
        try
        {
            //Decrypt request with AES where secret key is decrypted with server private key
            byte[] keyBytes                 =   Base64.getDecoder().decode(request.getParameter("key"));
            byte[] dataBytes                =   Base64.getDecoder().decode(request.getParameter("data"));
            EncryptedSession encSession     =   new EncryptedSession(keyBytes, dataBytes, ServerKeyManager.getInstance().getServerPrivateKey());
            encSession.unlock();
            
            JsonObject dataObj  =   ServletUtils.parseJsonInput(new String(encSession.getData()));
            String userEmail    =   dataObj.getAsJsonPrimitive("email").getAsString();
            
            //Send the generated password to the clients email
            String password     =   CryptoUtils.generateRandomString(8, CryptoUtils.ALPHA_NUMERIC);
            MailController.getInstance().sendPasswordMail(userEmail, password); 
            
            ActionResponse resp =   new ActionResponse("An authentication code has been sent to your email", true);
            ServletUtils.jsonResponse(response, resp);
        }
        
        catch(Exception e)
        {
            System.out.println("[USER_SERVLET_ERROR@processUserPasswordRequest] " + e.getMessage());
            ActionResponse resp =   new ActionResponse("Failed to send authentication code ", false);
            ServletUtils.jsonResponse(response, resp);
        }
    }
    
    //Request handler for a client attempting to connect to the server
    //A client wanting to connect to the server will pass their phone ID
    //If the client exists allow them to connect and add them to the online user list
    //Request is encrypted with AES and secret key is encrypted with the servers public key
    //Request and response need are authenticated
    protected void processUserConnect(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException 
    {
        try
        {
            //Decrypt request with AES where secret key is decrypted with server private key
            EncryptedSession encSession         =   ServletUtils.decryptSessionRequest(request);
            JsonObject requestObj               =   ServletUtils.parseJsonInput(new String(encSession.getData()));
            JsonObject responseObj              =   ServletUtils.createAuthResponseObjFromInput(requestObj);
            
            String phoneID      =   requestObj.get("phoneID").getAsString();
            Users user          =   usersFacade.find(phoneID);
            
            
            //Check if user exists, if so add them to the online user list
            if(user != null)
            {
                UserManager.getInstance().addUser(phoneID);
                responseObj.addProperty("status", true);
                responseObj.addProperty("statusMessage", "Successfully connected");

                //Add the users record information (clients only store their phone id)
                responseObj.addProperty("name", user.getName());
                responseObj.addProperty("email", user.getEmail());
                responseObj.addProperty("phoneID", user.getId());
                
                //Include profile image if it is not null
                byte[] imgBytes     =   user.getProfileImage();
                if(imgBytes != null) 
                {
                    String profileImage =    Base64.getEncoder().encodeToString(imgBytes);
                    responseObj.addProperty("profileImage", profileImage);
                }
                
                
                //Encrypt response with AES where the secret key is encrypted with the clients public key
                UserKeys usersKey                   =   userKeysFacade.getKeyForUser(user);
                byte[] publicKeyBytes               =   Base64.getDecoder().decode(usersKey.getPubKey().getBytes("UTF-8"));
                Key userPublicKey                   =   CryptoUtils.stringToAsymKey(publicKeyBytes, true);
                EncryptedSession encSessionResp     =   new EncryptedSession(responseObj.toString().getBytes("UTF-8"), userPublicKey);
                JsonObject encResponseObj           =   ServletUtils.prepareKeySessionResponse(encSessionResp);
                
                ServletUtils.jsonResponse(response, encResponseObj);
            }
            
            //User not found, don't allow connection
            else
            {
                ActionResponse actionResponse   =   new ActionResponse("Failed to connect to server", false);
                ServletUtils.jsonResponse(response, actionResponse);
            }
            
        }
        
        catch(Exception e)
        {
            System.out.println("[USER_SERVLET_ERROR@processUserConnect] " + e.getMessage());
            ActionResponse actResponse  =   new ActionResponse("Failed to connect to server", false);
            ServletUtils.jsonResponse(response, actResponse);
        }
    }
    
    //Request handler for client disconnection
    //Request contains the ID of the client and if it exists, remove them from the online users list
    //Request is encrypted with AES and secret key is encrypted with the servers public key
    protected void processUserDisconnect(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException 
    {
        try
        {
            //Decrypt request with AES where secret key is decrypted with server private key
            EncryptedSession encSession     =   ServletUtils.decryptSessionRequest(request);
            JsonObject requestObj           =   ServletUtils.parseJsonInput(new String(encSession.getData()));
            String phoneID                  =   requestObj.getAsJsonPrimitive("phoneID").getAsString();
            
            boolean disconnectStatus        =   false;
            String disconnectResponse;
            
            //Check if user exists
            if(usersFacade.find(phoneID) != null)
            {
                //User is not currently online
                if(!UserManager.getInstance().containsUser(phoneID))
                {
                    disconnectStatus     =   false;
                    disconnectResponse    =   "Failed to disconnect: You are not currently connected";
                }
                
                //User is online, remove them from the online user list
                else
                {
                    UserManager.getInstance().removeUser(phoneID);
                    disconnectStatus     =   true;
                    disconnectResponse   =   "Successfully disconnected from server";
                }
            }
            
            //User record doesn't exist, deny disconnection
            else disconnectResponse  =   "Failed to disconnect: User ID not found";
            
            ActionResponse actResponse  =   new ActionResponse(disconnectResponse, disconnectStatus);
            ServletUtils.jsonResponse(response, actResponse);
        }
        
        catch(Exception e)
        {
            System.out.println("[USER_SERVLET_ERROR@processUserDisconnect] " + e.getMessage());
            ActionResponse actResponse  =   new ActionResponse("Failed to disconnect from the server", false);
            ServletUtils.jsonResponse(response, actResponse);
        }
    }
}
