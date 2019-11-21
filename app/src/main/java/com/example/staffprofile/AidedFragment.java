package com.example.staffprofile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.staffprofile.Common.Common;
import com.example.staffprofile.Interface.ItemClickListener;
import com.example.staffprofile.Model.Department;
import com.example.staffprofile.Model.Staff;
import com.example.staffprofile.ViewHolder.StaffViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AidedFragment extends Fragment {


    View view;
    private RecyclerView aidedTeachingRecyclerView;
    private LinearLayoutManager linearLayoutManager;

    private DatabaseReference aidedTeachingDatabaseReference;

    String DeptId = "";

    ProgressDialog dialog ;
    FirebaseRecyclerAdapter<Staff, StaffViewHolder> adapter;

    SharedPreferences sharedPreferences, staffSharedPreferences;
    SharedPreferences.Editor staffId;

    //Lock
    private ImageView lock;


    public AidedFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.aided, container, false);

        //Lock
        lock = view.findViewById(R.id.aidedLock);

        //Recycler View
        aidedTeachingRecyclerView = view.findViewById(R.id.aidedRecyclerView);
        aidedTeachingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Progress
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Please wait...");

        //Firebase
        aidedTeachingDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Aided");

        //SharedPreferenceFromMain
        sharedPreferences =  getActivity().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //SharedPreferenceToAidedStaffDetail
        staffSharedPreferences =  getActivity().getSharedPreferences("MyPrefStaffId", 0);
        staffId = staffSharedPreferences.edit();

        if (sharedPreferences.contains("DeptId")){
            DeptId = sharedPreferences.getString("DeptId","");
        }



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        String principal = Common.CURRENT_USER.getPassword();
        String men = Common.CURRENT_USER.getPassword();
        if(!DeptId.isEmpty() && DeptId != null) {
            if (Common.isConnectedToInternet(getActivity())) {
                if (men.equals("1111") || principal.equals("0000")) {
                    aidedTeachingStaff(DeptId);
                }
                else{
                    aidedTeachingRecyclerView.setVisibility(View.GONE);
                    lock.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(getContext(), "Please Check Internet Connection", Toast.LENGTH_LONG).show();
            }
        }
    }



    private void aidedTeachingStaff(String deptId) {

            Query query = aidedTeachingDatabaseReference.orderByChild("deptId").equalTo(deptId);

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
                    dialog.dismiss();
                    final Staff clickCategoryItem = staff;

                    holder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            if (Common.isConnectedToInternet(getActivity())) {
                                staffId.putString("StaffId", adapter.getRef(position).getKey());
                                staffId.commit();
                                startActivity(new Intent(getContext(), AidedStaffDetail.class));
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

        aidedTeachingRecyclerView.setAdapter(adapter);
        adapter.startListening();

    }
}
