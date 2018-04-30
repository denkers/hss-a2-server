//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.safesms.server.web.servlet;

import com.google.gson.JsonObject;
import com.kyleruss.hssa2.commons.CryptoCommons;
import com.kyleruss.hssa2.commons.CryptoUtils;
import com.kyleruss.hssa2.commons.EncryptedSession;
import com.kyleruss.hssa2.commons.RequestPaths;
import com.kyleruss.safesms.server.entity.Users;
import com.kyleruss.safesms.server.entityfac.UserKeysFacade;
import com.kyleruss.safesms.server.entityfac.UsersFacade;
import com.kyleruss.safesms.server.web.app.ServerKeyManager;
import com.kyleruss.safesms.server.web.util.ServletUtils;
import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Map.Entry;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "KeyServlet", urlPatterns = 
{
    RequestPaths.USER_PUBLIC_GET_REQ,
    RequestPaths.USER_PUBLIC_SEND_REQ,
    RequestPaths.SERV_PUBLIC_GET_REQ
}) 
public class KeyServlet extends HttpServlet
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
     **/
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException 
    {
        String path =   request.getServletPath();
        
        switch(path)
        {
            case RequestPaths.USER_PUBLIC_GET_REQ:
                processUserPublicGetRequest(request, response);
                break;
            case RequestPaths.USER_PUBLIC_SEND_REQ:
                processUserPublicSendRequest(request, response);
                break;
            default: break;
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException 
    {
       
        String path =   request.getServletPath();
        
        if(path.equals(RequestPaths.SERV_PUBLIC_GET_REQ))
            processServerPublicRequest(request, response); 
    }
    
    //Request handler for fetching server public key
    //Response is the Base64 encoded server public key
    private void processServerPublicRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        PublicKey publicKey     =   ServerKeyManager.getInstance().getServerPublicKey();
        JsonObject responseObj  =   new JsonObject();
        String encKey           =   Base64.getEncoder().encodeToString(publicKey.getEncoded());
        
        responseObj.addProperty("serverPublicKey", encKey);
        ServletUtils.jsonResponse(response, responseObj);
    }
    
    //Request handler for fetching other users public key
    //Gets the public key for the requested user if it exists
    //Request and response are authenticated 
    //Response is encrypted with AES where secret key is encrypted with the clients public key
    private void processUserPublicGetRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        try
        {
            EncryptedSession encSession         =   ServletUtils.decryptSessionRequest(request);
            JsonObject requestObj               =   ServletUtils.parseJsonInput(new String(encSession.getData()));
            JsonObject responseObj              =   ServletUtils.createAuthResponseObjFromInput(requestObj);
            String requestedUserID              =   requestObj.get("reqUserID").getAsString();
            String userID                       =   requestObj.get("userID").getAsString();
            
            //Get client and requested user records
            Users targetUser                    =   usersFacade.find(requestedUserID);
            Users user                          =   usersFacade.find(userID);
            
            //User and requested user exist
            if(targetUser != null && user != null)
            {
                
                //Get the public key for the requested user
                String reqKeyStr        =   userKeysFacade.getKeyForUser(targetUser).getPubKey();
                
                //Get the public key for the user
                //Used to encrypt the secret key
                String userKeyStr       =   userKeysFacade.getKeyForUser(user).getPubKey();
                byte[] userKeyDecoded   =   Base64.getDecoder().decode(userKeyStr);
                PublicKey userPublicKey =   (PublicKey) CryptoUtils.stringToAsymKey(userKeyDecoded, true);

                responseObj.addProperty("requestedKey", reqKeyStr);
                responseObj.addProperty("requestedUser", requestedUserID);
                
                //Encrypt message with AES where secret key is encrypted with clients public key
                EncryptedSession encresponse    =   new EncryptedSession(responseObj.toString().getBytes("UTF-8"), userPublicKey);
                JsonObject encResponseObj       =   ServletUtils.prepareKeySessionResponse(encresponse);
                
                ServletUtils.jsonResponse(response, encResponseObj);
            }
        }
        
        catch(Exception e)
        {
            System.out.println("[KEY_SERVLET_ERROR@processUserPublicGetRequest] " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
   
    //Request handler for users registration 
    //Processes a users request to create an account record and public key
    //The request consists of two components: authContents and clientData
    //authContents: contains the password and salt used to generate the ephemeral key in PBE
    //clientData: contains the registration contents i.e public key, name, phone id etc.
    //authContents is RSA encrypted with servers public key so decrypt with servers public key
    //clientData is AES encrypted with the ephemeral key
    //Request is also authenticated so response needs to contain auth values
    private void processUserPublicSendRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        try
        {
            //RSA Decrypt auth contents (salt & password) with servers private key
            JsonObject passSaltObj              =   ServletUtils.getPublicEncryptedClientJson(request, 
                                                    ServerKeyManager.getInstance().getServerPrivateKey(), "authContents");
            
            //MD5 Hash the salt and password to generate the 128bit key/iv length
            byte[] password                     =   CryptoCommons.generateHash(passSaltObj.get("password").getAsString().getBytes("UTF-8"));
            byte[] salt                         =   CryptoCommons.generateHash(passSaltObj.get("salt").getAsString().getBytes("UTF-8"));
            
            //Decrypt the data with the generated ephemeral key in AES using the password and salt
            byte[] data                         =   Base64.getDecoder().decode(request.getParameter("clientData").getBytes("UTF-8"));
            String decData                      =   CryptoCommons.pbeDecrypt(password, salt, data);
            
            JsonObject requestObj               =   ServletUtils.parseJsonInput(decData);
            JsonObject responseObj              =   ServletUtils.createAuthResponseObjFromInput(requestObj);
            
            //Read the registration contents
            String phoneID                      =   requestObj.get("phoneID").getAsString();
            String name                         =   requestObj.get("name").getAsString();
            String email                        =   requestObj.get("email").getAsString();
            String publicKey                    =   requestObj.get("publicKey").getAsString();
            byte[] publicKeyDecoded             =   Base64.getDecoder().decode(publicKey);
            
            //Attempt to create the user account
            //Successful if the record created exists in the entity manager
            Entry<Boolean, String> createUserResult     =   usersFacade.createUserAccount(phoneID, name, email);
            if(createUserResult.getKey())
            {
                //Create the public key record
                Users user                              =   usersFacade.find(phoneID);
                Entry<Boolean, String> createKeyResult  =   userKeysFacade.createUserKey(user, publicKey);
                
                responseObj.addProperty("status", createKeyResult.getKey());
                responseObj.addProperty("statusMessage", createKeyResult.getValue());
            }
            
            //Failed to create the user account
            else
            {
                responseObj.addProperty("status", false);
                responseObj.addProperty("statusMessage", createUserResult.getValue());
            }
            
            //Encrypt the response with AES where the 
            //secret key is encrypted with the clients public key
            PublicKey userKey           =   (PublicKey) CryptoUtils.stringToAsymKey(publicKeyDecoded, true);
            byte[] responseBytes        =   responseObj.toString().getBytes("UTF-8");
            EncryptedSession encSession =   new EncryptedSession(responseBytes, userKey);  
            JsonObject wrappedObj       =   ServletUtils.prepareKeySessionResponse(encSession);
            ServletUtils.jsonResponse(response, wrappedObj);
        }
        
        catch(Exception e)
        {
            System.out.println("[KEY_SERVLET_ERROR@processUserPublicSendRequest] " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
