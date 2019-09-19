package com.example.staffprofile.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.staffprofile.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StaffViewHolder extends RecyclerView.ViewHolder {

    public TextView staffName, staffPost;
    public ImageView staffImage;


    public StaffViewHolder(@NonNull View itemView) {
        super(itemView);

        staffName = itemView.findViewById(R.id.staff_name);
        staffPost = itemView.findViewById(R.id.staff_post);
        staffImage = itemView.findViewById(R.id.staff_image);
    }


}
