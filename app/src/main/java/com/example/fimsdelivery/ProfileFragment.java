package com.example.fimsdelivery;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextInputEditText edtFname, edtAddress, edtVehicle, edtLicense, edtContact, edtEmail, edtPass, edtWorkTime;
    TextView viewName;
    Button btnEdit;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        btnEdit = view.findViewById(R.id.btnEdit);

        viewName = view.findViewById(R.id.viewName);
        edtFname = view.findViewById(R.id.edtFname);
        edtContact = view.findViewById(R.id.edtCont);
        edtAddress = view.findViewById(R.id.edtAddress);
        edtVehicle = view.findViewById(R.id.edtVehicle);
        edtLicense = view.findViewById(R.id.edtLicense);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPass = view.findViewById(R.id.edtPass);
        edtWorkTime = view.findViewById(R.id.edtWorkTime);

        processData();

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), EditProfileActivity.class));
            }
        });

        return view;
    }

    private void processData() {
        StringRequest request = new StringRequest(Request.Method.POST, Connection.profileUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
                    if (json.get("status").equals("success")) {
                        viewName.setText(json.getString("fullName"));
                        edtContact.setText(json.getString("contact"));
                        String addr = json.getString("streetAddress") + ", " + json.getString("city") + ", " + json.getString("pincode");
                        edtAddress.setText(addr);
                        edtEmail.setText(json.getString("email"));
                        edtVehicle.setText(json.getString("vehicleNo"));
                        edtLicense.setText(json.getString("licenseNo"));
                        if (json.getString("jobType").equals("P")) {
                            edtWorkTime.setText("Part-Time (4 hours, 4pm to 8pm)");
                        } else {
                            edtWorkTime.setText("Full-Time (10 hours, 10am to 8pm)");
                        }
                    } else {
                        Toast.makeText(getContext(), "Status: " + json.getString("status"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "VolleyError: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                SessionManager sessionManager = new SessionManager(getContext());
                int userId = sessionManager.getUserIdFromSession();
                map.put("userId", String.valueOf(userId));

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);
    }
}