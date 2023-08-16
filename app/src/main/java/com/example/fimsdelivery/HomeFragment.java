package com.example.fimsdelivery;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fimsdelivery.adapter.PendingOrdersAdapter;
import com.example.fimsdelivery.connection.Connection;
import com.example.fimsdelivery.model.Order;
import com.example.fimsdelivery.sharedpreferences.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;
    ArrayList<Order> pendingOrdersList = new ArrayList<>();

    TextView txtDoneCount, txtPendingCount, txtUsername;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.pending_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        txtDoneCount = view.findViewById(R.id.txtDoneCount);
        txtPendingCount = view.findViewById(R.id.txtPendingCount);
        txtUsername = view.findViewById(R.id.textView);

//        Order o1 = new Order("#1","2 items", "₹ 2700.00", "A-101, Swapna Srushti Society, Daboli char rasta", "UPI");
//        Order o2 = new Order("#2","4 items", "₹ 4800.00", "E-704, Burhani Park, Jiav Budia Road, Bhestan char rasta", "Card");
//        Order o3 = new Order("#3","6 items", "₹ 6900.00", "B-204, Tulsi Society, Katargam char rasta", "COD");
//        pendingOrdersList.add(o1);
//        pendingOrdersList.add(o2);
//        pendingOrdersList.add(o3);
//        pendingOrdersList.add(o1);
//        pendingOrdersList.add(o2);
//        pendingOrdersList.add(o3);


        processOrderCount();

        processPendingOrders();

        DividerItemDecoration DIDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(DIDecoration);

        return view;
    }

    //    METHOD FOR ORDER COUNT
    private void processOrderCount() {
        StringRequest request = new StringRequest(Request.Method.POST, Connection.orderCountUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject json = new JSONObject(response);

                            if (json.get("status").equals("success")) {
                                txtUsername.setText("Hi, " + json.getString("firstName"));
                                txtDoneCount.setText(json.getString("doneCount"));
                                txtPendingCount.setText(json.getString("pendingCount"));
                            }
                        }
                        catch (JSONException e) {
                            Toast.makeText(getContext(), "JSONException (OrderCount) : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "VolleyError (OrderCount) : "+error.getMessage(), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                SessionManager sessionManager = new SessionManager(getContext());
                int userId = sessionManager.getUserIdFromSession();
                map.put("userId", String.valueOf(userId));
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    //    METHOD FOR PENDING ORDERS LIST
    private void processPendingOrders() {
        StringRequest request = new StringRequest(Request.Method.POST, Connection.pendingOrdersUrl,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.get("status").equals("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            Order order = new Order();
                            JSONObject json = jsonArray.getJSONObject(i);
                            order.setOrderId(json.getString("orderId"));
                            order.setOrderAddress(json.getString("streetAddress"));
                            order.setOrderPrice(json.getString("totalOrderPrice"));
                            order.setPayMode(json.getString("paymentType"));
                            order.setTotalQuantity(json.getString("totalQuantity"));

                            pendingOrdersList.add(order);
                        }

//                    SET RECYCLER VIEW ADAPTER
                        recyclerView.setAdapter(new PendingOrdersAdapter(pendingOrdersList, getContext()));
                    }
                    else {
                        Toast.makeText(getContext(), "PendingOrders Status :\n\n"+jsonObject.getString("status"), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e) {
                    Toast.makeText(getContext(), "JSONException (PendingOrder) :\n\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "VolleyError (PendingOrder) :\n\n"+error.getMessage(), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                SessionManager sessionManager = new SessionManager(getContext());
                int userId = sessionManager.getUserIdFromSession();
                map.put("userId", String.valueOf(userId));
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
}