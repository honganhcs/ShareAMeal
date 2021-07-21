package com.example.shareameal.notifications;

import android.app.Activity;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareameal.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class NotificationsSender {
    private String fcmToken, title, body;
    private Context ctx;
    private Activity activity;

    private RequestQueue requestQueue;

    public NotificationsSender(String fcmToken, String title, String body, Context ctx, Activity activity) {
        this.fcmToken = fcmToken;
        this.title = title;
        this.body = body;
        this.ctx = ctx;
        this.activity = activity;
    }


    public void sendNotification() {

        this.requestQueue = Volley.newRequestQueue(activity);

        JSONObject obj = new JSONObject();
        try {
           obj.put("to", this.fcmToken);
           JSONObject notification = new JSONObject();
           notification.put("title", this.title);
           notification.put("body", this.body);

           obj.put("notification", notification);

           JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, activity.getString(R.string.post_URL), obj,
                   new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {

               @Override
               public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key = " + activity.getString(R.string.firebase_server_key));
                    return header;
               }
           };

           requestQueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
