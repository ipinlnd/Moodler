package com.nlnd.moodler;
 import java.io.DataInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
 public final class CommunicationBreakdown
{
    private static final String SERVER_ADDRESS = "http://192.168.1.56:8080";
     private static HttpURLConnection makeReady(URL url)
    {
        HttpURLConnection urlConnection;
        try
        {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(15*1000);
            urlConnection.connect();
            return urlConnection;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
     private static String getResponse(HttpURLConnection urlConnection)
    {
        DataInputStream br;
        try
        {
            String response = "";
            int bytesRead;
            byte[] bytes = new byte[1000];
            br = new DataInputStream(urlConnection.getInputStream());
            while ((bytesRead = br.read(bytes)) > 0)
                response += new String(bytes, 0, bytesRead, "UTF-8");
            return response;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
     public static int signUp(String username, String password)
    {
        try
        {
            URL url = new URL(SERVER_ADDRESS + "/users/add/" + username);
            String response;
            response = getResponse(Objects.requireNonNull(makeReady(url)));
            LoginActivity.userId = response;
            url = new URL(SERVER_ADDRESS + "/users/set/password/" + response + "/" + password);
            response = getResponse(Objects.requireNonNull(makeReady(url)));
             if (response == null)
                return 2;
            else if (response.equals("OK"))
                return 0;
            else if(response.equals("1"))
                return 1;
            else
                return 2;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
         return 2;
    }
     public static boolean login(String username, String password)
    {
        try
        {
            URL url = new URL(SERVER_ADDRESS + "/users/authenticate/" + username + "/" + password);
            String response = getResponse(Objects.requireNonNull(makeReady(url)));
            if (response == null)
                return false;
            if (response.equals("Failure"))
                return false;
            LoginActivity.userId = response;
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
}