package com.example.fimsdelivery;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    TextInputLayout lytFname, lytCont, lytEmail, lytLicense, lytVehicle, lytPincode;
    TextInputEditText edtFname, edtStreetAddress, edtPincode, edtVehicle, edtLicense, edtContact, edtEmail, edtPass;
    RadioButton rdoPart, rdoFull;
    Button btnUpdate;
    Spinner spnCity;

    String Fname, address, pincode, vehicle, license, contact, email, jobType;

    String[] cityArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        btnUpdate = findViewById(R.id.btnUpdate);

        edtFname = findViewById(R.id.edtFname);
        edtContact = findViewById(R.id.edtCont);
        edtStreetAddress = findViewById(R.id.edtStreetAddress);
        spnCity = findViewById(R.id.spnCity);
        edtPincode = findViewById(R.id.edtPincode);
        edtVehicle = findViewById(R.id.edtVehicleNo);
        edtLicense = findViewById(R.id.edtLicense);
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);
        rdoPart = findViewById(R.id.rdoPart);
        rdoFull = findViewById(R.id.rdoFull);

        lytFname = findViewById(R.id.lytFname);
        lytCont = findViewById(R.id.lytCont);
        lytEmail = findViewById(R.id.lytEmail);
        lytLicense = findViewById(R.id.lytLicense);
        lytVehicle = findViewById(R.id.lytVehicle);
        lytPincode = findViewById(R.id.lytPincode);

        cityData();

        processData();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateAll()) {
                    Fname = edtFname.getText().toString();
                    contact = edtContact.getText().toString();
                    email = edtEmail.getText().toString();
                    license = edtLicense.getText().toString();
                    vehicle = edtVehicle.getText().toString();
                    address = edtStreetAddress.getText().toString();
                    pincode = edtPincode.getText().toString();
                    if (rdoFull.isChecked()) {
                        jobType = "F";
                    } else {
                        jobType = "P";
                    }

                    processUpdate();
                }
            }
        });

        edtFname.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateFname();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtContact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateContact();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateEmail();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtLicense.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateLicense();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

//    METHOD TO RESTART ACTIVITY
    public static void restartActivity(Activity activity){
        if (Build.VERSION.SDK_INT >= 11) {
            activity.recreate();
        } else {
            activity.finish();
            activity.startActivity(activity.getIntent());
        }
    }

    //    GET PROFILE DATA FROM DB VIA API
    private void processData() {
        StringRequest request = new StringRequest(Request.Method.POST, Connection.profileUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("status").equals("success")) {
                        edtFname.setText(json.getString("fullName"));
                        edtContact.setText(json.getString("contact"));
                        edtStreetAddress.setText(json.getString("streetAddress"));
                        edtPincode.setText(json.getString("pincode"));
                        edtEmail.setText(json.getString("email"));
                        edtVehicle.setText(json.getString("vehicleNo"));
                        edtLicense.setText(json.getString("licenseNo"));

                        if (json.getString("jobType").equals("P")) {
                            rdoPart.setActivated(true);
                        } else {
                            rdoFull.setActivated(true);
                        }
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Status: " + json.getString("status"), Toast.LENGTH_SHORT).show();
                        ;
                    }
                } catch (JSONException e) {
                    Toast.makeText(EditProfileActivity.this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EditProfileActivity.this, "VolleyError: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        ;
                    }
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                SessionManager sessionManager = new SessionManager(EditProfileActivity.this);
                int userId = sessionManager.getUserIdFromSession();
                map.put("userId", String.valueOf(userId));

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(EditProfileActivity.this);
        requestQueue.add(request);
    }

    //    GET CITY LIST FROM API
    public void cityData() {

        StringRequest request = new StringRequest(Request.Method.GET, Connection.cityUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.get("status").equals("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

//                    CONVERT JSONArray to array
                        ArrayList<String> list = new ArrayList<String>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            list.add(jsonArray.getString(i));
                        }
                        cityArray = list.toArray(new String[list.size()]);

                        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, cityArray);
                        spnCity.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        );

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    //    UPDATE PROFILE DATA THROUGH API
    public void processUpdate() {
        Toast.makeText(this, "processUpdate() called", Toast.LENGTH_SHORT).show();
        StringRequest request = new StringRequest(Request.Method.POST, Connection.profileUpdateUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(EditProfileActivity.this, "Response:\n\n"+response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject json = new JSONObject(response);

                            if (json.get("status").equals("success")) {

                                Toast.makeText(EditProfileActivity.this, "Edit Success", Toast.LENGTH_SHORT);

                                restartActivity(EditProfileActivity.this);
                            }
                            else {
                                Toast.makeText(EditProfileActivity.this, "Status: " + json.getString("status"), Toast.LENGTH_SHORT);
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(EditProfileActivity.this, "JSONException: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EditProfileActivity.this, "VolleyError: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("editProfile", error.getMessage());
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                SessionManager sessionManager = new SessionManager(EditProfileActivity.this);
                int userId = sessionManager.getUserIdFromSession();
                map.put("userId", String.valueOf(userId));
                map.put("fName", Fname);
                map.put("contact", contact);
                map.put("email", email);
                map.put("jobType", jobType);
                map.put("licenseNo", license);
                map.put("vehicleNo", vehicle);
                map.put("address", address);
                map.put("pincode", pincode);
                map.put("city", cityArray[spnCity.getSelectedItemPosition()]);

                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(EditProfileActivity.this);
        requestQueue.add(request);
    }

//    VALIDATION METHODS

    private boolean validateFname() {
        String fname = edtFname.getText().toString();
        if (fname.isEmpty()) {
            lytFname.setError("Full Name is required");
            return false;
        } else {
            lytFname.setError(null);
            return true;
        }
    }

    private boolean validateContact() {
        String contact = edtContact.getText().toString();
        String contPattern = "[6-9][0-9]{9}";
        if (contact.isEmpty()) {
            lytCont.setError("Contact is required");
            return false;
        } else if (!contact.matches(contPattern)) {
            lytCont.setError("Invalid contact");
            return false;
        } else {
            lytCont.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        String email = edtEmail.getText().toString();
        String emailPattern = "^[^.][a-zA-Z0-9._!#$%&'*+/=?^_`{|}~-]+@{1}(?:gmail.com|yahoo.com|yahoo.in|utu.ac.in|hotmail.in)$";
        if (email.isEmpty()) {
            lytEmail.setError("Email is required");
            return false;
        } else if (!email.matches(emailPattern)) {
            lytEmail.setError("invalid email");
            return false;
        } else {
            lytEmail.setError(null);
            return true;
        }
    }

    private boolean validatePincode() {
        String pincode = edtPincode.getText().toString();
        String pinPattern = "[1-9]{1}[0-9]{5}";
        if (pincode.isEmpty()) {
            lytPincode.setError("Pincode is required");
            return false;
        } else if (!pincode.matches(pinPattern)) {
            lytPincode.setError("Invalid pincode");
            return false;
        } else {
            lytPincode.setError(null);
            return true;
        }
    }

    private boolean validateLicense() {
        String licPattern = "[A-Z]{2}[0-9]{2}(?:19[0-9]{2}|20[0-9]{2})[0-9]{7}";
        license = edtLicense.getText().toString();
        if (!license.matches(licPattern)) {
            lytLicense.setError("Invalid License Number");
            return false;
        } else {
            lytLicense.setError(null);
            lytLicense.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateAll() {
        if (!validateFname() || !validateEmail() || !validateContact() || !validatePincode() || !validateLicense()) {
            Toast.makeText(this, "Solve error to update", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}