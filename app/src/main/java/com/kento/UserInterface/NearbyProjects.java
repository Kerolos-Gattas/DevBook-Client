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
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kento.devbookandroidclient.Encryption;
import com.example.kento.devbookandroidclient.R;
import com.example.kento.devbookandroidclient.Resources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NearbyProjects extends Activity {

    private final String PROJECTSERROR = "Failed to retrieve projects";
    private String userName;
    private ListView projectList;
    private ArrayAdapter<String> projectAdapter;
    private int[] ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_projects);

        userName = getIntent().getStringExtra(Resources.USERNAME);
        projectList = (ListView) findViewById(R.id.projectList);
        projectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleSelection(parent, view, position, id);
            }
        });
        getProjects();
    }

    private void handleSelection(AdapterView<?> parent, View view, int position, long id){
        Intent intent = new Intent(NearbyProjects.this, ProjectView.class);
        intent.putExtra("id", Integer.toString(ids[position]));
        intent.putExtra("userName", userName);
        startActivity(intent);
    }

    private void getProjects(){
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Retrieving Projects");
        progress.setMessage("Please Wait while we retrieve projects near you");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        String url = Resources.SERVER_WEB_APP_PROJECT + Resources.NEARBY_PROJECTS_REQUEST;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Map<String,String> params = new HashMap<>();
        params.put(Resources.USERNAME, userName);
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
                    JSONArray projects = (JSONArray) jsonObject.get("projects");
                    addProjects(projects);
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

    private void addProjects(JSONArray projects){
        Encryption encrypter = Encryption.getDefaultEncrypter();
        String[] projectsarray = new String[projects.length()];
        ids = new int[projects.length()];
        try {
            for(int i = 0; i < projects.length(); i++){
                JSONObject tempJson = projects.getJSONObject(i);

                String admin = encrypter.decrypt(tempJson.getString("admin"));
                String projectTitle = tempJson.getString("name");
                projectsarray[i] = "Project Title: " + projectTitle + "\n" + "Project admin: " +admin;
                ids[i] = tempJson.getInt("id");
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    PROJECTSERROR, Toast.LENGTH_LONG).show();
            finish();
        }

        projectAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, projectsarray);
        projectList.setAdapter(projectAdapter);
    }
}
