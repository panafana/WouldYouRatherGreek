package com.example.panaf.wouldyourather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class SelectGender extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_gender);
        Button male = findViewById(R.id.button);
        Button female = findViewById(R.id.button2);
        Button other = findViewById(R.id.button3);
        SharedPreferences SP = getSharedPreferences("gender",MODE_PRIVATE);
        final SharedPreferences.Editor SPE = SP.edit();


        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(SelectGender.this,MainActivity.class);
                SPE.putString("gender","male");
                SPE.apply();
                SPE.commit();
                startActivity(i);
                finish();
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(SelectGender.this,MainActivity.class);
                SPE.putString("gender","female");
                SPE.apply();
                SPE.commit();
                startActivity(i);
                finish();
            }
        });

        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(SelectGender.this,MainActivity.class);
                SPE.putString("gender","other");
                SPE.apply();
                SPE.commit();
                startActivity(i);
                finish();
            }
        });

    }
}
