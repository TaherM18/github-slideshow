package com.example.fimsdelivery;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chaos.view.PinView;
import com.example.fimsdelivery.connection.Connection;
import com.example.fimsdelivery.global.Global;
import com.example.fimsdelivery.sharedpreferences.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VerifyOtpActivity extends AppCompatActivity {

    TextView txtMobile;
    Button btnVerify;
    ImageButton btnClose;
    PinView pinView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        txtMobile = findViewById(R.id.txtMobile);
        btnVerify = findViewById(R.id.btnVerify);
        btnClose = findViewById(R.id.btnClose);
        pinView = findViewById(R.id.pinOTP);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pin = 0;
                try {
                    pin = Integer.parseInt(pinView.getText().toString());
                    if ( pin == Global.otp ) {
//                        Toast.makeText(VerifyOtpActivity.this, "OTP Verified", Toast.LENGTH_SHORT).show();
                        pinView.setError(null);

                        processDeliveryConfirmation();
                    }
                    else {
                        pinView.setError("Incorrect OTP");
                    }
                }
                catch (NumberFormatException e) {
                    Toast.makeText(VerifyOtpActivity.this, "OTP is required", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VerifyOtpActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

//    CONFIRM DELIVERY VIA API
    private void processDeliveryConfirmation() {
        StringRequest request = new StringRequest(Request.Method.POST, Connection.confirmDeliveryUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
                    if ( json.getString("status").equals("success") ) {
                        Toast.makeText(VerifyOtpActivity.this, "Delivery Confirmed", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(VerifyOtpActivity.this, MainActivity.class));
                        finish();
                    }
                    else {
                        Toast.makeText(VerifyOtpActivity.this, "Status: "+json.getString("status"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(VerifyOtpActivity.this, "JSONException: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(VerifyOtpActivity.this, "VolleyError: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("orderId", Global.orderId);

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(VerifyOtpActivity.this);
        requestQueue.add(request);
    }
}