package com.example.fimsdelivery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.fimsdelivery.global.Global;
import com.example.fimsdelivery.model.Order;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeliveryDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeliveryDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    protected LocationManager locationManager;
    protected LocationListener locationListener;

    static int PERMISSION_CODE = 69;

    private double latitude, longitude;

    SupportMapFragment smf;
    FusedLocationProviderClient client;

    MaterialCardView cardPhone, cardAddress;
    TextView txtOrderId, txtOrderStatus, txtOrderPrice, txtOrderDateTime, txtPayMode, txtItemQtyName, txtCustomerName, txtCustomerPhone, txtCustomerAddress, txtFee, txtSubTotalAmount;

    Button btnConfirm;

    String streetAddress;

    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;

    public DeliveryDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeliveryDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeliveryDetailsFragment newInstance(String param1, String param2) {
        DeliveryDetailsFragment fragment = new DeliveryDetailsFragment();
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


            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

            // method to get the location
            getLastLocation();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_delivery_details, container, false);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        client = LocationServices.getFusedLocationProviderClient(getActivity());

        cardPhone = view.findViewById(R.id.cardPhone);
        cardAddress = view.findViewById(R.id.cardAddress);

        txtOrderId = view.findViewById(R.id.txtOrderId);
        txtOrderPrice = view.findViewById(R.id.txtOrderPrice);
        txtOrderStatus = view.findViewById(R.id.txtOrderStatus);
        txtOrderDateTime = view.findViewById(R.id.txtOrderDateTime);
        txtPayMode = view.findViewById(R.id.txtPayMode);
        txtItemQtyName = view.findViewById(R.id.txtItemQtyName);
        txtCustomerName = view.findViewById(R.id.txtCustomerName);
        txtCustomerPhone = view.findViewById(R.id.txtCustomerPhone);
        txtCustomerAddress = view.findViewById(R.id.txtCustomerAddress);
        txtFee = view.findViewById(R.id.txtFee);
        txtSubTotalAmount = view.findViewById(R.id.txtTotalAmount);

        btnConfirm = view.findViewById(R.id.btnDeliveryConfirm);

        processDeliveryDetails();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_CODE);
        }

        cardPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iDial = new Intent(Intent.ACTION_DIAL);
                iDial.setData(Uri.parse("tel: " + txtCustomerPhone.getText().toString()));
                startActivity(iDial);
            }
        });

        cardAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dexter.withContext(getContext())
                        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                String source = String.valueOf(latitude) + "," + String.valueOf(longitude);

                                Toast.makeText(getContext(), source, Toast.LENGTH_SHORT).show();

                                Uri uri = Uri.parse(Connection.googleMapsUrl + source + "/" + streetAddress);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                intent.setPackage("com.google.android.apps.maps");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                Toast.makeText(getContext(), "Location permission is required", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                processOTP();
            }
        });

        return view;
    }

//  GET DELIVERY DETAILS FROM API
    private void processDeliveryDetails() {
        StringRequest request = new StringRequest(Request.Method.POST, Connection.orderDetailsUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
//                    Toast.makeText(getContext(), Global.orderId, Toast.LENGTH_SHORT).show();

                    if (json.get("status").equals("success")) {

                        txtOrderId.setText(json.getString("orderId"));
                        txtOrderStatus.setText(json.getString("orderStatus"));
                        txtOrderPrice.setText("₹ " + json.getString("totalOrderAmount"));
                        txtOrderDateTime.setText(json.getString("orderDateTime"));
                        txtPayMode.setText(json.getString("payMode"));
                        txtItemQtyName.setText(json.getString("quantityXName"));
                        txtCustomerName.setText(json.getString("fullName"));
                        txtCustomerPhone.setText(json.getString("contact"));
                        Global.contact = json.getString("contact");
                        txtCustomerAddress.setText(json.getString("fullAddress"));
                        txtFee.setText("₹ " + json.getString("deliveryFee"));
                        txtSubTotalAmount.setText("₹ " + json.getString("subTotalPrice"));

                        if (txtOrderStatus.getText().toString().equals("Successful")) {
                            txtOrderStatus.setBackgroundColor(Color.parseColor("#26d07c")); // GREEN COLOR
                        }

                        streetAddress = (txtCustomerAddress.getText().toString().split(", ", 2))[1];
//                        Toast.makeText(getContext(), streetAddress, Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getContext(), "Status : " + json.getString("status"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getContext(), "JSONException : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "VolleyError : "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("orderId", Global.orderId);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

//    GET OTP FROM API
    private void processOTP() {
        StringRequest request = new StringRequest(Request.Method.POST, Connection.generateOtpUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);

                    if (json.getInt("status") == 200) {
                        Global.otp = json.getInt("otp");

                        Toast.makeText(getContext(), "OTP Sent", Toast.LENGTH_SHORT).show();
//                                START ACTIVITY
                        startActivity(new Intent(getContext(), VerifyOtpActivity.class));
                    } else {
                        Toast.makeText(getContext(), "Status: "+json.getString("status"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "JSONException: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Volley: "+error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("contact", Global.contact);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                });
            } else {
                Toast.makeText(getContext(), "Please turn on your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

}