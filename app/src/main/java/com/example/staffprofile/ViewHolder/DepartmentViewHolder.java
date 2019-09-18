package com.example.staffprofile.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.staffprofile.Interface.ItemClickListener;
import com.example.staffprofile.R;

public class DepartmentViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {


    public TextView departmentName;
    public ImageView departmentImage;

    private ItemClickListener itemClickListener;

    public DepartmentViewHolder( View itemView) {
        super(itemView);

        departmentName = itemView.findViewById(R.id.department_name);
        departmentImage = itemView.findViewById(R.id.department_image);
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
