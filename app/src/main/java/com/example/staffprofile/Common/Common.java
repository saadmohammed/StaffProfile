package com.example.staffprofile.Common;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.staffprofile.Model.User;

public class Common {
    //Current User
    public static User CURRENT_USER;



    //Internet Connection Checking
    public static boolean isConnectedToInternet(Context context){

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null){

            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info != null){

                for(int i=0;i<info.length;i++){

                    if(info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }


        }
        return false;
    }

}
