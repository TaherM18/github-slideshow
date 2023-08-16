package com.example.fimsdelivery;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fimsdelivery.connection.Connection;
import com.example.fimsdelivery.sharedpreferences.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText edtEmail, edtPass;
    TextInputLayout lytEmail, lytPass;
    CheckBox chkRemember;
    Button btnLogin, btnNewUser;
    short count = 3;
    String email = "", pass = "", status = "";
    int userId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams);

        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);
        lytEmail = findViewById(R.id.lytEmail);
        lytPass = findViewById(R.id.lytPass);
        chkRemember = findViewById(R.id.chkRemember);
        btnLogin = findViewById(R.id.btnLogin);
        btnNewUser = findViewById(R.id.btnNewUser);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                processData();
            }
        });

        btnNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });

    }

    //  processData Method
    public void processData() {

        StringRequest request = new StringRequest(Request.Method.POST, Connection.loginUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);

                    if (json.getString("status").equals("success")) {
                        userId = json.getInt("userId");

//                        PUT userId IN SESSION (SHARED PREFERENCES)
                        if (chkRemember.isChecked()) {
                            SessionManager sessionManager = new SessionManager(LoginActivity.this);
                            sessionManager.putUserId(userId);
                        }

                        Toast.makeText(getApplicationContext(), json.getString("status"), Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                    else {
                        if (count == 0) {
                            Toast.makeText(LoginActivity.this, "Limit exceeded for login attempts.\nKindly contact your administrator.", Toast.LENGTH_LONG).show();
                            btnLogin.setEnabled(false);
                        } else if (count > 0) {
                            count--;
                            Toast.makeText(LoginActivity.this, "Invalid credentials\n" + count + " attempts left", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("email", edtEmail.getText().toString());
                map.put("pass", edtPass.getText().toString());
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(request);


    }
}