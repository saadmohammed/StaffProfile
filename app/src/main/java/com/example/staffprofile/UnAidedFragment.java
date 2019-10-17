package com.example.staffprofile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.staffprofile.Common.Common;
import com.example.staffprofile.Interface.ItemClickListener;
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

public class UnAidedFragment extends Fragment {

    View view;
    private RecyclerView unAidedTeachingRecyclerView;
    private LinearLayoutManager linearLayoutManager;

    private DatabaseReference unAidedTeachingDatabaseReference;

    String DeptId = "";

    FirebaseRecyclerAdapter<Staff, StaffViewHolder> adapter;

    SharedPreferences sharedPreferences, staffSharedPreferences;
    SharedPreferences.Editor staffId;



    public UnAidedFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.unaided, container, false);

        //Recycler View
        unAidedTeachingRecyclerView = view.findViewById(R.id.unAidedRecyclerView);
        unAidedTeachingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Firebase
        unAidedTeachingDatabaseReference = FirebaseDatabase.getInstance().getReference().child("UnAided");

        //SharedPreferenceFromMain
        sharedPreferences =  getActivity().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //SharedPreferenceToUnAidedStaffDEtail
        staffSharedPreferences =  getActivity().getSharedPreferences("MyPrefUnStaffId", 0);
        staffId = staffSharedPreferences.edit();


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
                unAidedTeachingStaff(DeptId);
            else
                Toast.makeText(getContext(),"Please Check Internet Connection", Toast.LENGTH_LONG).show();

        if(adapter == null) {
            if (Common.isConnectedToInternet(getContext()))
                adapter.startListening();
            else
                startActivity(new Intent(getContext(), RetryActivity.class));
        }
    }

    private void unAidedTeachingStaff(String deptId) {
        Query query = unAidedTeachingDatabaseReference.orderByChild("deptId").equalTo(deptId);        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Staff>()
                        .setQuery(query, Staff.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Staff, StaffViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull StaffViewHolder holder, int i, @NonNull Staff staff) {
                        holder.staffName.setText(staff.getName());
                        holder.staffPost.setText(staff.getPost());
                        Picasso.get().load(staff.getImage()).into(holder.staffImage);

                        final Staff clickCategoryItem = staff;
                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                if (Common.isConnectedToInternet(getActivity())) {
                                    staffId.putString("UnStaffId", adapter.getRef(position).getKey());
                                    staffId.commit();
                                    startActivity(new Intent(getContext(), UnAidedStaffDetail.class));
                                }
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View staffView = LayoutInflater.from(parent.getContext()).inflate(R.layout.staff_list, parent, false);
                        StaffViewHolder staffViewHolder = new StaffViewHolder(staffView);
                        return staffViewHolder;
                    }
                };


        unAidedTeachingRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }


}
