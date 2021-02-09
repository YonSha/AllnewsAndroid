package edu.yonatan.allnews.sports_package.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.yonatan.allnews.R;
import edu.yonatan.allnews.activitys.CategoriesActivity;
import edu.yonatan.allnews.activitys.NoConnectionActivity;
import edu.yonatan.allnews.recyclers.rvRss;
import edu.yonatan.allnews.register_login_activitys.LoginActivity;
import edu.yonatan.allnews.settings.SettingsActivity;

import static edu.yonatan.allnews.fragments.WebView.inWebview;



public class SportsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    //props:
    private static final String YNET = "http://www.ynet.co.il/Integration/StoryRss3.xml";
    private static final String WALLA = "http://rss.walla.co.il/feed/3?type=main";
    private static final String MAKO = "http://rcs.mako.co.il/rss/87b50a2610f26110VgnVCM1000005201000aRCRD.xml";
    private static final String ONE = "https://www.one.co.il/cat/coop/xml/rss/newsfeed.aspx";

    private TextView sportsLabel;


    public static BottomNavigationView Sbnv;
    public static CircleImageView sportsProfileImgCiv;

    //fireBase Props:
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    //Sbnv - tab item selected:
    private int bnvTabNumber = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports);
        Toolbar toolbar = findViewById(R.id.sportsToolbar);
        setSupportActionBar(toolbar);

        //init the views:
        initViews();

        //nav setup -> listener
        Sbnv.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);


        //on app create(first init) -> load Ynet RSS
        getSupportFragmentManager().beginTransaction().
                replace(R.id.sportsContent, rvRss.newInstance(ONE)).
                commit();


        //firebase init
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();

        //sets the current activity title to non:
        this.setTitle("");




    }


    ///checks if the user is connected to the network, if so refresh btn works and sends the user back
    //into the categories class
    private void checkConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network

        }else{

            sendUserToNoConnectionActivity();



        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Sbnv.setVisibility(View.VISIBLE);
        //sets the profile img margins inside the webview:
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) sportsProfileImgCiv.getLayoutParams();
        marginParams.setMargins(410, 0, 0, 0);
        //minimize the app -> home button click
        if (!inWebview) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }

    //init the views:
    private void initViews() {
        Sbnv = findViewById(R.id.sports_navigation_menu);
        sportsLabel = findViewById(R.id.sportLabel);

        sportsProfileImgCiv = findViewById(R.id.civProfilePicSports);
    }

    //changing layout listener:
    // when landscape -> make the bottom nav bar disappear and profile Icone
    // when portrait -> back to default -> bottom nav bar appear and profile Icone
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            sportsProfileImgCiv.setVisibility(View.GONE);
            Sbnv.setVisibility(View.INVISIBLE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            sportsProfileImgCiv.setVisibility(View.VISIBLE);
            Sbnv.setVisibility(View.VISIBLE);
        }
    }


    //onStart checks if the user exists in the database:
    //if not -> to Login screen
    //if appear -> to main app layout:
    @Override
    protected void onStart() {
        super.onStart();



        inWebview = false;



        if (mAuth.getCurrentUser() == null) {
            sendUserToLoginActivity();

        }else{


            uploadProfileImageViewToCiv();
        }


    }


    //sends the user to login activity:
    private void sendUserToLoginActivity() {

        Intent loginActivity = new Intent(this, LoginActivity.class);
        startActivity(loginActivity);
        finish();

    }


    // //checks which tab is on -> refresh it if needed: onResume
    @Override
    protected void onResume() {
        super.onResume();

        ///checks if the user is connected to the network, if so refresh btn works and sends the user back
        //into the categories class
        checkConnectivity();

        if (Sbnv != null) {
            int selectedItem = Sbnv.getSelectedItemId();

            //Toast.makeText(this, selectedItem + "", Toast.LENGTH_SHORT).show();
            switch (bnvTabNumber) {

                case 5:
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.sportsContent, rvRss.newInstance(YNET)).
                            commit();


                    break;
                case 6:
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.sportsContent, rvRss.newInstance(MAKO)).
                            commit();

                    break;


                case 7:
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.sportsContent, rvRss.newInstance(WALLA)).
                            commit();

                    break;

                case 4:
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.sportsContent, rvRss.newInstance(ONE)).
                            commit();

                    break;


            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /// HAMBURGER  upper menu bar hamburger
    //misc and settings:
    //not yet operational
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.menuAbout) {
            Toast.makeText(this, "show About", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menuSetting) {
            sendUserToSettingsActivity();
            return true;

        } else if (id == R.id.menuLogout) {
            //singout + send user to login activity
            mAuth.signOut();
            sendUserToLoginActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //bottom nav item selection -> loads rss -> set title;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.sports_navigation_one:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fadein,R.anim.fadeout).
                        replace(R.id.sportsContent, rvRss.newInstance(ONE)).
                        commit();


                sportsLabel.setText("One");
                bnvTabNumber = 4;
                return true;
            case R.id.sports_navigation_ynet:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fadein,R.anim.fadeout).
                        replace(R.id.sportsContent, rvRss.newInstance(YNET)).
                        commit();


                sportsLabel.setText("Ynet");
                bnvTabNumber = 5;
                return true;

            case R.id.sports_navigation_mako:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fadein,R.anim.fadeout).
                        replace(R.id.sportsContent, rvRss.newInstance(MAKO)).
                        commit();


                sportsLabel.setText("Mako");
                bnvTabNumber = 6;
                return true;

            case R.id.sports_navigation_walla:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fadein,R.anim.fadeout).
                        replace(R.id.sportsContent, rvRss.newInstance(WALLA)).
                        commit();


                sportsLabel.setText("Walla");
                bnvTabNumber = 7;
                return true;


        }


        return true;
    }

    //sends the user to the Categories activity:
    private void sendUserToCategoriesActivity() {

        Intent categoriesActivity = new Intent(this, CategoriesActivity.class);
        startActivity(categoriesActivity);
        finish();

    }

    private void sendUserToSettingsActivity() {

        Intent settingsActivity = new Intent(this, SettingsActivity.class);
        startActivity(settingsActivity);
        finish();

    }

    //adds the user img pic if available to the toolbar:
    private void uploadProfileImageViewToCiv() {
        //adds the user img pic if available to the toolbar:
        mRef.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("image")) {

                    String profileImage = dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(profileImage).into(sportsProfileImgCiv);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //sends the user to noconnection activity
    private void sendUserToNoConnectionActivity() {
        Intent toNoConnectionActivity = new Intent(this, NoConnectionActivity.class);
        startActivity(toNoConnectionActivity);
        finish();


    }

}
