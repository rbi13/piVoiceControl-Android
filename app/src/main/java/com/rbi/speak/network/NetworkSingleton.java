package com.rbi.speak.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


/**
 * Created by ron on 13/11/14.
 */
public class NetworkSingleton {

    private static NetworkSingleton singleton = null;

    private Context context;
    private RequestQueue requestQueue;

    public static NetworkSingleton getInstance(Context context){

        if(singleton == null){
            singleton = new NetworkSingleton(context);
        }

        return singleton;
    }

    private NetworkSingleton(Context context){

        this.context = context;
        this.requestQueue = getRequestQueue();

    }

    public <T> void addRequest(Request<T> request){

        requestQueue.add(request);
    }

    public RequestQueue getRequestQueue() {

        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context);
        }

        return requestQueue;
    }
}
