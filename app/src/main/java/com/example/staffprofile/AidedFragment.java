package com.example.staffprofile;

import android.content.Context;
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
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AidedFragment extends Fragment {


    View view;
    private RecyclerView aidedTeachingRecyclerView, aidedNonTeachingRecyclerView;
    private LinearLayoutManager linearLayoutManager;

    private DatabaseReference aidedTeachingDatabaseReference, aidedNonTeachingDatabaseReference;

    String DeptId = "";


    public AidedFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.aided, container, false);
        //Recycler View
        aidedTeachingRecyclerView = view.findViewById(R.id.aidedRecyclerViewTeaching);
        aidedTeachingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        aidedNonTeachingRecyclerView = view.findViewById(R.id.aidedRecyclerViewNonTeaching);
        aidedNonTeachingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //Firebase
        aidedTeachingDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Staff");
        aidedNonTeachingDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Staff");


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        aidedTeachingStaff();
        aidedNonTeachingStaff();

    }


    private void aidedTeachingStaff() {
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

        aidedTeachingRecyclerView.setAdapter(adapter);
        adapter.startListening();

    }


    private void aidedNonTeachingStaff() {
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

        aidedNonTeachingRecyclerView.setAdapter(adapter);
        adapter.startListening();

    }
}
