package com.example.staffprofile.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.staffprofile.Interface.ItemClickListener;
import com.example.staffprofile.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StaffViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView staffName, staffPost;
    public ImageView staffImage;

    private ItemClickListener itemClickListener;


    public StaffViewHolder(@NonNull View itemView) {
        super(itemView);

        staffName = itemView.findViewById(R.id.staff_name);
        staffPost = itemView.findViewById(R.id.staff_post);
        staffImage = itemView.findViewById(R.id.staff_image);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }


}
