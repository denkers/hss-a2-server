//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.hssa2.server.web.servlet;

import com.google.gson.JsonObject;
import com.kyleruss.hssa2.server.web.app.CryptoController;
import com.kyleruss.hssa2.server.web.app.ServerKeyManager;
import com.kyleruss.hssa2.server.web.util.ServletUtils;
import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "KeyServlet", urlPatterns = {"/KeyServlet"})
public class KeyServlet extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException 
    {
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
        
    }
    
    private void processServerPublicRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        PublicKey publicKey =   ServerKeyManager.getInstance().getServerPublicKey();
        byte[] pubKeyBytes  =   publicKey.getEncoded();
        String enc          =   Base64.getEncoder().encodeToString(pubKeyBytes);
        JsonObject jObj     =   new JsonObject();
        jObj.addProperty("authPubKey", enc);
        
        ServletUtils.jsonResponse(response, jObj);
    }
}
