package com.kento.UserInterface;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ProjectChat extends Activity {

    private final String MESSAGESERROR = "Could not reterieve chat history";
    private final String ADDMESSAGEFAIL = "Failed to add message to the database, Please try again";
    private TextView messages;
    private EditText chatBox;
    private Button send;
    private String userName;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_chat);

        messages = (TextView) findViewById(R.id.textView3);
        chatBox = (EditText) findViewById(R.id.chatBox);
        send = (Button) findViewById(R.id.sendMessage);

        id = getIntent().getIntExtra("id", 0);
        userName = getIntent().getStringExtra(Resources.USERNAME);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String url = Resources.SERVER_WEB_APP_PROJECT + Resources.ADD_MESSAGE;
                RequestQueue queue = Volley.newRequestQueue(ProjectChat.this);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                messages.setText(messages.getText().toString() + chatBox.getText().toString());
                                chatBox.setText("");
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),
                                ADDMESSAGEFAIL, Toast.LENGTH_LONG).show();
                    }
                }){
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<>();
                        params.put("id", Integer.toString(id));
                        params.put("message", chatBox.getText().toString());
                        params.put(Resources.USERNAME, userName);
                        return params;
                    }
                };
                queue.add(stringRequest);

            }
        });

        getMessages();
    }

    private void getMessages(){
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Retrieving Requests");
        progress.setMessage("Please Wait while we retrieve project requests");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        String url = Resources.SERVER_WEB_APP_PROJECT + Resources.MESSAGES;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Map<String,String> params = new HashMap<>();
        params.put("id", Integer.toString(id));
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, params, this.createRequestSuccessListener(), this.createRequestErrorListener());

        requestQueue.add(jsObjRequest);
        progress.dismiss();
    }

    private Response.Listener<JSONObject> createRequestSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    messages.setText(response.getString("messages"));

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),
                            MESSAGESERROR, Toast.LENGTH_LONG).show();

                }
            }
        };
    }

    private Response.ErrorListener createRequestErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        MESSAGESERROR, Toast.LENGTH_LONG).show();

            }
        };
    }
}
