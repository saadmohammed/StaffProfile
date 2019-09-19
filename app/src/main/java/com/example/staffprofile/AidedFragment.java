package com.example.staffprofile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.staffprofile.Model.Staff;
import com.example.staffprofile.ViewHolder.StaffViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AidedFragment extends Fragment {

    View view;
    private RecyclerView aidedRecyclerView;
    private LinearLayoutManager linearLayoutManager;

    private DatabaseReference aidedDatabaseReference;

    public AidedFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.aided, container, false);
        //Recycler View
        aidedRecyclerView = view.findViewById(R.id.aidedRecyclerView);
        aidedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Firebase
        aidedDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Staff");


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = FirebaseDatabase.getInstance().getReference().child("Staff");
        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Staff>()
                .setQuery(query, Staff.class)
                .build();

        FirebaseRecyclerAdapter<Staff, StaffViewHolder> adapter =
                new FirebaseRecyclerAdapter<Staff, StaffViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull StaffViewHolder holder, int i, @NonNull Staff staff) {
                holder.staffName.setText(staff.getName());
                holder.staffPost.setText(staff.getPost());
                Picasso.get().load(staff.getImage()).into(holder.staffImage);
            }

            @NonNull
            @Override
            public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View staffView = LayoutInflater.from(parent.getContext()).inflate(R.layout.staff_list, parent, false);
                StaffViewHolder staffViewHolder = new StaffViewHolder(staffView);
                return staffViewHolder;
            }
        };

        aidedRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
