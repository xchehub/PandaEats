package com.erranddaddy.pandaeats.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.erranddaddy.pandaeats.R;
import com.erranddaddy.pandaeats.model.RestaurantModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.MyViewHolder> {
    private List<RestaurantModel> restaurantModelList;
    private RestaurantListClickListener clickListener;
    public RestaurantListAdapter(List<RestaurantModel> restaurantModelList, RestaurantListClickListener clickListener) {
        this.restaurantModelList = restaurantModelList;
        this.clickListener = clickListener;
    }

    public void updateData(List<RestaurantModel> restaurantModelList) {
        this.restaurantModelList = restaurantModelList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RestaurantListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantListAdapter.MyViewHolder holder, int position) {
        holder.restaurantName.setText(restaurantModelList.get(position).getName());
        holder.restaurantAddress.setText("Address: " + restaurantModelList.get(position).getAddress());
        holder.restaurantHours.setText("Today's hours: " + restaurantModelList.get(position).getHours().getTodayHours());

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                clickListener.onItemClick(restaurantModelList.get(position));
            }
        });
        Glide.with(holder.thumbImage)
                .load(restaurantModelList.get(position).getImage())
                .into(holder.thumbImage);

    }

    @Override
    public int getItemCount() {
        return restaurantModelList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView restaurantName;
        TextView restaurantAddress;
        TextView restaurantHours;
        ImageView thumbImage;

        public MyViewHolder(View view) {
            super(view);
            restaurantName = view.findViewById(R.id.restaurantName);
            restaurantAddress = view.findViewById(R.id.restaurantAddress);
            restaurantHours = view.findViewById(R.id.restaurantHours);
            thumbImage = view.findViewById(R.id.thumbImage);
        }
    }

    public interface RestaurantListClickListener {
        public void onItemClick(RestaurantModel restaurantModel);
    }
}
