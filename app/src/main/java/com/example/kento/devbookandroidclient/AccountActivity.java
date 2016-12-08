package com.example.kento.devbookandroidclient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.os.Bundle;
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
import com.kento.UserInterface.UserMainActivity;

import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends Activity {

    //TODO logout if app is destroyed
    private final String EMPTYFIELDS = "All fields must be entered";
    private final String INVALIDLOGIN = "Invalid userName or password";
    private final String LOGINERROR = "Failed to login";

    private EditText userName;
    private EditText password;
    private Button login;
    private Button createAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        userName = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(userName.getText().equals("") || password.getText().equals("")){
                    Toast.makeText(getApplicationContext(),
                            EMPTYFIELDS, Toast.LENGTH_LONG).show();
                }
                else if(userName.getText().length() < 4 || password.getText().length() < 8){
                    Toast.makeText(getApplicationContext(),
                            INVALIDLOGIN, Toast.LENGTH_LONG).show();
                }
                else{
                    try {
                        performLogin();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                                LOGINERROR, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        createAccount = (Button) findViewById(R.id.createAccount);
        createAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(AccountActivity.this, CreateAccountActivity.class);
                startActivity(intent);
            }
        });
    }

    private void performLogin() throws Exception {

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Please Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        String url = Resources.SERVER_WEB_APP_NAME + Resources.LOGIN_REQUEST;
        RequestQueue queue = Volley.newRequestQueue(this);
        final String userNameEncrypted = Resources.issueToken(userName.getText().toString());
        final String passwordEncrypted = Resources.issueToken(password.getText().toString());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        Intent intent = new Intent(AccountActivity.this, UserMainActivity.class);
                        intent.putExtra(Resources.SERVER_RESPONSE, response);
                        intent.putExtra(Resources.USERNAME, userNameEncrypted);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                Toast.makeText(getApplicationContext(),
                        INVALIDLOGIN, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put(Resources.USERNAME,userNameEncrypted);
                params.put(Resources.PASSWORD,passwordEncrypted);
                return params;
            }
        };
        hideKeyboard();
        queue.add(stringRequest);
    }


    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) AccountActivity.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(
                AccountActivity.this.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
