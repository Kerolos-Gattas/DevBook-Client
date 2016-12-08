package com.kento.UserInterface;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kento.devbookandroidclient.R;
import com.example.kento.devbookandroidclient.Resources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RequestDetails extends Activity {

    private final String REQUESTSERROR = "We could not retrieve project details";
    private final String PROCESSSUCCESS = "Proccess completed";
    private final String FAILEDTOPROCCESSREQUEST = "Failed to proccess your request";

    private TextView userName;
    private TextView experience;
    private Button accept;
    private Button refuse;
    private int id;
    private String admin;
    private String userNameStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);

        userName = (TextView) findViewById(R.id.requestUserName);
        experience = (TextView) findViewById(R.id.requestExperience);
        accept = (Button) findViewById(R.id.acceptRequest);
        refuse = (Button) findViewById(R.id.refuseRequest);

        id = getIntent().getIntExtra("Requestid", 0);
        admin = getIntent().getStringExtra("Requestadmin");
        userNameStr = getIntent().getStringExtra("RequestuserName");

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                processRequest(true);
            }
        });

        refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                processRequest(false);
            }
        });

        getInfo();
    }

    private void processRequest(boolean accept){
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Processing.");
        progress.setMessage("Please Wait while process your request");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        String url = Resources.SERVER_WEB_APP_PROJECT;

        if(accept)
            url += Resources.ACCEPT_REQUEST;
        else
            url += Resources.REFUSE_REQUEST;

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(),
                                PROCESSSUCCESS, Toast.LENGTH_LONG).show();
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        FAILEDTOPROCCESSREQUEST, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("id",Integer.toString(id));
                params.put(Resources.ADMIN, admin);
                params.put(Resources.USERNAME, userNameStr);
                return params;
            }
        };
        queue.add(stringRequest);
        progress.dismiss();
    }

    private void getInfo(){
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Retrieving User Info.");
        progress.setMessage("Please Wait while we retrieve the details of the request");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        String url = Resources.SERVER_WEB_APP_PROJECT + Resources.GET_USER_INFO;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Map<String,String> params = new HashMap<>();
        params.put(Resources.USERNAME, userNameStr);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, params, this.createRequestSuccessListener(), this.createRequestErrorListener());

        requestQueue.add(jsObjRequest);
        progress.dismiss();
    }

    private Response.Listener<JSONObject> createRequestSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = response;
                    userName.setText(jsonObject.getString(Resources.USERNAME));
                    experience.setText(jsonObject.getString(Resources.BIO));

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),
                            REQUESTSERROR, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        };
    }

    private Response.ErrorListener createRequestErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        REQUESTSERROR, Toast.LENGTH_LONG).show();
                finish();
            }
        };
    }
}
