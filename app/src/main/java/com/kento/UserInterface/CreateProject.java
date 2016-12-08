package com.kento.UserInterface;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kento.devbookandroidclient.R;
import com.example.kento.devbookandroidclient.Resources;

import java.util.HashMap;
import java.util.Map;

public class CreateProject extends Activity {

    private final String EMPTYFIELDS = "All non optional fields must be entered";
    private final String CREATEPROJECTERROR = "Failed to create project";
    private final String SUCCESS = "Project created successfully";

    private String admin;
    private EditText title;
    private EditText city;
    private EditText country;
    private EditText description;
    private EditText tools;
    private EditText languages;
    private Button createProject;
    private Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        admin = getIntent().getStringExtra(Resources.USERNAME);
        title = (EditText) findViewById(R.id.title);
        city = (EditText) findViewById(R.id.projectCity);
        country = (EditText) findViewById(R.id.projectCountry);
        description = (EditText) findViewById(R.id.description);
        tools = (EditText) findViewById(R.id.tools);
        languages = (EditText) findViewById(R.id.languages);

        createProject = (Button) findViewById(R.id.createProject);
        createProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(title.getText().toString().equals("") || city.getText().toString().equals("")
                        || country.getText().toString().equals("") || description.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),
                            EMPTYFIELDS, Toast.LENGTH_LONG).show();
                }
                else{
                    try {
                        createProject();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                                CREATEPROJECTERROR, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        cancel = (Button) findViewById(R.id.cancelProject);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

    }

    private void createProject() throws Exception{
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Please Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        String url = Resources.SERVER_WEB_APP_PROJECT + Resources.CREATE_REQUEST;
        RequestQueue queue = Volley.newRequestQueue(this);

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
                        CREATEPROJECTERROR, Toast.LENGTH_LONG).show();
                return;
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put(Resources.ADMIN, admin);
                params.put(Resources.PROJECTTITLE, title.getText().toString());
                params.put(Resources.CITY, city.getText().toString().toLowerCase());
                params.put(Resources.COUNTRY, country.getText().toString().toLowerCase());
                params.put(Resources.DESCRIPTION, description.getText().toString().toLowerCase());
                params.put(Resources.TOOLS, tools.getText().toString());
                params.put(Resources.LANGUAGES, languages.getText().toString());
                return params;
            }
        };
        hideKeyboard();
        queue.add(stringRequest);
        progress.dismiss();

    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) CreateProject.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(
                CreateProject.this.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
