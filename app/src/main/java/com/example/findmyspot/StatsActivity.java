package com.example.findmyspot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StatsActivity extends AppCompatActivity {

    private Button btnToMainAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        btnToMainAct = findViewById(R.id.btnToMainActiv);
        btnToMainAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

                /*
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(myIntent);
                /**/

            }
        });

    }
}
