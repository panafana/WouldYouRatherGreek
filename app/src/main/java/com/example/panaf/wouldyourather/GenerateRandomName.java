package com.example.panaf.wouldyourather;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class GenerateRandomName {
    String finalname;

    @RequiresApi(api = Build.VERSION_CODES.O)
    GenerateRandomName(Context ctx) throws IOException {
        // Open the file
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> surnames = new ArrayList<String>();

        AssetManager assets = ctx.getAssets();
        InputStream is = assets.open("names.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String strLine;

        //Read File Line By Line
        while ((strLine = br.readLine()) != null)   {
            // Print the content on the console
            names.add(strLine);

        }
        //Close the input stream
        br.close();

        InputStream is2 = assets.open("surnames.txt");
        BufferedReader br2 = new BufferedReader(new InputStreamReader(is2));

        String strLine2;

        //Read File Line By Line
        while ((strLine2 = br2.readLine()) != null)   {
            // Print the content on the console
            surnames.add(strLine2);
            //System.out.println(strLine2);

        }
        //Close the input stream
        br2.close();

        final int min = 0;
        final int max = names.size();
        final int randomName = new Random().nextInt((max - min) + 1) + min;
        final int min2 = 0;
        final int max2 = surnames.size();
        final int random2Surname = new Random().nextInt((max2 - min2) + 1) + min2;

        String chosenName = names.get(randomName);
        String chosenSurname = surnames.get(random2Surname);

        chosenName= chosenName.substring(0,1).toUpperCase()+chosenName.substring(1).toLowerCase();
        chosenSurname = chosenSurname.substring(0,1)+chosenSurname.substring(1).toLowerCase();
        System.out.println("name "+chosenName);
        System.out.println("surname "+chosenSurname);
        finalname=chosenName+" "+chosenSurname;





    }
    String getName(){
        return this.finalname;
    }


}
