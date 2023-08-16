package com.example.fimsdelivery.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fimsdelivery.DeliveryDetailsFragment;
import com.example.fimsdelivery.MainActivity;
import com.example.fimsdelivery.R;
import com.example.fimsdelivery.connection.Connection;
import com.example.fimsdelivery.global.Global;
import com.example.fimsdelivery.model.Order;
import com.example.fimsdelivery.sharedpreferences.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewOrdersAdapter extends RecyclerView.Adapter<NewOrdersAdapter.Holder> {

    private Context context;
    ArrayList<Order> orderList = new ArrayList<>();
    Activity activity;

    public NewOrdersAdapter(Context context, ArrayList<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.new_order_list_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.txtOrderId.setText(orderList.get(position).getOrderId());
        if (Integer.parseInt(orderList.get(position).getTotalQuantity()) == 1) {
            holder.txtItemQty.setText(orderList.get(position).getTotalQuantity()+" item");
        }
        else {
            holder.txtItemQty.setText(orderList.get(position).getTotalQuantity()+" items");
        }
        holder.txtAddress.setText(orderList.get(position).getOrderAddress());
        holder.txtPrice.setText(orderList.get(position).getOrderPrice());
        holder.txtPayMode.setText(orderList.get(position).getPayMode());

//        holder.image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Global.orderId = holder.txtOrderId.getText().toString();
//                Intent intent = new Intent(context, MainActivity.class);
//                intent.putExtra("orderId", Global.orderId);
//                context.startActivity(intent);
//            }
//        });

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processDeliveryAcceptance("Y", holder.txtOrderId.getText().toString());
            }
        });

//        holder.btnReject.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                processDeliveryAcceptance("N", holder.txtOrderId.getText().toString());
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    //    INNER CLASS
    public class Holder extends RecyclerView.ViewHolder {

        TextView txtOrderId, txtItemQty, txtAddress, txtPrice, txtPayMode;
        ImageView image;
        Button btnAccept, btnReject;

        public Holder(@NonNull View itemView) {
            super(itemView);

            txtOrderId = itemView.findViewById(R.id.txtOrderId);
            txtItemQty = itemView.findViewById(R.id.txtItemQty);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtPayMode = itemView.findViewById(R.id.txtPayMode);
            image = itemView.findViewById(R.id.image);
            btnAccept = itemView.findViewById(R.id.btnAccept);
//            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }


    //    METHOD TO SET DELIVERY ACCEPTANCE STATUS USING API
    private void processDeliveryAcceptance(String n, String id) {
        StringRequest request = new StringRequest(Request.Method.POST, Connection.deliveryApprovalUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("status").equals("success")) {
                                Intent intent = new Intent(context, MainActivity.class);
                                intent.putExtra("id", id);
                                context.startActivity(intent);
                            }
                            else {
                                Toast.makeText(context, "Status: "+json.getString("status"), Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e) {
                            Toast.makeText(context, "JSONException: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "VolleyError: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                SessionManager sessionManager = new SessionManager(context);
                int userId = sessionManager.getUserIdFromSession();
                map.put("userId", String.valueOf(userId));
                map.put("orderId", id);
                map.put("n", n);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

}
