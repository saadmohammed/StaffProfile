package com.example.staffprofile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.staffprofile.Common.Common;
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
    private RecyclerView aidedTeachingRecyclerView;
    private LinearLayoutManager linearLayoutManager;

    private DatabaseReference aidedTeachingDatabaseReference;

    String DeptId = "";

    FirebaseRecyclerAdapter<Staff, StaffViewHolder> adapter;

    SharedPreferences sharedPreferences;

    public AidedFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.aided, container, false);

        //Recycler View
        aidedTeachingRecyclerView = view.findViewById(R.id.aidedRecyclerView);
        aidedTeachingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Firebase
        aidedTeachingDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Aided");

        //SharedPreference
        sharedPreferences =  getActivity().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (sharedPreferences.contains("DeptId")){
            DeptId = sharedPreferences.getString("DeptId","");
        }


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(!DeptId.isEmpty() && DeptId != null)
            if(Common.isConnectedToInternet(getActivity()))
                aidedTeachingStaff(DeptId);
            else
                Toast.makeText(getContext(),"Please Check Internet Connection", Toast.LENGTH_LONG).show();

        if(adapter == null) {
            if (Common.isConnectedToInternet(getContext()))
                adapter.startListening();
            else
                startActivity(new Intent(getContext(), RetryActivity.class));
        }

    }


    private void aidedTeachingStaff(String deptId) {
        Query query = aidedTeachingDatabaseReference.orderByChild("DeptId").equalTo(deptId);
        FirebaseRecyclerOptions<Staff> options =
                new FirebaseRecyclerOptions.Builder<Staff>()
                        .setQuery(query, Staff.class)
                        .build();

           adapter  = new FirebaseRecyclerAdapter<Staff, StaffViewHolder>(options) {
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



}
