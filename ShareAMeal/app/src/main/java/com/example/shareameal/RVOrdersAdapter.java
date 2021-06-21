package com.example.shareameal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RVOrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private ArrayList<Order> list = new ArrayList<>();
    private OnOrderClickListener onOrderClickListener;

    public RVOrdersAdapter(Context context, OnOrderClickListener onOrderClickListener) {
        this.context = context;
        this.onOrderClickListener = onOrderClickListener;
    }

    public void setItems(ArrayList<Order> orders) { list.addAll(orders); }

    public class OrderVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txt_food, txt_food_quantity, txt_date_time;
        public ImageView img_food;
        public OnOrderClickListener onOrderClickListener;

        public OrderVH(@NonNull @NotNull View itemView, OnOrderClickListener onOrderClickListener) {
            super(itemView);
            this.txt_date_time = itemView.findViewById(R.id.txt_date_time);
            this.txt_food = itemView.findViewById(R.id.txt_food);
            this.txt_food_quantity = itemView.findViewById(R.id.txt_food_quantity);
            this.img_food = itemView.findViewById(R.id.img_food);

            this.onOrderClickListener = onOrderClickListener;

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            onOrderClickListener.onOrderClick(getBindingAdapterPosition());
        }
    }
    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_order_item, parent, false);
        return new OrderVH(view, onOrderClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        OrderVH vh = (OrderVH) holder;

        Order order = list.get(position);

        vh.txt_food.setText(order.getFoodName());
        vh.txt_food_quantity.setText("Order quantity: " + order.getQuantity());
        vh.txt_date_time.setText("Order scheduled at " + order.getStartTime() + " - " + order.getEndTime() + " on " + order.getDate() + ".");

        if (order.getFoodImageURL() == null) {
            vh.img_food.setImageResource(R.drawable.dish);
        } else {
            if (order.getFoodImageURL().equals("null")) {
                vh.img_food.setImageResource(R.drawable.dish);
            } else {
                Picasso.get().load(order.getFoodImageURL()).into(vh.img_food);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnOrderClickListener {
        void onOrderClick(int position);
    }
}
