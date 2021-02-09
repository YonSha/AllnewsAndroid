package edu.yonatan.allnews.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;

import edu.yonatan.allnews.R;

public class NoConnectionActivity extends AppCompatActivity {




    private Button btnRefresh;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connection);

        //init views:
        initViews();


        btnRefresh.setOnClickListener(v->{


           ///checks if the user is connected to the network, if so refresh btn works and sends the user back
            //into the categories class
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                //we are connected to a network

                sendUserToCategoriesActivity();

            }






        });

    }







    //init views:
    private void initViews() {

        btnRefresh = findViewById(R.id.btnRefresh);


    }


//sends the user to categories activity;
    private void sendUserToCategoriesActivity(){

Intent toCategoriesActivity = new Intent(this,CategoriesActivity.class);
startActivity(toCategoriesActivity);
finish();

    };






}


