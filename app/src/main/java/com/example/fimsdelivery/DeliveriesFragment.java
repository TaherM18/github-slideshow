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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fimsdelivery.adapter.NewOrdersAdapter;
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
 * Use the {@link DeliveriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeliveriesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;
    ArrayList<Order> ordersList = new ArrayList<>();

    public DeliveriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeliveriesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeliveriesFragment newInstance(String param1, String param2) {
        DeliveriesFragment fragment = new DeliveriesFragment();
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
        View view =  inflater.inflate(R.layout.fragment_deliveries, container, false);
        recyclerView = view.findViewById(R.id.deliveries_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        processRequest();

        recyclerView.setAdapter(new NewOrdersAdapter(getContext(), ordersList));

        DividerItemDecoration DIDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(DIDecoration);

        return view;

    }

//    GET DELIVERIES LIST FROM API
    public void  processRequest() {
        StringRequest request = new StringRequest(Request.Method.POST, Connection.ordersUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.get("status").equals("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            Order order = new Order();
                            JSONObject orderDetails = jsonArray.getJSONObject(i);
                            order.setOrderId(orderDetails.getString("orderId"));
                            order.setOrderPrice("₹ " + orderDetails.getString("totalOrderPrice"));
                            order.setOrderAddress(orderDetails.getString("deliveryAddress"));
                            order.setTotalQuantity(orderDetails.getString("totalQuantity"));
                            order.setPayMode(orderDetails.getString("paymentType"));

                            ordersList.add(order);
                        }
                        NewOrdersAdapter customAdapter = new NewOrdersAdapter(getContext(), ordersList);
                        recyclerView.setAdapter(customAdapter);
                    }
                    else {
                        Toast.makeText(getContext(), "Status : "+jsonObject.getString("status"), Toast.LENGTH_LONG).show();
                    }
                }
                catch (JSONException e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
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