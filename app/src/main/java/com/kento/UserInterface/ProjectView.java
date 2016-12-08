package com.kento.UserInterface;

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
import com.example.kento.devbookandroidclient.Encryption;
import com.example.kento.devbookandroidclient.R;
import com.example.kento.devbookandroidclient.Resources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProjectView extends AppCompatActivity {
    //TODO Fix screen titles
    private String PROJECTSERROR = "Failed to reterieve project";
    private String SUCCESS = "Your request was sent successfully";
    private String  REQUESTERROR = "We could not send your request";

    private TextView title;
    private TextView admin;
    private TextView description;
    private TextView tools;
    private TextView languages;
    private String id;
    private Button request;
    private String adminEncrypted;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_view);

        title = (TextView) findViewById(R.id.projectTitle);
        admin = (TextView) findViewById(R.id.projectAdmin);
        description = (TextView) findViewById(R.id.projectDescription);
        tools = (TextView) findViewById(R.id.projectTools);
        languages = (TextView) findViewById(R.id.projectLanguages);
        id = getIntent().getStringExtra("id");
        userName = getIntent().getStringExtra("userName");
        getProject();

        request = (Button) findViewById(R.id.requestBtn);
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                request.setClickable(false);
                String url = Resources.SERVER_WEB_APP_PROJECT + Resources.REQUEST_ADD;
                RequestQueue queue = Volley.newRequestQueue(ProjectView.this);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(getApplicationContext(),
                                        SUCCESS, Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),
                                REQUESTERROR, Toast.LENGTH_LONG).show();
                        finish();
                    }
                }){
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<>();
                        params.put("id", id);
                        params.put("admin", adminEncrypted);
                        params.put("userName", userName);
                        return params;
                    }
                };
                queue.add(stringRequest);
                finish();
            }
        });
    }

    private void getProject(){
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Retrieving Project");
        progress.setMessage("Please Wait while we retrieve the specified project");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        String url = Resources.SERVER_WEB_APP_PROJECT + Resources.PROJECTS_BY_ID_REQUEST;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Map<String,String> params = new HashMap<>();
        params.put("id", id);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, params, this.createRequestSuccessListener(), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);
        progress.dismiss();
    }

    private Response.Listener<JSONObject> createRequestSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response1 = response.getJSONObject("project");
                    try {
                        setFields(response1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),
                            PROJECTSERROR, Toast.LENGTH_LONG).show();
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
                        PROJECTSERROR, Toast.LENGTH_LONG).show();
                finish();
            }
        };
    }

    private void setFields(JSONObject obj) throws Exception {
        Encryption encrypter = Encryption.getDefaultEncrypter();
        title.setText("Project title: " + (String) obj.get("name"));
        adminEncrypted = obj.getString("admin");
        admin.setText("Project admin: " + encrypter.decrypt(obj.getString("admin")));
        description.setText("Project description: " + (String) obj.get("description"));
        tools.setText("Required tools (e.g. github, IDE, etc...): " + (String) obj.get("tools"));
        languages.setText("Required languages (e.g. Java, C++, etc...): " + (String) obj.get("languages"));
    }
}
