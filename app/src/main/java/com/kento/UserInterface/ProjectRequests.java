package com.kento.UserInterface;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.kento.devbookandroidclient.R;
import com.example.kento.devbookandroidclient.Resources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProjectRequests extends Activity {

    private final String REQUESTSERROR = "We could not retrieve the project requests";

    private ListView projectList;
    private ArrayAdapter<String> projectAdapter;
    private String userName;
    private Requests[] projectIDS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_requests);

        projectList = (ListView) findViewById(R.id.requestList);
        userName = getIntent().getStringExtra(Resources.USERNAME);

        projectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ProjectRequests.this, RequestDetails.class);
                intent.putExtra("Requestid", projectIDS[position].getId());
                intent.putExtra("Requestadmin", projectIDS[position].getAdmin());
                intent.putExtra("RequestuserName", projectIDS[position].getUserName());
                startActivity(intent);
            }
        });
        getRequests();

    }



    public void getRequests(){

        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Retrieving Requests");
        progress.setMessage("Please Wait while we retrieve project requests");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        String url = Resources.SERVER_WEB_APP_PROJECT + Resources.GET_REQUESTS;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Map<String,String> params = new HashMap<>();
        params.put(Resources.ADMIN, userName);
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
                    JSONArray requestsStr = (JSONArray) jsonObject.get("requestsStr");
                    JSONArray requestsIDS = (JSONArray) jsonObject.get("requestsID");

                    String[] temp = new String[requestsStr.length()];
                    projectIDS = new Requests[requestsIDS.length()];
                    for(int i = 0; i < requestsStr.length(); i++){
                        JSONObject tempJson = requestsIDS.getJSONObject(i);

                        int id = tempJson.getInt("id");
                        String admin = tempJson.getString("admin");
                        String userName = tempJson.getString("userName");
                        projectIDS[i] = new Requests(id, admin, userName);
                        temp[i] = (String) requestsStr.get(i);
                    }

                    setItems(temp);

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

    private void setItems(String[] requests){
        projectAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, requests);
        projectList.setAdapter(projectAdapter);
    }
}
