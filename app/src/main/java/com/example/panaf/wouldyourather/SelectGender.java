package com.example.panaf.wouldyourather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SelectGender extends AppCompatActivity {
    Context ctx=this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_gender);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
                try {
                    @SuppressLint({"NewApi", "LocalSuppress"}) GenerateRandomName gn = new GenerateRandomName(ctx,0);
                    System.out.println("final "+gn.getName());
                    SharedPreferences SP2 = getSharedPreferences("user",MODE_PRIVATE);
                    SharedPreferences.Editor SPE2 = SP2.edit();
                    SPE2.putString("username",gn.getName());
                    SPE2.apply();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                try {
                    @SuppressLint({"NewApi", "LocalSuppress"}) GenerateRandomName gn = new GenerateRandomName(ctx,1);
                    System.out.println("final "+gn.getName());
                    SharedPreferences SP2 = getSharedPreferences("user",MODE_PRIVATE);
                    SharedPreferences.Editor SPE2 = SP2.edit();
                    SPE2.putString("username",gn.getName());
                    SPE2.apply();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                try {
                    @SuppressLint({"NewApi", "LocalSuppress"}) GenerateRandomName gn = new GenerateRandomName(ctx,2);
                    System.out.println("final "+gn.getName());
                    SharedPreferences SP2 = getSharedPreferences("user",MODE_PRIVATE);
                    SharedPreferences.Editor SPE2 = SP2.edit();
                    SPE2.putString("username",gn.getName());
                    SPE2.apply();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startActivity(i);
                finish();
            }
        });

    }
}
