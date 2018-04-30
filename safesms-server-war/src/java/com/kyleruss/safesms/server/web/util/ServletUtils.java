//======================================
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems A2
//======================================

package com.kyleruss.safesms.server.web.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kyleruss.hssa2.commons.CryptoCommons;
import com.kyleruss.hssa2.commons.EncryptedSession;
import com.kyleruss.safesms.server.web.app.ServerKeyManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;

public class ServletUtils 
{
    //Returns a JSON string of the passed data
    //responseData must be serializable 
    public static String getJsonResponseString(Object responseData)
    {
        if(responseData instanceof JsonObject)
            return ((JsonObject) responseData).toString();
        
        Gson gson           =   new Gson();
        String jsonResponse =   responseData == null? "" : gson.toJson(responseData);
        return jsonResponse;
    }
    
    //Writes a json response for the passed data
    public static void jsonResponse(HttpServletResponse response, Object responseData) 
    throws ServletException, IOException
    {
        String jsonResponse =   getJsonResponseString(responseData);
        response.setContentType("application/json");        
        response.getWriter().write(jsonResponse);
    }
    
    //Decrypts the session request message where the passed
    //key is RSA encrypted with the servers public key and is thus decrypted with the server private key
    //The data is AES encrypted and is decrypted with the decrypted key
    //See com.kyleruss.hssa2.commons.EncryptedSession
    public static EncryptedSession decryptSessionRequest(String key, String data) 
    throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
       byte[] keyBytes                     =   Base64.getDecoder().decode(key);
       byte[] dataBytes                    =   Base64.getDecoder().decode(data);
       EncryptedSession encSession         =   new EncryptedSession(keyBytes, dataBytes, ServerKeyManager.getInstance().getServerPrivateKey());
       encSession.unlock();

       return encSession;
    }
    
    //See ServletUtils@decryptSessionRequest
    //Uses default key and data param names
    public static EncryptedSession decryptSessionRequest(HttpServletRequest request) 
    throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
        return decryptSessionRequest(request.getParameter("key"), request.getParameter("data"));
    }
    
    //Prepares a JsonObject response message from the passed encrypted session message
    //Encrypts the data and then the key
    //Passes the key and data as response params
    public static JsonObject prepareKeySessionResponse(EncryptedSession enc) 
    throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException
    {
        enc.initCipher(Cipher.ENCRYPT_MODE);
        String data             =   Base64.getEncoder().encodeToString(enc.processData());
        String key              =   Base64.getEncoder().encodeToString(enc.encryptKey());
        JsonObject responseObj  =   new JsonObject();
        
        responseObj.addProperty("key", key);
        responseObj.addProperty("data", data);
        return responseObj;
    }
    
    //Generates a authenticated response message from the request
    //Many client requests include a request ID and nonce to authenticate the response
    //These values need to be attached to the response
    public static JsonObject createAuthResponseObjFromInput(JsonObject requestObj)
    {
        JsonObject responseObj  =   new JsonObject();
        String nonce            =   requestObj.get("nonce").getAsString();
        String reqID            =   requestObj.get("requestID").getAsString();
        responseObj.addProperty("nonce", nonce);
        responseObj.addProperty("requestID", reqID);
        return responseObj;
    }
    
    //Parses the input json string and returns its JsonObject
    public static JsonObject parseJsonInput(String json)
    {
        return new JsonParser().parse(json).getAsJsonObject();
    }
    
    //Returns the default client data json string
    public static String getClientJson(HttpServletRequest request)
    {
        return request.getParameter("clientData");
    }
    
    //Decrypts and returns the JsonObject from a request
    //Data in the request is decrypted using RSA
    //key: the public/private RSA key to decrypt the request with
    //paramName: the request parameter name 
    public static JsonObject getPublicEncryptedClientJson(HttpServletRequest request, Key key, String paramName) 
    throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, 
    IllegalBlockSizeException, InvalidKeyException, BadPaddingException
    {
        byte[] data         =   Base64.getDecoder().decode(request.getParameter(paramName));
        String decData      =   CryptoCommons.publicDecrypt(data, key);
        JsonObject dataObj  =   ServletUtils.parseJsonInput(decData);
        
        return dataObj;
    }
    
    //See ServletUtils@getPublicEncryptedClientJson(HttpServletRequest, Key, String) 
    //Uses the default data param name 
    public static JsonObject getPublicEncryptedClientJson(HttpServletRequest request, Key key) 
    throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, 
    IllegalBlockSizeException, InvalidKeyException, BadPaddingException
    {
        return getPublicEncryptedClientJson(request, key, "clientData");
    }
}
