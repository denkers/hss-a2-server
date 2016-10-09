//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.web.servlet;

import com.google.gson.JsonObject;
import com.kyleruss.hssa2.commons.EncryptedSession;
import com.kyleruss.hssa2.commons.RequestPaths;
import com.kyleruss.hssa2.server.web.app.ServerKeyManager;
import com.kyleruss.hssa2.server.web.util.ServletUtils;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "KeyServlet", urlPatterns = 
{
    RequestPaths.SERV_KEY_REQ,
    RequestPaths.PUBLIC_SEND_REQ,
    RequestPaths.PUBLIC_GET_REQ
})
public class KeyServlet extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException 
    {
        String path =   request.getServletPath();
        
        if(path.equals(RequestPaths.PUBLIC_GET_REQ))
            processServerPublicRequest(request, response);
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
            case RequestPaths.SERV_KEY_REQ:
            case RequestPaths.PUBLIC_SEND_REQ:
        }
    }
    
    private void processServerPublicRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        PublicKey publicKey     =   ServerKeyManager.getInstance().getServerPublicKey();
        JsonObject responseObj  =   new JsonObject();
        String encKey           =   Base64.getEncoder().encodeToString(publicKey.getEncoded());
        
        responseObj.addProperty("serverPublicKey", encKey);
        ServletUtils.jsonResponse(response, responseObj);
    }
}
