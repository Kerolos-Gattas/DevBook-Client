package com.kento.UserInterface;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class UserMainActivity extends AppCompatActivity {

    private final String LOGOUTERROR = "Failed to Logout";
    private final String PROJECTSERROR = "Failed to load current projects";
    //TODO screen title
    //TODO back button
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView projectList;
    private ArrayAdapter<String> projectAdapter;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    private String logoutCredentials;
    private String userName;
    private String[] projectTitles;
    private int[] projectIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        logoutCredentials = getIntent().getStringExtra(Resources.SERVER_RESPONSE);
        userName = getIntent().getStringExtra(Resources.USERNAME);

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        projectList = (ListView) findViewById(R.id.projectsUsersList);

        projectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(UserMainActivity.this, ProjectChat.class);
                intent.putExtra("id", projectIds[position]);
                intent.putExtra(Resources.USERNAME, userName);
                startActivity(intent);
            }
        });

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleSelection(parent, view, position, id);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        addDrawerItems();
        setupDrawer();
        getCurrentProjects();
    }

    private void getCurrentProjects(){
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Retrieving Requests");
        progress.setMessage("Please Wait while we retrieve project requests");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        String url = Resources.SERVER_WEB_APP_PROJECT + Resources.CURRENT_PROJECTS;
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
                    JSONArray projectTitle = (JSONArray) jsonObject.get("titles");
                    JSONArray projectIDS = (JSONArray) jsonObject.get("ids");
                    projectIds = new int[projectTitle.length()];
                    projectTitles = new String[projectTitle.length()];

                    for(int i = 0; i < projectTitle.length(); i++){
                        projectIds[i] = (int) projectIDS.get(i);
                        projectTitles[i] = (String) projectTitle.get(i);
                    }

                    setList();

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

    private void setList(){
        projectAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, projectTitles);
        projectList.setAdapter(projectAdapter);
    }

    private void handleSelection(AdapterView<?> parent, View view, int position, long id){
        if(position == 3){
            logOut();
        }
        else if(position == 1){
            Intent intent = new Intent(UserMainActivity.this, CreateProject.class);
            intent.putExtra(Resources.USERNAME, userName);
            startActivity(intent);
        }
        else if(position == 0){
            Intent intent = new Intent(UserMainActivity.this, NearbyProjects.class);
            intent.putExtra(Resources.USERNAME, userName);
            startActivity(intent);
        }
        else if(position == 2){
            Intent intent = new Intent(UserMainActivity.this, ProjectRequests.class);
            intent.putExtra(Resources.USERNAME, userName);
            startActivity(intent);
        }
    }

    private void logOut(){
        String url = Resources.SERVER_WEB_APP_NAME + Resources.LOGOUT_REQUEST;
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        LOGOUTERROR, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put(Resources.LOGIUTCREDENTIALS, logoutCredentials);
                params.put(Resources.USERNAME, userName);
                return params;
            }
        };
        queue.add(stringRequest);
        finish();
    }

    private void addDrawerItems() {
        //TODO projects and availability
        String[] actions = { "Available projects near you", "Post a new project",
                "Project requests", "Logout"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, actions);
        mDrawerList.setAdapter(mAdapter);
    }

    private void setupDrawer() {
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
