package edu.yonatan.allnews.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import edu.yonatan.allnews.R;

public class SomethingWentWrong extends AppCompatActivity {


    private Button btnBackToMainPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_something_went_wrong);

        initViews();




        btnBackToMainPage.setOnClickListener(v->{



            sendUserToCategoriesActivity();
        });
    }

    private void initViews() {

        btnBackToMainPage = findViewById(R.id.btnBackToMainPage);
    }





    //sends the user to the Categories activity:
    private void sendUserToCategoriesActivity() {

        Intent categoriesActivity = new Intent(this, CategoriesActivity.class);
        startActivity(categoriesActivity);
        finish();

    }
}
