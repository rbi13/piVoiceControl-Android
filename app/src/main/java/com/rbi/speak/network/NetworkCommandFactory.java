package com.rbi.speak.network;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rbi.speak.storage.Logger;
import java.io.UnsupportedEncodingException;


import static java.net.URLEncoder.encode;

/**
 * Created by ron on 13/11/14.
 */
public class NetworkCommandFactory {

    public static JsonObjectRequest getVoiceCommand(String command,String url,VolleyRequestListener listener) throws UnsupportedEncodingException{

        String urlGet = url+"/echo/"+ encode(command, "UTF-8").replaceAll("\\+","%20");
        Logger.log(urlGet);
        JsonObjectRequest request =
                new JsonObjectRequest(Request.Method.GET,urlGet,null,listener,listener);

        // eliminate retries and give longer
        request.setRetryPolicy(new DefaultRetryPolicy(
                60000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return request;
    }
}
