package com.example.kento.devbookandroidclient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends Activity {

    private final String EMPTYFIELDS = "All non optional fields must be entered";
    private final String INVALIDINFO = "Invalid entries";
    private final String CREATEACCOUNTERROR = "Failed to create account";
    private final String SUCCESS = "Account created successfully";
    private final String PASSWORDERROR = "Password do not match";

    //TODO fix so that keyboard does not cover material
    private EditText userName;
    private EditText password;
    private EditText confirmPassword;
    private EditText email;
    private EditText city;
    private EditText country;
    private EditText bio;
    private Button createAccount;
    private Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        userName = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);
        city = (EditText) findViewById(R.id.city);
        country = (EditText) findViewById(R.id.country);
        bio = (EditText) findViewById(R.id.bio);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);

        createAccount = (Button) findViewById(R.id.createAccount);
        createAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(userName.getText().toString().equals("") || password.getText().toString().equals("")
                        || email.getText().toString().equals("") || city.getText().toString().equals("")
                        || country.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),
                            EMPTYFIELDS, Toast.LENGTH_LONG).show();
                }
                else if(userName.getText().length() < 4 || password.getText().length() < 8
                        || !email.getText().toString().contains("@")){
                    Toast.makeText(getApplicationContext(),
                            INVALIDINFO, Toast.LENGTH_LONG).show();
                }
                else if(!password.getText().toString().equals(confirmPassword.getText().toString())){
                    Toast.makeText(getApplicationContext(),
                            PASSWORDERROR, Toast.LENGTH_LONG).show();
                }
                else{
                    try {
                        createAccount();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                                CREATEACCOUNTERROR, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
    }

    private void createAccount() throws Exception {
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Please Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        String url = Resources.SERVER_WEB_APP_NAME + Resources.CREATE_REQUEST;
        RequestQueue queue = Volley.newRequestQueue(this);
        final String userNameEncrypted = Resources.issueToken(userName.getText().toString());
        final String passwordEncrypted = Resources.issueToken(password.getText().toString());
        final String emailEncrypted = Resources.issueToken(email.getText().toString());

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
                        CREATEACCOUNTERROR, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put(Resources.USERNAME,userNameEncrypted);
                params.put(Resources.PASSWORD,passwordEncrypted);
                params.put(Resources.EMAIL,emailEncrypted);
                params.put(Resources.CITY, city.getText().toString().toLowerCase());
                params.put(Resources.COUNTRY, country.getText().toString().toLowerCase());
                params.put(Resources.BIO, bio.getText().toString());
                return params;
            }
        };
        hideKeyboard();
        queue.add(stringRequest);
        progress.dismiss();
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) CreateAccountActivity.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(
                CreateAccountActivity.this.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
