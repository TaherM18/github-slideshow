package com.example.fimsdelivery.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimsdelivery.MainActivity;
import com.example.fimsdelivery.R;
import com.example.fimsdelivery.global.Global;
import com.example.fimsdelivery.model.Order;

import java.util.ArrayList;

public class PendingOrdersAdapter extends RecyclerView.Adapter<PendingOrdersAdapter.Holder> {

    ArrayList<Order> orderList;
    Context context;

    public PendingOrdersAdapter(ArrayList<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.pending_order_list_item, parent, false);
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
        holder.txtPrice.setText("₹ "+orderList.get(position).getOrderPrice());
        holder.txtPayMode.setText(orderList.get(position).getPayMode());

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.orderId = holder.txtOrderId.getText().toString();
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("orderId", Global.orderId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView txtOrderId, txtItemQty, txtAddress, txtPrice, txtPayMode;
        ImageView image;

        public Holder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            txtOrderId = itemView.findViewById(R.id.txtOrderId);
            txtItemQty = itemView.findViewById(R.id.txtItemQty);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtPayMode = itemView.findViewById(R.id.txtPayMode);

        }
    }
}
