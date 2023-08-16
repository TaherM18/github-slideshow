package com.example.fimsdelivery;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    TextInputLayout lytFname, lytBdate, lytCont, lytEmail, lytLicense, lytVehicle, lytPass, lytConfirm, lytPincode;
    TextInputEditText edtFname, edtBdate, edtCont, edtEmail, edtLicense, edtVehicle, edtCity, edtAddress, edtPincode, edtPass, edtConfirm;
    Spinner spnCity;
    RadioGroup rdoGroupWorkTime;
    Button btnReg, btnOldUser;
    String status = "", fname, cont, email, license, vehicle, address, pincode, city, pass, jobType;
    String[] cityArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        method to retrieve json array of city names
        cityData();

//  Initialization
        edtFname = findViewById(R.id.edtFname);
        edtCont = findViewById(R.id.edtCont);
        edtEmail = findViewById(R.id.edtEmail);
        edtLicense = findViewById(R.id.edtLicense);
        edtVehicle  = findViewById(R.id.edtVehicle);
        edtAddress = findViewById(R.id.edtAddress);
        edtPincode = findViewById(R.id.edtPincode);
        spnCity = findViewById(R.id.spnCity);
        edtPass = findViewById(R.id.edtPass);
        edtConfirm = findViewById(R.id.edtConfirm);
        rdoGroupWorkTime = findViewById(R.id.rdoGrpWorkTime);
        btnReg = findViewById(R.id.btnReg);
        btnOldUser = findViewById(R.id.btnOldUser);

        lytFname = findViewById(R.id.lytFname);
        lytCont = findViewById(R.id.lytCont);
        lytEmail = findViewById(R.id.lytEmail);
        lytLicense = findViewById(R.id.lytLicense);
        lytVehicle = findViewById(R.id.lytVehicle);
        lytPass = findViewById(R.id.lytPass);
        lytConfirm = findViewById(R.id.lytConfirm);
        lytPincode = findViewById(R.id.lytPincode);

        btnOldUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
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

        edtCont.addTextChangedListener(new TextWatcher() {
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

        edtPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validatePass();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateConfirm();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//        Registration button onClick Listener
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fname = edtFname.getText().toString();
                cont = edtCont.getText().toString();
                email = edtEmail.getText().toString();
                license = edtLicense.getText().toString();
                vehicle = edtVehicle.getText().toString();
                address = edtAddress.getText().toString();
                pincode = edtPincode.getText().toString();
                int index = spnCity.getSelectedItemPosition();
                city = cityArray[index];
                pass = edtPass.getText().toString();

                switch ( rdoGroupWorkTime.getCheckedRadioButtonId() ) {
                    case R.id.rdoPart:
                        jobType = "P";
                        break;
                    case R.id.rdoFull:
                        jobType = "F";
                        break;
                }

                if ( validateAll() ) {
                    processData();
                }
            }
        });
    }

    //  processData Method
    public void processData () {

        StringRequest request = new StringRequest(Request.Method.POST, Connection.registerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
                    status = json.getString("status");

                    if (status.equals("success")) {
                        Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(i);
                    }
                    else if (status.equals("registered")) {
                        Toast.makeText(RegisterActivity.this, "Already Registered", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "JSON: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Volley: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("fName", fname);
                map.put("mobile", cont);
                map.put("email", email);
                map.put("jobType", jobType);
                map.put("licenseNo", license);
                map.put("vehicleNo", vehicle);
                map.put("address", address);
                map.put("pincode", pincode);
                map.put("city", city);
                map.put("pass", pass);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }


    public void cityData () {

        StringRequest request = new StringRequest(Request.Method.GET, Connection.cityUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.get("status").equals("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

//                      CONVERT JSONArray to array
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


    private boolean validateFname() {
        String namePattern = "[A-Z][a-z]{0,29}\\s[A-Z][a-z]{0,29}";
        fname = edtFname.getText().toString();
        if (fname.isEmpty()) {
            lytFname.setError("Full Name is required");
            return false;
        } else if (!fname.matches(namePattern)) {
            lytFname.setError("Invalid name format");
            return false;
        }
        else {
            lytFname.setError(null);
            lytFname.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateContact() {
        String contact = edtCont.getText().toString();
        String contPattern = "[6-9][0-9]{9}";
        if (contact.isEmpty()) {
            lytCont.setError("Contact is required");
            return false;
        }
        else if (!contact.matches(contPattern)) {
            lytCont.setError("Invalid contact");
            return false;
        }
        else {
            lytCont.setError(null);
            lytCont.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePincode() {
        String pin = edtPincode.getText().toString();
        String pinPattern = "[1-9]{1}[0-9]{5}";
        if (pin.isEmpty()) {
            lytPincode.setError("Pincode is required");
            return false;
        }
        else if (!pin.matches(pinPattern)) {
            lytPincode.setError("Invalid pincode");
            return false;
        }
        else {
            lytPincode.setError(null);
            lytPincode.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEmail() {
        String email = edtEmail.getText().toString();
        String emailPattern = "^[^.][a-zA-Z0-9._!#$%&'*+/=?^_`{|}~-]+@{1}(?:gmail.com|yahoo.com|yahoo.in|utu.ac.in|hotmail.in)";
        if (email.isEmpty()) {
            lytEmail.setError("Email is required");
            return false;
        }
        else if (!email.matches(emailPattern)) {
            lytEmail.setError("invalid email");
            return false;
        }
        else {
            lytEmail.setError(null);
            lytEmail.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateLicense() {
        String licPattern = "[A-Z]{2}[0-9]{2}(?:19[0-9]{2}|20[0-9]{2})[0-9]{7}";
        license = edtLicense.getText().toString();
        if (!license.matches(licPattern)) {
            lytLicense.setError("Invalid License Number");
            return false;
        }
        else {
            lytLicense.setError(null);
            lytLicense.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateVehicle() {
        String vehiclePattern = "[A-Z]{2}(?:19[0-9]{2}|20[0-9]{2})[0-9]{7}";
        vehicle = edtVehicle.getText().toString();
        if (!vehicle.matches(vehiclePattern)) {
            lytVehicle.setError("Invalid License Number");
            return false;
        }
        else {
            lytVehicle.setError(null);
            lytVehicle.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePass() {
        String pass = edtPass.getText().toString();
        String passPattern = "[a-zA-Z0-9\\\\!\\\\@\\\\#\\\\$]{8,10}";
        if (pass.isEmpty()) {
            lytPass.setError("Password is required");
            return false;
        }
        else if (!pass.matches(passPattern)) {
            lytPass.setError("Password must have a letter, number, and special (!,@,#,$)\nMin length 8 and max 10");
            return false;
        }
        else {
            lytPass.setError(null);
            lytPass.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateConfirm () {
        String pass = edtPass.getText().toString();
        String confirm = edtConfirm.getText().toString();
        if (confirm.isEmpty()) {
            lytConfirm.setError("Confirmation is required");
            return false;
        }
        else if (!pass.equals(confirm)) {
            lytConfirm.setError("Password doesn't match");
            return false;
        }
        else {
            lytConfirm.setError(null);
            lytConfirm.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateAll() {
        if (!validateFname() || !validateContact() || !validatePincode() || !validateEmail() || !validateLicense() || !validateVehicle() || !validatePass() || !validateConfirm()) {
            Toast.makeText(this, "Solve error to register", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }
}